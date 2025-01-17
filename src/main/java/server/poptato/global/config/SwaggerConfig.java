package server.poptato.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Legacy Illdan API Documentation")
                        .version("1.0.0")
                        .description("Spring REST Docs with Swagger UI.")
                        .contact(new Contact()
                        .name("Sangho Han")
                        .url("https://github.com/bbbang105")
                        .email("hchsa77@gmail.com"))
                )
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("로컬 서버"),
                        new Server().url("https://prev-illdan.store").description("레거시 배포 서버")
                ));

        // REST Docs에서 생성한 open-api-3.0.1.json 파일 읽어오기
        try {
            ClassPathResource resource = new ClassPathResource("static/docs/open-api-3.0.1.json");
            InputStream inputStream = resource.getInputStream();
            ObjectMapper mapper = new ObjectMapper();

            // open-api-3.0.1.json 파일을 OpenAPI 객체로 매핑
            OpenAPI restDocsOpenAPI = mapper.readValue(inputStream, OpenAPI.class);

            // REST Docs에서 생성한 Paths 정보 병합
            Paths paths = restDocsOpenAPI.getPaths();
            openAPI.setPaths(paths);

            openAPI.components(restDocsOpenAPI.getComponents());

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load Swagger configuration from REST Docs JSON.", e);
        }

        // 액세스 토큰
        SecurityScheme apiKey = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Token");

        openAPI.components(new Components().addSecuritySchemes("Bearer Token", apiKey))
                .addSecurityItem(securityRequirement);

        return openAPI;
    }
}
