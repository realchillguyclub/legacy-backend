package server.poptato.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
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
public abstract class RepositoryTestConfig {

    @Autowired
    protected TestEntityManager tem;

    @Container
    @ServiceConnection // ← 이 한 줄로 spring.datasource.* 바인딩 자동화
    protected static final MySQLContainer<?> MYSQL =
            new MySQLContainer<>("mysql:8.0.36")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test")
                    .withEnv("TZ", "Asia/Seoul")
                    .withCommand("--character-set-server=utf8mb4", "--collation-server=utf8mb4_general_ci")
                    .withReuse(true);

    // 공통 유틸 필요시 protected 헬퍼 메서드 추가
}
