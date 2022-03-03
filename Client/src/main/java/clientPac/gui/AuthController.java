package clientPac.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import clientPac.Model;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthController implements Initializable {

    @FXML
    public VBox authBaseBox;
    public Button signInButton;
    public TextField textFieldLogin;
    public TextField textFieldPass;

    @FXML
    public TextField leftTextField;
    public ListView<String> leftList;

    Model model;
    MainController mainController;

    private boolean possibleToCreate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldLogin.setPromptText("Логин");
        textFieldLogin.setFocusTraversable(false);
        textFieldPass.setPromptText("Пароль");
        textFieldPass.setFocusTraversable(false);
        model = Model.getModel();
        mainController = new MainController();
    }

    // События окна авторизации

    public void clickCloseLabel(MouseEvent mouseEvent) {
        Stage stage = (Stage) authBaseBox.getScene().getWindow();
        stage.close();

        //todo
        //Добавить сворачивание окна
        //Сделать перемещение окна мышкой
        //Добавить картинки для кнопки "закрыть" и лого
    }

    public void clickSignInButton(ActionEvent actionEvent) throws Exception {
        URL mainSceneURL;
        Parent main;

        //Подключаемся к серверу

        model.startChannels("localhost", 8188, 8189);

        //Проходим аутентификацию

        while(model.getNetService().getCommChannel() == null)
            Thread.sleep(100);
        model.authorization(textFieldLogin.getText(), textFieldPass.getText());


        //Подключаем файл FXML и запускаем основное окно программы

        try {
            mainSceneURL = getClass().getResource("/mainScene.fxml");
            if (mainSceneURL == null) throw new RuntimeException("Нет доступа к файлу mainScene.fxml");
            main = FXMLLoader.load(mainSceneURL);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("SWW during FXML loading");
        }

        Stage newStage = new Stage();
        newStage.setWidth(800);
        newStage.setHeight(600);
        newStage.setTitle("DreamCloud");
        Scene mainScene = new Scene(main);
        newStage.setScene(mainScene);
        newStage.show();
        Stage stage = (Stage) authBaseBox.getScene().getWindow();
        stage.close();
    }

    public void clickParamLabel(MouseEvent mouseEvent) {

    }
}
