package server.poptato.external.discord.application.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import server.poptato.external.discord.dto.DailyStats;
import server.poptato.todo.domain.value.TodayStatus;
import server.poptato.todo.domain.value.Type;
import server.poptato.user.domain.value.MobileType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class DailyStatsService {

	@PersistenceContext
	private final EntityManager em;

	@Transactional(readOnly = true)
	public DailyStats buildStatsFor(LocalDate date) {
		LocalDateTime start = date.atStartOfDay();
		LocalDateTime end = date.atTime(LocalTime.MAX);

		long signupsTotal = countSignups(start, end);
		long signupsIos = countSignupsByOs(start, end, MobileType.IOS);
		long signupsAndroid = countSignupsByOs(start, end, MobileType.ANDROID);

		long todosCreated = countTodosCreated(start, end);

		long todayTotal = countTodayTodos(date);
		long todayCompleted = countTodayCompletedByCompletionLogs(date, start, end);
		double todayCompletedRate = rate(todayCompleted, todayTotal);

		long newUsersWhoCompleted = countNewUsersWhoCompleted(date, start, end);
		double newUsersWhoCompletedRate = rate(newUsersWhoCompleted, signupsTotal);

		return DailyStats.of(
			date.toString(),
			signupsTotal,
			signupsIos,
			signupsAndroid,
			todosCreated,
			todayTotal,
			todayCompleted,
			todayCompletedRate,
			newUsersWhoCompleted,
			newUsersWhoCompletedRate
		);
	}

	// rate 계산 내부 메서드
	private double rate(long numerator, long denominator) {
		if (denominator <= 0) return 0.0;
		return (numerator * 100.0) / denominator;
	}

	// 오늘 신규 가입자 수 집계
	private long countSignups(LocalDateTime start, LocalDateTime end) {
		String q = """
                select count(u.id)
                from User u
                where u.createDate between :start and :end
                """;
		TypedQuery<Long> query = em.createQuery(q, Long.class);
		query.setParameter("start", start);
		query.setParameter("end", end);
		return query.getSingleResult();
	}

	// iOS or Android 신규 가입자 수 집계
	private long countSignupsByOs(LocalDateTime start, LocalDateTime end, MobileType os) {
		String q = """
                select count(distinct u.id)
                from User u
                where u.createDate between :start and :end
                  and exists (
                      select 1
                      from Mobile m
                      where m.userId = u.id
                        and m.type = :os
                        and m.createDate between :start and :end
                  )
                """;
		TypedQuery<Long> query = em.createQuery(q, Long.class);
		query.setParameter("start", start);
		query.setParameter("end", end);
		query.setParameter("os", os);
		return query.getSingleResult();
	}

	// 오늘 생성된 총 할 일 개수 집계
	private long countTodosCreated(LocalDateTime start, LocalDateTime end) {
		String q = """
                select count(t.id)
                from Todo t
                where t.createDate between :start and :end
                """;
		TypedQuery<Long> query = em.createQuery(q, Long.class);
		query.setParameter("start", start);
		query.setParameter("end", end);
		return query.getSingleResult();
	}

	// '오늘' 페이지에 옮겨져 있는 개수 집계
	private long countTodayTodos(LocalDate date) {
		String q = """
                select count(t.id)
                from Todo t
                where t.type = :type
                  and t.todayDate = :date
                """;
		TypedQuery<Long> query = em.createQuery(q, Long.class);
		query.setParameter("type", Type.TODAY);
		query.setParameter("date", date);
		return query.getSingleResult();
	}

	// '오늘' 페이지에서 완료된 할 일 개수 집계
	private long countTodayCompletedByCompletionLogs(LocalDate date, LocalDateTime start, LocalDateTime end) {
		String q = """
                select count(distinct t.id)
                from CompletedDateTime c
                join Todo t on t.id = c.todoId
                where t.todayDate = :date
                  and c.createDate between :start and :end
                """;
		TypedQuery<Long> query = em.createQuery(q, Long.class);
		query.setParameter("date", date);
		query.setParameter("start", start);
		query.setParameter("end", end);
		return query.getSingleResult();
	}

	// 오늘 신규 가입자 중, 할 일을 완료한 명수 집계
	private long countNewUsersWhoCompleted(LocalDate date, LocalDateTime start, LocalDateTime end) {
		String q = """
                select count(distinct t.userId)
                from Todo t
                where t.type = :type
                  and t.todayDate = :date
                  and t.todayStatus = :status
                  and t.userId in (
                      select u.id
                      from User u
                      where u.createDate between :start and :end
                  )
                """;
		TypedQuery<Long> query = em.createQuery(q, Long.class);
		query.setParameter("type", Type.TODAY);
		query.setParameter("date", date);
		query.setParameter("status", TodayStatus.COMPLETED);
		query.setParameter("start", start);
		query.setParameter("end", end);
		return query.getSingleResult();
	}
}
