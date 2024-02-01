package lk.ijse.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class ChatFormController {
    public Button btnSend;
    public TextField txtMessage;
    public TextArea txtArea;
    public VBox vboxMsg;
    public ImageView imageEmoji;

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    String name = "";

    public void initialize(){
        new Thread(()->{
            try {
                socket = new Socket("localhost",3002);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                while (socket.isConnected()){
                    String message = dataInputStream.readUTF();
                    System.out.println(message);
                    recieveMsg(message , vboxMsg);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    private void recieveMsg(String message, VBox vboxMsg) {
        if (message.startsWith("IMAGE IS MESSAGE")){

            receiveImageMessage(message);

        }else {
            receiveTextMessage(message,vboxMsg);
        }
    }
//meken mama ena msg eka text ekak nm e method ek call kra image ekak nn anika call kara

    public void btnSendOnAction(ActionEvent actionEvent) {
        String msg = txtMessage.getText();
        messageSend(msg);


    }

    public void messageSend(String msg){
        HBox hbox = new HBox();
        Text text = new Text(msg);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: #6bbd72; -fx-font-weight: bold; -fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5,10,5,10));
        text.setFill(Color.color(0,0,0));
        hbox.setAlignment(Pos.CENTER_RIGHT);
        hbox.getChildren().add(textFlow);
        vboxMsg.getChildren().add(hbox);
        txtMessage.clear();

        try {
            dataOutputStream.writeUTF(name + "-" + msg);
            // System.out.println(msg);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void receiveTextMessage(String message , VBox vboxMsg){
        String name = message.split("-")[0];
        String msg = message.split("-")[1];
        //meka gtte yawana kenage nama mulin daaganna
        HBox hBoxName = new HBox();
        hBoxName.setAlignment(Pos.CENTER_LEFT);
        javafx.scene.text.Text textName = new Text(name);
        TextFlow textFlow = new TextFlow(textName);
        hBoxName.getChildren().add(textFlow);

        //meken mama ena msg ek display kara
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        Text text = new Text(msg);
        TextFlow textFlowMsg= new TextFlow(text);
        textFlowMsg.setStyle("-fx-background-color: #7fa6c7; -fx-font-weight: bold; -fx-background-radius: 20px");
        textFlowMsg.setPadding(new Insets(5,10,5,10));
        text.setFill(Color.color(0,0,0));
        hBox.getChildren().add(textFlowMsg);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vboxMsg.getChildren().add(hBoxName);
                vboxMsg.getChildren().add(hBox);
            }
        });

    }
    private void receiveImageMessage(String receivingMsg) {
        try {
            String name = receivingMsg.split("-")[1];
            int imageDataLength = dataInputStream.readInt();
            byte[] imageData = new byte[imageDataLength];
            dataInputStream.readFully(imageData);
            //  saveImageToFile(imageData);
            displayReceivedImage(imageData,name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //meken mama ena image eka allagtta

    private void displayReceivedImage(byte[] imageData, String name) {
        Image image = new Image(new ByteArrayInputStream(imageData));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        HBox hBox = new HBox(imageView);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 5, 0, 10));

        HBox hBoxName = new HBox();
        hBoxName.setAlignment(Pos.CENTER_LEFT);
        javafx.scene.text.Text textName = new Text(name);
        TextFlow textFlow = new TextFlow(textName);
        hBoxName.getChildren().add(textFlow);
        // hBox.setStyle("-fx-background-color: #abb8c3; -fx-font-weight: bold; -fx-background-radius: 20px");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vboxMsg.getChildren().add(hBoxName);
                vboxMsg.getChildren().add(hBox);
            }
        });
    }
    //e allagtta image eka display kra


    public void imageCloseOnAction(MouseEvent mouseEvent) {


    }

    private void displayImage(byte[] imageData) {
        Image image = new Image(new ByteArrayInputStream(imageData));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        HBox hBox = new HBox(imageView);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(5, 5, 0, 10));
        //hBox.setStyle("-fx-background-color: #7164cb; -fx-font-weight: bold; -fx-color: white; -fx-background-radius: 20px");



        HBox hBoxTime = new HBox();
        hBoxTime.setAlignment(Pos.CENTER_RIGHT);
        hBoxTime.setPadding(new Insets(0, 5, 5, 10));
        String stringTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        Text time = new Text(stringTime);
        time.setStyle("-fx-font-size: 10");

        hBoxTime.getChildren().add(time);

        vboxMsg.getChildren().add(hBox);
        vboxMsg.getChildren().add(hBoxTime);

    }

    public void imageMinimizeOnAction(MouseEvent mouseEvent) {
        Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
        // Minimize the stage
        stage.setIconified(true);
    }


    public void setClientName(String text) {
        this.name = text;
    }

    public void imageOnAction(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an image file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) vboxMsg.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            byte[] imageData = new byte[0];
            try {
                imageData = Files.readAllBytes(selectedFile.toPath());
                // Encode the image data as Base64 for sending
                String base64Image = Base64.getEncoder().encodeToString(imageData);
                // image ek server ekata yawwa
                dataOutputStream.writeUTF("IMAGE IS MESSAGE"+"-"+name);
                dataOutputStream.flush();
                dataOutputStream.writeInt(imageData.length);
                dataOutputStream.flush();
                dataOutputStream.write(imageData);
                dataOutputStream.flush();

                //  image eka display kara
                displayImage(imageData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }


    public void imageEmojiOnAction(MouseEvent mouseEvent) {

    }
}
