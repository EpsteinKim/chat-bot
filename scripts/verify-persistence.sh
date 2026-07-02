#!/usr/bin/env bash
set -euo pipefail

BASE="${BASE:-http://localhost:8081}"
PG_CONTAINER="${PG_CONTAINER:-chatbot-postgres}"

pg() { docker exec "$PG_CONTAINER" psql -U chatbot -d chatbot "$@"; }

echo "▶ 사전 점검"
if ! docker ps --format '{{.Names}}' | grep -q "^${PG_CONTAINER}\$"; then
  echo "  ✗ Postgres 컨테이너(${PG_CONTAINER})가 실행 중이 아닙니다. 먼저: docker compose up -d"
  exit 1
fi
code="$(curl -s -o /dev/null -w '%{http_code}' -X POST "$BASE/api/auth/login" -H 'Content-Type: application/json' -d '{}' || true)"
if [ "$code" = "000" ] || [ -z "$code" ]; then
  echo "  ✗ 앱이 실행 중이 아닙니다(${BASE}). 먼저: ./gradlew bootRun"
  exit 1
fi
echo "  ✓ Postgres 컨테이너 · 앱 모두 정상"

EMAIL="see-$(date +%s)@acme.com"
PASSWORD="password123"

echo
echo "▶ 1) 회원가입 요청  ($EMAIL)"
SIGNUP="$(curl -s -X POST "$BASE/api/auth/signup" -H 'Content-Type: application/json' \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\",\"name\":\"눈으로 확인\"}")"
USER_ID="$(printf '%s' "$SIGNUP" | python3 -c 'import sys,json;print(json.load(sys.stdin)["id"])')"
echo "  → user id: $USER_ID"

echo "▶ 2) 로그인 → 토큰 발급"
TOKEN="$(curl -s -X POST "$BASE/api/auth/login" -H 'Content-Type: application/json' \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}" \
  | python3 -c 'import sys,json;print(json.load(sys.stdin)["accessToken"])')"

echo "▶ 3) 대화 2건 생성 요청"
for q in "첫 번째 질문입니다" "두 번째 질문입니다"; do
  curl -s -o /dev/null -X POST "$BASE/api/chats" -H 'Content-Type: application/json' \
    -H "Authorization: Bearer $TOKEN" -d "{\"question\":\"$q\"}"
done

echo
echo "================= 실제 Postgres 테이블에 저장된 내용 ================="
echo "-- users (방금 가입한 사용자) --"
pg -c "SELECT id, email, name, role, created_at FROM users WHERE email = '$EMAIL';"
echo "-- threads (이 사용자의 스레드) --"
pg -c "SELECT id, user_id, created_at, updated_at FROM threads WHERE user_id = '$USER_ID';"
echo "-- chats (이 사용자의 대화) --"
pg -c "SELECT id, thread_id, question, model, created_at FROM chats WHERE user_id = '$USER_ID' ORDER BY created_at;"

echo
echo "✓ 위 행들은 실제 DB에 영속 저장되었습니다. 앱을 꺼도 남아 있으며, 아래로 언제든 다시 확인 가능합니다:"
echo "    docker exec -it $PG_CONTAINER psql -U chatbot -d chatbot -c \"SELECT email, name FROM users;\""
echo
echo "참고) JWT 블랙리스트는 H2 인메모리라 앱 실행 중에만 존재합니다. H2 콘솔에서 확인:"
echo "    http://localhost:8081/h2-console  (JDBC URL: jdbc:h2:mem:blacklist, user: sa)"
