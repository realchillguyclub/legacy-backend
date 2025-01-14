package server.poptato.global.response.status;

public interface ResponseStatus {
    int getCode();
    int getStatus();
    String getMessage();
}