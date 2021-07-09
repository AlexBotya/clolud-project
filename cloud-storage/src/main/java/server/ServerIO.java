package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerIO {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8190);
        System.out.println("Server started...");
        while(true){
            Socket socket = serverSocket.accept();
            System.out.println("Client accepted");
            try{
                new Thread(new Handler(socket)).start();
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("connection lost..");
            }
        }
    }
}
