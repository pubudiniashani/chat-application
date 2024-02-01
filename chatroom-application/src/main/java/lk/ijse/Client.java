package lk.ijse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    private Socket socket;

    List<Client> clients ;
    String msg = "";


    public Client(Socket socket , List<Client> clients){
        this.socket = socket;
        this.clients = clients;

        try {
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sendMessage();
    }
    private void sendMessage(){
        new Thread(()->{
            while (socket.isConnected()){
                try {
                    msg = dataInputStream.readUTF();
                    System.out.println(msg);
                    if (msg.startsWith("IMAGE IS MESSAGE")){
                        imageHandle(msg);

                    }else {

                    for (Client client:clients ) {
                        if (client.socket.getPort() != socket.getPort()) {
                            client.dataOutputStream.writeUTF(msg);
                            client.dataOutputStream.flush();
                            }
                        }
                    }
                } catch (IOException e) {

                }
            }

        }).start();
    }

    private void imageHandle(String msg) {
        try {
            int imageDataLength =dataInputStream.readInt();
            byte[] imageData = new byte[imageDataLength];
            dataInputStream.readFully(imageData);
            // anik ayatt image eka peenna meka dmme
           manageImage(imageData,msg);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void manageImage(byte[] imageData, String msg) {
        String clientName = msg.split("-")[1];
        for (Client client : clients){
            if (client.socket.getPort()!=socket.getPort()){
                try {
                    client.dataOutputStream.writeUTF("IMAGE IS MESSAGE"+"-"+clientName);
                    client.dataOutputStream.flush();
                    client.dataOutputStream.writeInt(imageData.length);
                    client.dataOutputStream.flush();
                    client.dataOutputStream.write(imageData);
                    client.dataOutputStream.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }

    }

}
