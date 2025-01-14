package server.poptato.todo.domain.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CompletedDateTimeRepositoryTest {
    @Autowired
    private CompletedDateTimeRepository completedDateTimeRepository;

    @DisplayName("캘린더 조회 시,userId가 1의 해당 월의 기록이 있는 날짜들을 조회한다")
    @Test
    void findCalendar_Success(){
        //given
        Long userId = 1L;
        String year = "2024";
        int month = 10;

        //when
        List<LocalDateTime> dateTimes = completedDateTimeRepository.findHistoryExistingDates(userId, year, month);

        //then
        assertThat(dateTimes).isNotNull();
        assertThat(dateTimes.size()).isEqualTo(6);
    }
}
