package dstu.csae.config;

public class MainWindowConfig {
    private static final String KEY = "app.window";

    public static String getFxmlPath(){
        return Config.getProperty(KEY + ".fxml.path");
    }

    public static String getTitle(){
        return Config.getProperty(KEY + ".title");
    }

    public static int getWidth(){
        return Integer.parseInt(Config.getProperty(KEY + ".width"));
    }

    public static int getHeight(){
        return Integer.parseInt(Config.getProperty(KEY + ".height"));
    }

    public static boolean isResizeable(){
        return Boolean.parseBoolean(Config.getProperty(KEY + ".resizeable"));
    }

    public static boolean isFullScreen(){
        return Boolean.parseBoolean(Config.getProperty(KEY + ".fullscreen"));
    }
}
