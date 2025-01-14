package server.poptato.todo.domain.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Type {
    BACKLOG, YESTERDAY, TODAY;

    @JsonCreator
    public static Type from(String value) {
        return Type.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}
