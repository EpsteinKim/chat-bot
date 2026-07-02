package com.example.chatbot.config

import jakarta.persistence.EntityManagerFactory
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.example.chatbot.adapter.out.persistence"],
    entityManagerFactoryRef = "mainEntityManagerFactory",
    transactionManagerRef = "mainTransactionManager",
)
class MainDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.main")
    fun mainDataSourceProperties(): DataSourceProperties = DataSourceProperties()

    @Bean
    @Primary
    fun mainDataSource(
        @Qualifier("mainDataSourceProperties") properties: DataSourceProperties,
    ): DataSource = properties.initializeDataSourceBuilder().build()

    @Bean(initMethod = "migrate")
    fun mainFlyway(@Qualifier("mainDataSource") dataSource: DataSource): Flyway =
        Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration/main")
            .baselineOnMigrate(true)
            .load()

    @Bean
    @Primary
    @DependsOn("mainFlyway")
    fun mainEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
        @Qualifier("mainDataSource") dataSource: DataSource,
    ): LocalContainerEntityManagerFactoryBean =
        builder
            .dataSource(dataSource)
            .packages("com.example.chatbot.adapter.out.persistence")
            .persistenceUnit("main")
            .properties(
                mapOf(
                    "hibernate.hbm2ddl.auto" to "validate",
                    "hibernate.format_sql" to "true",
                    "hibernate.show_sql" to "true",
                ),
            )
            .build()

    @Bean
    @Primary
    fun mainTransactionManager(
        @Qualifier("mainEntityManagerFactory") entityManagerFactory: EntityManagerFactory,
    ): PlatformTransactionManager = JpaTransactionManager(entityManagerFactory)
}
