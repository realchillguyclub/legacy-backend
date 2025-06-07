package server.poptato.todo.api.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalTime;

public record EventCreateRequestDto(
        @NotBlank(message = "푸쉬 알람 제목은 필수값입니다.")
        String pushAlarmTitle,
        @NotBlank(message = "푸쉬 알람 내용은 필수값입니다.")
        String pushAlarmContent,
        boolean isCreateTodayTodo,
        boolean isBookmarked,
        String todoContent,
        LocalTime todoTime
){
}
