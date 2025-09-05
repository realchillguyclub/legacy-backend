package server.poptato.infra.discord.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "discord")
public class DiscordWebhookProperties {
    private String createUserCommentWebhookUrl;
    private String createUserWebhookUrl;
    private String deleteUserWebhookUrl;
    private String dailyStatsWebhookUrl;
}
