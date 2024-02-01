package lk.ijse.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginFormController {
    public Button btnLogin;
    public TextField txtUsername;
    public AnchorPane root;

    public void btnLoginOnAction(ActionEvent actionEvent) throws IOException {

        if(!txtUsername.getText().isEmpty()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/chat_form.fxml"));
            AnchorPane anchorPane = loader.load();
            Stage chatStage = new Stage();
            chatStage.setTitle("Chat Room");
            Scene scene = new Scene(anchorPane);
            chatStage.setScene(scene);
            ChatFormController controller =loader.getController();
            controller.setClientName(txtUsername.getText());
            chatStage.centerOnScreen();
            chatStage.show();
            txtUsername.clear();

        }
    }
}
