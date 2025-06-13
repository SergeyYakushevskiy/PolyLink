package dstu.csae.controller;

import dstu.csae.codec.Codec;
import dstu.csae.codec.TextConverter;
import dstu.csae.config.Settings;
import dstu.csae.config.WorkModeEnum;
import dstu.csae.galois.FieldExtensionEnum;
import dstu.csae.gui.SettingsWindow;
import dstu.csae.index.Index;
import dstu.csae.polynomial.FieldPolynomial;
import dstu.csae.rs_code.RSCode;
import dstu.csae.rs_code.RSFCoder;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MainController {

    private static final Logger logger = LogManager.getLogger(MainController.class);

    @FXML private MenuItem exitMenuItem;
    @FXML private MenuItem settingsMenuItem;
    @FXML private MenuItem aboutMenuItem;
    @FXML private TextArea inputArea;
    @FXML private TextFlow polynomialFlow;
    @FXML private TextArea outputArea;
    @FXML private Label inputAreaLabel;
    @FXML private Label polynomialAreaLabel;
    @FXML private Label outputAreaLabel;
    @FXML private ComboBox<WorkModeEnum> modeComboBox;
    @FXML private Label codeLabel;
    @FXML private Label modeLabel;
    @FXML private Button operateButton;

    @FXML
    public void initialize(){
        modeComboBox.getItems().addAll(WorkModeEnum.values());
        modeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            Settings.getInstance().setMode(newValue);
            updateModeLabel(newValue);
        });
        logger.info("Окно инициализировано");
    }

    @FXML
    public void onExit(ActionEvent event){
        Platform.exit();
        logger.info("Программа завершена");
    }

    @FXML
    public void onSettings(ActionEvent event) {
        Stage loadingStage = createLoadingStage();
        loadingStage.show();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                FieldExtensionEnum.values();
                Platform.runLater(() -> {
                    SettingsWindow.getInstance().show();
                });
                return null;
            }

            @Override
            protected void succeeded() {
                SettingsWindow.getInstance().setMainController(MainController.this);
                loadingStage.close();
            }

            @Override
            protected void failed() {
                loadingStage.close();
                // показать ошибку
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        logger.info("Окно настроек успешно открыто");
    }

    @FXML
    public void onOperate(ActionEvent event){

        String input = inputArea.getText();
        if(input.isBlank()){
            return;
        }
        logger.info("Обработка текста");
        clear();
        RSFCoder coder;
        try{
            coder = Settings.getInstance().getCoder();
            TextConverter converter = TextConverter.getInstance();
            converter.setCoder(coder);
            WorkModeEnum mode = Settings.getInstance().getMode();
            if (mode.equals(WorkModeEnum.ENCODE)) {
                showEncode(input, converter);
                return;
            }
            showDecode(input, converter);
        }catch (Exception ex){
            logger.error("Ошибка: ", ex);
            showError("Ошибка: " + ex.getMessage());
        }

    }

    public void clear(){
        inputArea.clear();
        polynomialFlow.getChildren().clear();
        outputArea.clear();
    }

    public void updateCodeLabel(RSCode code){
        this.codeLabel.setText("Код: " + code);
    }

    public void updateModeLabel(WorkModeEnum mode){
        this.modeLabel.setText("Режим работы: " + mode.toString().toLowerCase());
        switch (mode){
            case ENCODE -> {
                this.inputAreaLabel.setText("Входной текст");
                this.polynomialAreaLabel.setText("Полиномиальный вид");
                this.outputAreaLabel.setText("Закодированный текст");
            }
            case DECODE -> {
                this.inputAreaLabel.setText("Закодированный текст");
                this.polynomialAreaLabel.setText("Полиномиальный вид");
                this.outputAreaLabel.setText("Раскодированный текст");
            }
        }
    }

    public void updateModeComboBox(WorkModeEnum mode){
        modeComboBox.setValue(mode);
    }

    public void loadForm(Settings settings){
        if(settings == null){
            return;
        }
        RSCode code = settings.getCoder().getCode();
        WorkModeEnum mode = settings.getMode();
        updateCodeLabel(code);
        updateModeLabel(mode);
        updateModeComboBox(mode);
    }

    private void showEncode(String input, TextConverter converter){
        inputArea.setText(input);
        byte[] bytes = converter.getBytes(input);
        RSCode code = converter.getCoder().getCode();
        ArrayList<FieldPolynomial> polynomials = converter.bytesToPolynomials(bytes, code.getK());
        polynomials = converter.encode(polynomials);
        Text p = new Text(polynomials.stream()
                .map(String::valueOf)
                .collect(Collectors.joining("\n")));
        polynomialFlow.getChildren().add(p);
        byte[] encodedBytes = converter.polynomialsToBytes(polynomials, code.getN());
        String encodedText = converter.getBase64String(encodedBytes);
        outputArea.setText(encodedText);
    }

    private void showDecode(String input, TextConverter converter){
        inputArea.setText(input);
        byte[]bytes = converter.getBase64Bytes(input);
        RSCode code = converter.getCoder().getCode();
        ArrayList<FieldPolynomial> polynomials = converter.bytesToPolynomials(bytes, code.getN());
        polynomials = showErrors(polynomials, converter);

        byte[] decodedBytes = converter.polynomialsToBytes(polynomials, code.getK());
        String decodedText = converter.getString(decodedBytes);
        outputArea.setText(decodedText);
    }

    private ArrayList<FieldPolynomial> showErrors(ArrayList<FieldPolynomial> received, TextConverter converter){
        ArrayList<FieldPolynomial> decoded = converter.decode(received);
        ArrayList<FieldPolynomial> encoded = converter.encode(decoded);
        if(encoded.size() != received.size()){
            return decoded;
        }
        for(int i = 0; i < encoded.size(); i ++) {
            FieldPolynomial encodedWord = encoded.get(i);
            FieldPolynomial receivedWord = received.get(i);
            encodedWord.removeLastZero();
            receivedWord.removeLastZero();
            showPolynomial(receivedWord.getCoefficients(), encodedWord.getCoefficients());
        }
        return decoded;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка: ");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Stage createLoadingStage() {
        Label label = new Label("Подождите, идёт подготовка полей Галуа...");
        VBox box = new VBox(label);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        Scene scene = new Scene(box);

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Загрузка");
        stage.setScene(scene);
        stage.setWidth(300);
        stage.setHeight(100);
        return stage;
    }

    private void showPolynomial(int[] receivedC, int[] encodedC){
        if(receivedC.length != encodedC.length){
            return;
        }
        for(int i = 0; i < receivedC.length; i ++){
            boolean isCorrect = receivedC[i] == encodedC[i];
            String monomial = String.valueOf(receivedC[i]);
            if(i == 0){
                monomial += " + ";
            }else{
                monomial += "x" + Index.toSuperscript(String.valueOf(i)) +
                        ((i != receivedC.length - 1) ? " + ": "");
            }
            Text text = new Text(monomial);
            if(!isCorrect){
                text.setFill(Color.rgb(191, 43, 27));
                Tooltip tooltip = new Tooltip("Ожидалось: " + encodedC[i]);
                Tooltip.install(text, tooltip);
            }
            polynomialFlow.getChildren().add(text);
        }
        polynomialFlow.getChildren().add(new Text("\n"));
    }

}
