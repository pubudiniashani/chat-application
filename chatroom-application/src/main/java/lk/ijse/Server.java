package lk.ijse;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Application {
    private ServerSocket serverSocket;
    private List<Client> clients = new ArrayList<>();

    public void init() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(3002);
                System.out.println("Server Started");

                while (true) {
                    Socket socket = serverSocket.accept();
                    Client client = new Client(socket, clients);
                    clients.add(client);
                    System.out.println("Client Connected");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(this.getClass().getResource("/view/login_form.fxml"))));
        stage.centerOnScreen();
        stage.setTitle("Server");
        stage.show();
    }
}
