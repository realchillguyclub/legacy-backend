package server.poptato.global.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import server.poptato.global.response.status.ResponseStatus;

import java.time.LocalDateTime;

@Getter
@JsonPropertyOrder({"code","status","message","timestamp"})
public class BaseErrorResponse implements ResponseStatus {
    private final int code;
    private final int status;
    private final String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp;

    public BaseErrorResponse(ResponseStatus status) {
        this.code = status.getCode();
        this.status = status.getStatus();
        this.message = status.getMessage();
        this.timestamp = LocalDateTime.now();
    }

    public BaseErrorResponse(ResponseStatus status, String message) {
        this.code = status.getCode();
        this.status = status.getStatus();
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String convertToJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(this);
    }
}
