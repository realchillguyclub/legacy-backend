package server.poptato.configuration;

import org.testcontainers.containers.MySQLContainer;

public final class DatabaseTestContainer {

    public static final MySQLContainer<?> INSTANCE =
            new MySQLContainer<>("mysql:8.0.36")
                    .withCommand("--default-time-zone=+09:00");

    static {
        INSTANCE.start();
    }

    private DatabaseTestContainer() {}
}
