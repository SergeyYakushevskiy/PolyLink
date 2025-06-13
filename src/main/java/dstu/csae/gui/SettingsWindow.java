package dstu.csae.gui;

import dstu.csae.config.Settings;
import dstu.csae.config.SettingsWindowConfig;
import dstu.csae.controller.MainController;
import dstu.csae.controller.SettingsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;


public class SettingsWindow {
    private static final Logger logger = LogManager.getLogger(SettingsWindow.class);

    private static volatile SettingsWindow instance;
    @Getter @Setter
    private MainController mainController;
    private Stage stage;

    private SettingsWindow() {
        logger.info("Создание окна настроек");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(SettingsWindowConfig.getFxmlPath()));
            Parent root = loader.load();

            SettingsController controller = loader.getController();
            controller.loadForm(Settings.getInstance());

            stage = new Stage();
            stage.setTitle(SettingsWindowConfig.getTitle());
            stage.setScene(new Scene(root, SettingsWindowConfig.getWidth(), SettingsWindowConfig.getHeight()));
            stage.initModality(Modality.valueOf(SettingsWindowConfig.getModality()));
            stage.setResizable(SettingsWindowConfig.isResizeable());
            logger.info("Окно настроек создано");
        } catch (IOException e) {
            logger.error("Ошибка при создании окна настроек", e);
        }
    }

    public static SettingsWindow getInstance() {
        if (instance == null) {
            synchronized (SettingsWindow.class){
                if(instance == null){
                    instance = new SettingsWindow();
                }
            }
        }
        return instance;
    }

//    public void setMainController(@NonNull MainController controller){
//        this.mainController = controller;
//    }

    public void show() {
        if (stage == null) {
            logger.error("Stage не инициализирован, окно настроек не может быть показано");
            return;
        }
        if (!stage.isShowing()) {

            stage.showAndWait(); // модальное окно
        } else {
            stage.toFront();
        }
    }
}
