package server.poptato.todo.api.request;

import java.time.LocalDate;

public record DeadlineUpdateRequestDto (
        LocalDate deadline
){
}
