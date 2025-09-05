package server.poptato.infra.discord.sender;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import server.poptato.infra.discord.client.DiscordWebhookClient; // 변경됨
import server.poptato.infra.discord.config.DiscordWebhookProperties; // 추가됨
import server.poptato.infra.discord.dto.DailyStats;
import server.poptato.infra.discord.dto.DiscordMessage;
import server.poptato.infra.discord.formatter.DiscordMessageFormatter;
import server.poptato.user.application.event.CreateUserCommentEvent;
import server.poptato.user.application.event.CreateUserEvent;
import server.poptato.user.application.event.DeleteUserEvent;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class DiscordSender {

	private final DiscordWebhookClient discordWebhookClient;
	private final DiscordWebhookProperties webhookProperties;

	// 유저 의견 전송 시
	@Retryable(retryFor = {Exception.class}, backoff = @Backoff(delay = 2000))
	public void sendCreateUserCommentMessage(CreateUserCommentEvent event) {
		String message = DiscordMessageFormatter.formatCreateUserCommentMessage(event);
		URI uri = URI.create(webhookProperties.getCreateUserCommentWebhookUrl());
		discordWebhookClient.sendMessage(uri, DiscordMessage.of(message));
	}

	// 유저 가입 시
	@Retryable(retryFor = {Exception.class}, backoff = @Backoff(delay = 2000))
	public void sendCreateUserMessage(CreateUserEvent event) {
		String message = DiscordMessageFormatter.formatCreateUserMessage(event);
		URI uri = URI.create(webhookProperties.getCreateUserWebhookUrl());
		discordWebhookClient.sendMessage(uri, DiscordMessage.of(message));
	}

	// 유저 탈퇴 시
	@Retryable(retryFor = {Exception.class}, backoff = @Backoff(delay = 2000))
	public void sendDeleteUserMessage(DeleteUserEvent event) {
		String message = DiscordMessageFormatter.formatDeleteUserMessage(event);
		URI uri = URI.create(webhookProperties.getDeleteUserWebhookUrl());
		discordWebhookClient.sendMessage(uri, DiscordMessage.of(message));
	}

	// 일일 통계 집계
	@Retryable(retryFor = {Exception.class}, backoff = @Backoff(delay = 2000))
	public void sendDailyStatsMessage(DailyStats stats) {
		String message = DiscordMessageFormatter.formatDailyStatsMessage(stats);
		URI uri = URI.create(webhookProperties.getDailyStatsWebhookUrl());
		discordWebhookClient.sendMessage(uri, DiscordMessage.of(message));
	}
}
