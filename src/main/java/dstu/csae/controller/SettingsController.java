package dstu.csae.controller;

import dstu.csae.config.Settings;
import dstu.csae.galois.FieldExtensionEnum;
import dstu.csae.galois.extended.GaloisFieldExtension;
import dstu.csae.gui.SettingsWindow;
import dstu.csae.rs_code.RSCode;
import dstu.csae.rs_code.RSFCoder;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SettingsController {

    private static final Logger logger = LogManager.getLogger(SettingsController.class);

    @FXML private ComboBox<FieldExtensionEnum> galoisComboBox;
    @FXML private TextField nField;
    @FXML private TextField kField;
    @FXML private TextField dField;
    @FXML private Button saveButton;

    @FXML
    public void initialize(){
        galoisComboBox.getItems().addAll(FieldExtensionEnum.values());
        galoisComboBox.setValue(FieldExtensionEnum.GF256);
    }

    @FXML
    public void onSave(){
        GaloisFieldExtension field = galoisComboBox.getValue().getField();
        RSCode code;
        RSFCoder coder;
        try {
            if(nField.getText().isBlank() && kField.getText().isBlank() && dField.getText().isBlank()){
                throw new IllegalArgumentException("Поля пусты");
            }
            int n = 0;
            int k = 0;
            int d = 0;
            if (nField.getText().isBlank() || kField.getText().isBlank()) {
                d = Integer.parseInt(dField.getText());
                code = new RSCode(field, d);
            } else {
                n = Integer.parseInt(nField.getText());
                k = Integer.parseInt(kField.getText());
                code = new RSCode(field, n, k);
            }
            coder = new RSFCoder(code);
            Settings.getInstance().setCoder(coder);
            Stage stage = (Stage) saveButton.getScene().getWindow();
            SettingsWindow.getInstance().getMainController().updateCodeLabel(code);
            stage.hide();
        }catch (Exception ex){
            showError(ex.getMessage());
            logger.error(ex);
        }
    }

    public void loadForm(Settings settings){
        FieldExtensionEnum field = FieldExtensionEnum.fromField(settings.getCoder().getCode().getField())
                .orElse(FieldExtensionEnum.GF4);
        galoisComboBox.setValue(field);
        nField.setText(String.valueOf(settings.getCoder().getCode().getN()));
        kField.setText(String.valueOf(settings.getCoder().getCode().getK()));
        dField.setText(String.valueOf(settings.getCoder().getCode().getD()));
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка: ");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
