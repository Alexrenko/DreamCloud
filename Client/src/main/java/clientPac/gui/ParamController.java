package clientPac.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.List;
import java.util.ResourceBundle;

public class ParamController implements Initializable {

    Path cfgPath;
    String host, commPort, carrierPort;

    @FXML
    public VBox paramBaseBox;
    public TextField textFieldServer, textFieldCommPort, textFieldCarrierPort;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Инициализируем путь к конфигурационному файлу
        String strPath = getClass().getResource("/").getPath().substring(1);
        strPath += "config.cfg";
        cfgPath = Paths.get(strPath);

        //Инициализируем путь к конфигурационному файлу
        //URL configURL = getClass().getResource("/config.cfg");
        //String strPath = configURL.getPath().substring(1);
        //cfgPath = Paths.get(strPath);

        //Считываем параметры подключения из конфигурационного файла
        try {
            List<String> param = Files.readAllLines(cfgPath);
            host = param.get(0);
            commPort = param.get(1);
            carrierPort = param.get(2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Заносим параметры подключения в текстовые поля
        textFieldServer.setText(host);
        textFieldCommPort.setText(commPort);
        textFieldCarrierPort.setText(carrierPort);

        //убираем выделение
        textFieldServer.setFocusTraversable(false);
        textFieldCommPort.setFocusTraversable(false);
        textFieldCarrierPort.setFocusTraversable(false);

    }

    public void clickCloseLabel(MouseEvent mouseEvent) {
        backToAuthWindow();
    }

    public void clickSaveButton(ActionEvent actionEvent) {
        //записываем параметры в файл config.cfg
        try {
            host = textFieldServer.getText();
            commPort = textFieldCommPort.getText();
            carrierPort = textFieldCarrierPort.getText();
            String parameters = String.format("%s%n%s%n%s", host, commPort, carrierPort);
            if (Files.exists(cfgPath))
                Files.delete(cfgPath);
            Files.write(cfgPath, parameters.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //возвращаемся в окно авторизации
        backToAuthWindow();
    }

    private void backToAuthWindow() {
        Parent auth;
        URL authSceneURL;

        //открываем окно авторизации
        try {
            authSceneURL = getClass().getResource("/authScene.fxml");
            if (authSceneURL == null) throw new RuntimeException("Нет доступа к файлу authScene.fxml");
            auth = FXMLLoader.load(authSceneURL);
        } catch (IOException e) {
            throw new RuntimeException("SWW during FXML loading");
        }

        Stage primaryStage = new Stage();
        primaryStage.setWidth(290);
        primaryStage.setHeight(270);
        primaryStage.setTitle("DreamCloud");
        primaryStage.setResizable(false);
        Scene authScene = new Scene(auth);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(authScene);
        primaryStage.show();

        //закрываем окно параметров
        Stage stage = (Stage) paramBaseBox.getScene().getWindow();
        stage.close();
    }
}
