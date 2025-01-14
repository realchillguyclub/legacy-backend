package server.poptato.todo.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import server.poptato.todo.application.response.TodayListResponseDto;
import server.poptato.todo.application.response.TodayResponseDto;
import server.poptato.todo.domain.repository.TodoRepository;
import server.poptato.todo.domain.value.TodayStatus;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TodoTodayServiceTest {
    @Autowired
    private TodoTodayService todoTodayService;
    @Autowired
    private TodoRepository todoRepository;

    @DisplayName("투데이 목록 조회 시, size=8을 요청하면 미달성 투데이 데이터 먼저 8개 응답된다.")
    @Test
    void getTodayList_Success(){
        //given
        Long userId = 1L;
        int page = 0;
        int size = 8;
        LocalDate todayDate = LocalDate.of(2024,10,16);

        //when
        List<TodayResponseDto> todayList = todoTodayService.getTodayList(userId, page, size, todayDate).getTodays();

        //then
        assertThat(todayList.size()).isEqualTo(size);
        assertThat(todayList)
                .allMatch(today -> today.getTodayStatus() == TodayStatus.INCOMPLETE);
    }

    @DisplayName("투데이 목록 조회 시, 유효하지 않는 페이지 수일 경우 빈 리스트를 반환한다.")
    @Test
    void getTodayList_NotValidPage_Success(){
        //given
        Long userId = 1L;
        int page = 2;
        int size = 8;
        LocalDate todayDate = LocalDate.of(2024,10,16);

        //when
        TodayListResponseDto todayList = todoTodayService.getTodayList(userId, page, size, todayDate);

        //then
        assertThat(todayList.getTodays()).isEmpty();
    }

    @DisplayName("투데이 목록 조회 시, 데이터가 없는 경우 빈 리스트를 반환한다.")
    @Test
    void getTodayList_EmptyToday_Success(){
        //given
        Long userId = 50L;
        int page = 0;
        int size = 8;
        LocalDate todayDate = LocalDate.now();

        //when
        TodayListResponseDto todayList = todoTodayService.getTodayList(userId, page, size, todayDate);

        //then
        assertThat(todayList.getTodays()).isEmpty();
        assertThat(todayList.getTotalPageCount()).isEqualTo(0);
    }
}