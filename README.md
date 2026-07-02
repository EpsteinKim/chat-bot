# chat-bot

AI 챗봇 서비스 백엔드 — Kotlin + Spring Boot.

## 기술 스택

- Kotlin 1.9 / JVM 21
- Spring Boot 3.4 (Web, WebFlux, Security, Data JPA, Validation)
- JWT 인증 (jjwt)
- PostgreSQL
- Flyway (스키마 마이그레이션 — DB를 코드로 관리)
- OpenAI 연동 (WebClient, 스트리밍 지원)

## 요구 환경

- JDK 21 (Gradle toolchain 이 자동으로 21 을 사용)
- Docker (PostgreSQL)

## 실행

```bash
docker compose up -d
./gradlew bootRun
```

- 앱: http://localhost:8081
- DB: postgres 16 (`localhost:5432`, db/user/pw 모두 `chatbot`)

## 환경 변수

| 변수 | 설명 | 기본값 |
|------|------|--------|
| `JWT_SECRET` | JWT 서명 키 (32바이트 이상) | 개발용 기본값 |
| `OPENAI_API_KEY` | OpenAI API 키 (없으면 목(mock) 응답으로 동작) | (없음) |
| `OPENAI_DEFAULT_MODEL` | 기본 모델 | `gpt-4o-mini` |
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | postgres 프로파일 접속 정보 | localhost |

> `OPENAI_API_KEY` 가 없으면 앱은 실행되며, 대화 생성 시 목(mock) 답변을 반환합니다.

## DB 마이그레이션 (Flyway)

스키마는 Hibernate 자동 DDL이 아니라 Flyway가 소유합니다. Hibernate는 `validate`로 검증만 합니다.

- `src/main/resources/db/migration/main/V1__init.sql` (users, threads, chats)
- `V2__feedback_and_activity.sql` (feedbacks, activity_logs)
- `V3__indexes.sql` (조회 패턴별 복합 인덱스)

스키마 변경 시 새 파일 `V4__xxx.sql` 을 추가하면 부팅 시 순서대로 적용됩니다. 적용 이력은 `flyway_schema_history` 테이블에 기록됩니다.

## 빌드 / 테스트

```bash
./gradlew build      # 전체 빌드 + 테스트
./gradlew test       # 테스트만
```

## 기능 (요구사항)

- 사용자 관리 및 인증 (회원가입, 로그인, JWT 인증, member/admin 권한)
- 대화(chat) 관리 (스레드 자동 그룹화, 스트리밍, 목록 조회, 스레드 삭제)
- 사용자 피드백 관리 (생성, 목록/필터, 상태 변경)
- 분석 및 보고 (사용자 활동 집계, CSV 보고서 — 관리자 전용)

구현 상세는 각 도메인 패키지 참고.

## 회고

### 분석

요구사항을 인증 / 대화 / 피드백 / 분석 4개 도메인으로 나눴고, 시연의 핵심 목표는 "API로 AI를 활용할 수 있다" 이 하나라고 봤습니다. 담당자와 요건 조율이 어려운 상황이라 합리적으로 가정하고 MVP를 만든다는 관점으로 진행했습니다. 대신 "지속 확장 가능"이 가장 중요한 요건이라 판단해 두 가지에 집중했습니다.

- 이후에 대외비 문서 학습(RAG)까지 확장할 수 있도록 H2 대신 postgre를 선택했습니다(pgvector로 확장 가능). LLM 호출부에도 참조 문서를 주입할 자리(`context`)를 미리 마련해뒀습니다.
- 구현 양보다 설계 결정이 중요하다고 보고 트랜잭션 경계 / 논블로킹 / 마이그레이션 / 인덱스 / 페이지네이션에 시간을 배분했습니다. 클린 아키텍처(포트·어댑터)로 LLM과 저장소를 추상화해 provider 교체나 기능 추가가 도메인 변경 없이 가능하도록 했습니다.

### AI를 어떻게 활용했나 / 어려웠던 점

시간이 촉박해 역할을 나눴습니다. 아키텍처, 기술 선택, 비즈니스 로직, 논블로킹·트랜잭션 설계 같은 결정은 전부 제가 하고, AI는 그 결정을 빠르게 구현하는 도구로 사용했습니다. 대신 생성된 코드를 그대로 신뢰하지 않고 컴파일 → 단위 테스트 → 통합 테스트(Testcontainers) → 실제 앱 e2e 순으로 한 번 더 검수하며 오류가 있으면 수정했습니다.

어려웠던 점은 "그럴듯하지만 틀린" 코드를 걸러내는 일이었습니다. 그래서 포트(인터페이스)를 먼저 정의해 AI가 벗어날 범위를 좁히고, 신뢰는 코드가 아니라 테스트와 실측에서 얻는 방식으로 진행했습니다. 배점상 구현 양은 낮으므로 기능을 더 붙이기보다 품질 쪽으로 방향을 잡았습니다.

### 가장 어려웠던 기능

솔직히 AI를 활용하다 보니 "구현이 어려운" 기능은 없었습니다. 난이도는 구현이 아니라 설계 판단으로 옮겨갔고, 그중 가장 까다로웠던 것은 논블로킹 전환과 트랜잭션 경계 분리였습니다.

JPA는 블로킹이고 `@Transactional`은 스레드에 묶이는데, 수 초가 걸리는 LLM 호출을 트랜잭션 안에 두면 DB 커넥션을 그 시간 내내 점유합니다. 그래서 대화 생성을 `prepare(트랜잭션) → LLM(트랜잭션 없음) → persist(트랜잭션)` 3구간으로 분리하고, 컨트롤러와 유스케이스를 `suspend`로 전환했습니다. `@Transactional` 프록시가 private·self-invocation·suspend와 맞지 않아 `TransactionTemplate`로 경계를 직접 잡았습니다.

여기서 비자명한 버그를 하나 만났는데, `suspend` 컨트롤러의 async 디스패치에 Spring Security 컨텍스트가 전파되지 않아 인증된 요청이 401이 나는 문제였습니다. 필터가 async 디스패치에서도 토큰으로 재인증하도록(`shouldNotFilterAsyncDispatch=false`) 수정해 해결했습니다. 이번 과제에서 판단이 가장 어려웠던 지점입니다.
