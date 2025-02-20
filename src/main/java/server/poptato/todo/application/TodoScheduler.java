package server.poptato.todo.application;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import server.poptato.external.firebase.service.FCMService;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.repository.TodoRepository;
import server.poptato.todo.domain.value.TodayStatus;
import server.poptato.todo.domain.value.Type;
import server.poptato.user.domain.entity.Mobile;
import server.poptato.user.domain.entity.User;
import server.poptato.user.domain.repository.MobileRepository;
import server.poptato.user.domain.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoScheduler {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final MobileRepository mobileRepository;
    private final FCMService fcmService;
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 매일 자정에 할 일의 상태(Type)를 업데이트하고 마감 알림을 전송합니다.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateTodoType() {
        List<Long> updatedTodoIds = new ArrayList<>();
        updateTodo(updatedTodoIds);
        sendDeadlineNotifications();
        deleteOldFcmTokens();
    }

    /**
     * 할 일의 상태(Type)를 업데이트합니다.
     *
     * @param updatedTodoIds 업데이트된 할 일의 ID 목록
     */
    @Async
    public void updateTodo(List<Long> updatedTodoIds) {
        Map<Long, Integer> userIdToStartingOrder = new HashMap<>();
        Map<Long, List<Todo>> userIdAndTodaysMap = updateTodays(updatedTodoIds, userIdToStartingOrder);
        List<Todo> yesterdayTodos = updateYesterdays(updatedTodoIds, userIdToStartingOrder);
        save(userIdAndTodaysMap, yesterdayTodos);
        updateDeadlineTodo();
    }

    /**
     * 오늘(TODAY) 상태의 할 일을 업데이트합니다.
     *
     * @param updatedTodoIds 업데이트된 할 일의 ID 목록
     * @param userIdToStartingOrder 사용자 ID별 시작 순서 맵
     * @return 사용자 ID별 오늘의 할 일 목록
     */
    private Map<Long, List<Todo>> updateTodays(List<Long> updatedTodoIds, Map<Long, Integer> userIdToStartingOrder) {
        Map<Long, List<Todo>> userIdAndTodaysMap = todoRepository.findByType(Type.TODAY)
                .stream()
                .collect(Collectors.groupingBy(Todo::getUserId));

        userIdAndTodaysMap.forEach((userId, todos) -> {
            userIdToStartingOrder.putIfAbsent(userId, todoRepository.findMaxBacklogOrderByUserIdOrZero(userId) + 1);
            int startingOrder = userIdToStartingOrder.get(userId);

            for (Todo todo : todos) {
                if (todo.getTodayStatus() == TodayStatus.COMPLETED && todo.isRepeat()) {
                    todo.setType(Type.BACKLOG);
                    todo.setTodayStatus(null);
                    todo.setTodayOrder(null);
                    todo.setBacklogOrder(startingOrder++);
                    updatedTodoIds.add(todo.getId());
                    continue;
                }

                if (todo.getTodayStatus() == TodayStatus.INCOMPLETE) {
                    todo.setType(Type.YESTERDAY);
                    todo.setTodayOrder(null);
                    todo.setBacklogOrder(startingOrder++);
                    updatedTodoIds.add(todo.getId());
                }
            }

            userIdToStartingOrder.put(userId, startingOrder);
        });
        return userIdAndTodaysMap;
    }

    /**
     * 어제(YESTERDAY) 상태의 할 일을 업데이트합니다.
     *
     * @param updatedTodoIds 업데이트된 할 일의 ID 목록
     * @param userIdToStartingOrder 사용자 ID별 시작 순서 맵
     * @return 업데이트된 어제의 할 일 목록
     */
    private List<Todo> updateYesterdays(List<Long> updatedTodoIds, Map<Long, Integer> userIdToStartingOrder) {
        return todoRepository.findByType(Type.YESTERDAY)
                .stream()
                /* 25.02.20 : 미사용으로 인한 주석 처리
                .filter(todo -> !updatedTodoIds.contains(todo.getId()))
                */
                .peek(todo -> {
                    Long userId = todo.getUserId();

                    userIdToStartingOrder.putIfAbsent(userId, todoRepository.findMaxBacklogOrderByUserIdOrZero(userId) + 1);
                    int startingOrder = userIdToStartingOrder.get(userId);

                    if (todo.getTodayStatus() == TodayStatus.INCOMPLETE) {
                        todo.setType(Type.BACKLOG);
                        todo.setTodayStatus(null);
                        todo.setBacklogOrder(startingOrder++);
                    } else if (todo.getTodayStatus() == TodayStatus.COMPLETED && todo.isRepeat()) {
                        todo.setType(Type.BACKLOG);
                        todo.setTodayStatus(null);
                        todo.setBacklogOrder(startingOrder++);
                    }

                    userIdToStartingOrder.put(userId, startingOrder);
                })
                .collect(Collectors.toList());
    }

    /**
     * 업데이트된 할 일을 저장합니다.
     *
     * @param userIdAndTodaysMap 사용자 ID별 오늘의 할 일 목록
     * @param yesterdayTodos 업데이트된 어제의 할 일 목록
     */
    private void save(Map<Long, List<Todo>> userIdAndTodaysMap, List<Todo> yesterdayTodos) {
        for (Todo todo : userIdAndTodaysMap.values().stream().flatMap(List::stream).toList()) {
            todoRepository.save(todo);
        }
        for (Todo todo : yesterdayTodos) {
            todoRepository.save(todo);
        }
    }

    /**
     * 마감기한 날짜가 된 할 일을 오늘 할 일로 옮깁니다.
     *
     */
    private void updateDeadlineTodo() {
        List<Long> userIds = userRepository.findAllUserIds();
        updateBacklogTodosToTodayWithBatch(userIds);
    }

    /**
     * 백로그를 오늘 할 일로 옮기는 배치작업을 진행합니다.
     * @param userIds 전체 사용자 ID
     *
     */
    @Transactional
    protected void updateBacklogTodosToTodayWithBatch(List<Long> userIds) {
        LocalDate today = LocalDate.now();
        int batchSize = 50;
        List<List<Long>> userBatches = splitListIntoBatches(userIds, batchSize);
        for (List<Long> batch : userBatches) {
            todoRepository.updateBacklogTodosToToday(today, batch, 0);
            entityManager.flush();
            entityManager.clear();
        }
    }

    /**
     * 유저 아이디 리스트를 배치 사이즈로 나누는 작업을 합니다.
     * @param userIds 전체 사용자 ID
     * @param batchSize 배치사이즈
     */
    private List<List<Long>> splitListIntoBatches(List<Long> userIds, int batchSize) {
        List<List<Long>> partitions = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i += batchSize) {
            partitions.add(userIds.subList(i, Math.min(i + batchSize, userIds.size())));
        }
        return partitions;
    }

    /**
     * 마감 알림을 전송합니다.
     */
    @Async
    public void sendDeadlineNotifications() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (Boolean.TRUE.equals(user.getIsPushAlarm())) {
                List<Todo> todosDueToday = todoRepository.findTodosDueToday(user.getId(), LocalDate.now());
                sendFcmMessage(user, todosDueToday);
            }
        }
    }

    /**
     * FCM 메시지를 전송합니다.
     *
     * @param user 알림을 받을 사용자
     * @param todosDueToday 오늘 마감 예정인 할 일 목록
     */
    private void sendFcmMessage(User user, List<Todo> todosDueToday) {
        if (!todosDueToday.isEmpty()) {
            List<Mobile> mobiles = mobileRepository.findAllByUserId(user.getId());
            for (Mobile mobile : mobiles) {
                for (Todo todo : todosDueToday) {
                    String todoContent = formatTodoContent(todo);
                    sendPushNotificationOrDeleteFcmToken(mobile.getClientId(), todoContent);
                }
            }
        }
    }

    private void sendPushNotificationOrDeleteFcmToken(String clientId, String todoContent) {
        try {
            fcmService.sendPushNotification(
                    clientId,
                    "오늘 마감 예정인 할 일",
                    todoContent
            );
        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode().equals(MessagingErrorCode.INVALID_ARGUMENT)) {
                // 토큰이 유효하지 않은 경우
                mobileRepository.deleteByClientId(clientId);
            } else if (e.getMessagingErrorCode().equals(MessagingErrorCode.UNREGISTERED)) {
                // 재발급된 이전 토큰인 경우
                mobileRepository.deleteByClientId(clientId);
            }
            else {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 비활성 fcm토큰을 삭제하는 메서드.
     * timestamp를 갱신한지 한달이 지난 fmc토큰을 삭제한다.
     */
    private void deleteOldFcmTokens() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        mobileRepository.deleteOldTokens(oneMonthAgo);
    }

    /**
     * 할 일 내용을 포맷합니다.
     *
     * @param todo 할 일 객체
     * @return 포맷된 할 일 내용
     */
    private String formatTodoContent(Todo todo) {
        return todo.getContent();
    }
}
