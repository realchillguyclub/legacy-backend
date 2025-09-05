package server.poptato.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * 모든 레포지토리 테스트가 상속하는 베이스.
 * - Testcontainers MySQL을 클래스 단위로 1회만 띄움(static)
 * - @DataJpaTest 슬라이스 환경
 * - 내장 DB 대체 방지(실제 MySQL 사용)
 */
@DataJpaTest
@Testcontainers
@ActiveProfiles("unittest")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class DatabaseTestConfig {

    @Autowired
    protected TestEntityManager tem;

    @DynamicPropertySource
    static void mysqlProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DatabaseTestContainer.INSTANCE::getJdbcUrl);
        registry.add("spring.datasource.username", DatabaseTestContainer.INSTANCE::getUsername);
        registry.add("spring.datasource.password", DatabaseTestContainer.INSTANCE::getPassword);
    }
}
