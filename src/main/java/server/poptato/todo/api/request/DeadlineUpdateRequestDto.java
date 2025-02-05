package server.poptato.todo.api.request;

import jakarta.validation.constraints.Future;

import java.time.LocalDate;

public record DeadlineUpdateRequestDto (
        @Future(message = "마감일은 미래 날짜여야 합니다.")
        LocalDate deadline
){
}
