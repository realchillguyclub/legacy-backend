package server.poptato.external.discord.sender;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import server.poptato.external.discord.client.DiscordCreateUserCommentWebhookClient;
import server.poptato.external.discord.client.DiscordCreateUserWebhookClient;
import server.poptato.external.discord.client.DiscordDailyStatsWebhookClient;
import server.poptato.external.discord.client.DiscordDeleteUserWebhookClient;
import server.poptato.external.discord.dto.DailyStats;
import server.poptato.external.discord.dto.DiscordMessage;
import server.poptato.external.discord.formatter.DiscordMessageFormatter;
import server.poptato.user.application.event.CreateUserCommentEvent;
import server.poptato.user.application.event.CreateUserEvent;
import server.poptato.user.application.event.DeleteUserEvent;

@Component
@RequiredArgsConstructor
public class DiscordSender {

    private final DiscordCreateUserCommentWebhookClient discordCreateUserCommentWebhookClient;
    private final DiscordCreateUserWebhookClient discordCreateUserWebhookClient;
    private final DiscordDeleteUserWebhookClient discordDeleteUserWebhookClient;
	private final DiscordDailyStatsWebhookClient discordDailyStatsWebhookClient;

	@Retryable(
            retryFor = { Exception.class },
            backoff = @Backoff(delay = 2000)
    )
    public void sendCreateUserCommentMessage(CreateUserCommentEvent event) {
        String message = DiscordMessageFormatter.formatCreateUserCommentMessage(event);
        discordCreateUserCommentWebhookClient.sendMessage(DiscordMessage.of(message));
    }

    @Retryable(
            retryFor = { Exception.class },
            backoff = @Backoff(delay = 2000)
    )
    public void sendCreateUserMessage(CreateUserEvent event) {
        String message = DiscordMessageFormatter.formatCreateUserMessage(event);
        discordCreateUserWebhookClient.sendMessage(DiscordMessage.of(message));
    }

    @Retryable(
            retryFor = { Exception.class },
            backoff = @Backoff(delay = 2000)
    )
    public void sendDeleteUserMessage(DeleteUserEvent event) {
        String message = DiscordMessageFormatter.formatDeleteUserMessage(event);
        discordDeleteUserWebhookClient.sendMessage(DiscordMessage.of(message));
    }

	@Retryable(
		retryFor = { Exception.class },
		backoff = @Backoff(delay = 2000)
	)
	public void sendDailyStatsMessage(DailyStats stats) {
		String message = DiscordMessageFormatter.formatDailyStatsMessage(stats);
		discordDailyStatsWebhookClient.sendMessage(DiscordMessage.of(message));
	}
}
