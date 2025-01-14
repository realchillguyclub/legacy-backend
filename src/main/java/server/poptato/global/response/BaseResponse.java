package server.poptato.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import server.poptato.global.response.status.ResponseStatus;

import static server.poptato.global.exception.errorcode.BaseExceptionErrorCode.SUCCESS;

@Getter
@JsonPropertyOrder({"code","status","message","result"})
public class BaseResponse<T> implements ResponseStatus {
    private final int code;
    private final int status;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;


    public BaseResponse() {
        this.code = SUCCESS.getCode();
        this.status = SUCCESS.getStatus();
        this.message = SUCCESS.getMessage();
        this.result = null;
    }

    public BaseResponse(T result) {
        this.code = SUCCESS.getCode();
        this.status = SUCCESS.getStatus();
        this.message = SUCCESS.getMessage();
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

