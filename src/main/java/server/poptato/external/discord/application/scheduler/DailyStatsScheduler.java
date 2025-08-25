package server.poptato.external.discord.application.scheduler;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import server.poptato.external.discord.application.service.DailyStatsService;
import server.poptato.external.discord.dto.DailyStats;
import server.poptato.external.discord.sender.DiscordSender;

import java.time.LocalDate;
import java.time.ZoneId;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class DailyStatsScheduler {

    private final DailyStatsService dailyStatsService;
    private final DiscordSender discordSender;

	/**
	 * 일일 통계를 집계하고 디스코드로 전송한다.
	 */
	@Scheduled(cron = "${scheduling.dailyStatsCron}")
    public void sendYesterdayStats() {
        LocalDate target = LocalDate.now(ZoneId.of("Asia/Seoul"));
        DailyStats stats = dailyStatsService.buildStatsFor(target);
        discordSender.sendDailyStatsMessage(stats);
    }
}
