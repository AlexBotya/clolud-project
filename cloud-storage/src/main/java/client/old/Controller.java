package client.old;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    public Label output;
    public ListView<String> listView;
    private DataInputStream is;
    private DataOutputStream os;


    public void send(ActionEvent actionEvent) throws IOException {
        String fileName = listView.getSelectionModel().getSelectedItem();
        File file = new File("dir" + fileName);
        long size = file.length();
        os.writeUTF(fileName);
        os.writeLong(size);
        Files.copy(file.toPath(), os);
        output.setText("File: " + fileName +" sent to server");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            Socket socket = new Socket("localhost", 8190);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            File dir = new File("dir");
            listView.getItems().addAll(dir.list());
            Thread readThread = new Thread(()->{
                try{
                    while (true){
                        String status = is.readUTF();
                        Platform.runLater(()-> output.setText(status));
                    }
                } catch (IOException e){
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

