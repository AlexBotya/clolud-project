package server;

import java.io.*;
import java.net.Socket;


public class Handler implements Runnable{
    private String dir = "server_dir";
    private final byte[] buffer;
    private final DataInputStream is;
    private final DataOutputStream os;
    private final Socket socket;


    public Handler(Socket socket) throws IOException {
        this.socket = socket;
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        buffer = new byte[1024];
    }

    @Override
    public void run() {
        try{
            while (true){
                String fileName = is.readUTF();
                System.out.println("File name : " + fileName);
                long size = is.readLong();
                System.out.println("Size" + size);
                try (FileOutputStream fos = new FileOutputStream(dir + "/" + fileName)) {
                    for (int i = 0; i < (size+1023)/1024; i++) {
                        int read = is.read(buffer);
                        fos.write(buffer, 0, read);
                    }
                }
                os.writeUTF("File " + fileName + "successfully read");
                os.flush();
            }

    } catch (Exception e){
            System.out.println("Exception while read");
            e.printStackTrace();
        }
    }
}
