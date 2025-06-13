package dstu.csae.config;

public enum WorkModeEnum {
    ENCODE("Кодирование"),
    DECODE("Декодирование");

    private final String title;

    WorkModeEnum(String title){
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
