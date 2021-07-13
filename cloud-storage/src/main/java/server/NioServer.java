package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class NioServer {
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ByteBuffer buf;
    private Path root;

    public NioServer() throws IOException {
        System.out.println("Server is rinning..");
        root = Paths.get("dir");
        buf = ByteBuffer.allocate(256);
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8189));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (serverSocketChannel.isOpen()) {
            selector.select();
            Set<SelectionKey> keySet = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    handleAccept(key);
                }
                if (key.isReadable()) {
                    handleRead(key);
                }
                iterator.remove();
            }
        }

    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        buf.clear();
        StringBuilder sb = new StringBuilder();
        int read;
        while (true) {
            read = channel.read(buf);
            if (read == -1) {
                channel.close();
                break;
            }
            if (read == 0) {
                break;
            }
            buf.flip();
            while (buf.hasRemaining()) {
                sb.append((char) buf.get());
            }
            buf.clear();

        }
        System.out.println("Received: " + sb);
        String command = sb.toString().trim();
        if (command.equals("ls")) {
            String files = Files.list(root)
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.joining("\n")) + "\r\n";
            ByteBuffer response = ByteBuffer.wrap(files.getBytes(StandardCharsets.UTF_8));
            channel.write(response);
        } else if (command.startsWith("cd")) {
            command = command.replaceAll("cd", "").trim();
            if (Files.isDirectory(Paths.get(command)) && Files.exists(Paths.get(command))) {
                root = Paths.get(command);
            } else {

                if (command.equals("..")) {
                    if (root.getParent() == null) {
                        root = root.toAbsolutePath();
                    }
                    root = root.getParent();
                } else if (Files.isDirectory(root.resolve(command))) {
                    root = root.resolve(command);
                } else if (command.startsWith("cat")){
                    command = command.replaceAll("cat +", "").trim();
                    String content = String.join("\n", Files.readAllLines(root.resolve(command))) + "\r \n";
                    ByteBuffer response = ByteBuffer.wrap(content.getBytes(StandardCharsets.UTF_8));
                    channel.write(response);
                }

                else {
                    ByteBuffer response = ByteBuffer.wrap("unknown command\r\n".getBytes(StandardCharsets.UTF_8));
                    channel.write(response);
                }
            }
        } else {

            ByteBuffer response = ByteBuffer.wrap((command + " is not directory\r\n").getBytes(StandardCharsets.UTF_8));
            channel.write(response);
        }
        printPath(channel);


    }

    private void printPath(SocketChannel channel) throws IOException {
        String path = root.toString() + " ";
        ByteBuffer response = ByteBuffer.wrap((path.getBytes(StandardCharsets.UTF_8)));
        channel.write(response);
    }

    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel channel = serverSocketChannel.accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
        printPath(channel);
    }

    public static void main(String[] args) throws IOException {
        new NioServer();
    }
}
