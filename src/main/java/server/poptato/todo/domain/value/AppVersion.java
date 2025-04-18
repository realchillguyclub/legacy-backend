package server.poptato.todo.domain.value;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AppVersion {
    V1("1.0", 1.0),
    V2("2.0", 2.0);

    private final String label;
    private final double value;

    public boolean isLegacy() {
        return this.value < 2.0;
    }
}
