package client;

import javafx.scene.control.*;
import server.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ControllerForNetty implements Initializable {

    private Path currentDir;
    public ListView<String> clientView;
    public ListView<String> serverView;
    public TextField clientPath;
    public TextField serverPath;
    private ObjectEncoderOutputStream os;
    private ObjectDecoderInputStream is;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            currentDir = Paths.get("dir").toAbsolutePath();
            Socket socket = new Socket("localhost", 8189);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());

            refreshClientView();
            addNavigationListeners();

            Thread readThread = new Thread(()->{
                try{
                    while (true){
                        AbstractCommand command = (AbstractCommand) is.readObject();
                        switch (command.getType()){
                            case LIST_MESSAGE:
                                ListResponse response = (ListResponse) command;
                                List<String> names = response.getNames();
                                refreshServerView(names);
                                break;
                            case PATH_RESPONSE:
                                PathUpResponse pathResponse = (PathUpResponse) command;
                                String path = pathResponse.getPath();
                                Platform.runLater(()-> serverPath.setText(path));
                        }
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

    private void  refreshClientView() throws IOException {
        List<String> names = Files.list(currentDir)
                .map(p->p.getFileName().toString())
                .collect(Collectors.toList());
        Platform.runLater(()-> {
            clientView.getItems().clear();
            clientView.getItems().addAll(names);
        });
    }
    private void refreshServerView(List<String> names){
        Platform.runLater(()-> {
            serverView.getItems().clear();
            serverView.getItems().addAll(names);
        });

    }

    public void serverPathUp(ActionEvent actionEvent) throws IOException {
        os.writeObject(new PathUpRequest());
        os.flush();
    }

    public void clientPathUp(ActionEvent actionEvent) {
        currentDir = currentDir.getParent();
    }

    public void upload(ActionEvent actionEvent) {
    }

    public void download(ActionEvent actionEvent) {
    }

    private void addNavigationListeners(){
        clientView.setCellFactory(view ->{
            ListCell<String> cell = new ListCell<>();
            cell.setOnMouseClicked(event -> {
                String item = cell.getItem();
                Path newPath = currentDir.resolve(item);
                if (Files.isDirectory(newPath)){
                    currentDir = newPath;
                    try {
                        refreshClientView();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            return cell;
        });
    }
}

