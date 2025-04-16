package server.poptato.todo.domain.value;

public class AppVersion {
    private final double version;

    public AppVersion(String version) {
        this.version = Double.parseDouble(version);
    }

    public boolean isLegacy() {
        return version < 2.0;
    }

    public double getValue() {
        return version;
    }
}
