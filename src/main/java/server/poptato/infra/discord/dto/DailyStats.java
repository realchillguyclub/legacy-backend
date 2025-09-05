package server.poptato.infra.discord.dto;

public record DailyStats(
	String targetDate,              // YYYY-MM-DD
	long signupsTotal,
	long signupsIos,
	long signupsAndroid,
	long todosCreated,
	long todayTotal,
	long todayCompleted,
	double todayCompletedRate      // 0.0 ~ 100.0
) {
	public static DailyStats of(
		String targetDate,
		long signupsTotal,
		long signupsIos,
		long signupsAndroid,
		long todosCreated,
		long todayTotal,
		long todayCompleted,
		double todayCompletedRate
	) {
		return new DailyStats(
			targetDate,
			signupsTotal,
			signupsIos,
			signupsAndroid,
			todosCreated,
			todayTotal,
			todayCompleted,
			todayCompletedRate
		);
	}
}
