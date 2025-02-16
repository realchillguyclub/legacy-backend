package server.poptato.todo.api.request;

import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;

public record DeadlineUpdateRequestDto (
        @FutureOrPresent(message = "마감일은 미래 날짜여야 합니다.")
        LocalDate deadline
){
}
