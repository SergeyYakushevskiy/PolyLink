package dstu.csae.gui;

import dstu.csae.config.MainWindowConfig;
import dstu.csae.config.Settings;
import dstu.csae.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainWindow extends Application {
    private static final Logger logger = LogManager.getLogger(MainWindow.class);

    @Override
    public void start(Stage stage) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Logger logger = LogManager.getLogger(getClass());
            logger.error("Исключение: ", throwable);

        });
        logger.info("Запуск приложения...");
        FXMLLoader loader = new FXMLLoader(getClass().getResource(MainWindowConfig.getFxmlPath()));
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.loadForm(Settings.getInstance());


        Scene scene = new Scene(root, MainWindowConfig.getWidth(), MainWindowConfig.getHeight());

        stage.setTitle(MainWindowConfig.getTitle());
        stage.setScene(scene);
        stage.setFullScreen(MainWindowConfig.isFullScreen());
        stage.setResizable(MainWindowConfig.isResizeable());
        stage.show();
        logger.info("Приложение создано и запущено");
    }


}
