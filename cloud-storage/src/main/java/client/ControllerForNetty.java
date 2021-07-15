package client;

import client.old.Message;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import server.FileMessage;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class ControllerForNetty implements Initializable {


    public Label output;
    public ListView<String> listView;
    private ObjectEncoderOutputStream os;
    private ObjectDecoderInputStream is;



    public void send(ActionEvent actionEvent) throws IOException {
        String fileName = listView.getSelectionModel().getSelectedItem();
        os.writeObject(new FileMessage(Paths.get("dir", fileName)));
        os.flush();
        output.setText("File: " + fileName +" sent to server");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            try (Socket socket = new Socket("localhost", 8186)) {
                os = new ObjectEncoderOutputStream(socket.getOutputStream());
                is = new ObjectDecoderInputStream(socket.getInputStream());
            }
            File dir = new File("dir");
            listView.getItems().addAll(dir.list());
            Thread readThread = new Thread(()->{
                try{
                    while (true){
                        FileMessage message = (FileMessage) is.readObject();
                        Platform.runLater(()-> output.setText(message.toString()));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            });
            readThread.setDaemon(true);
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

