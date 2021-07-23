package server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<AbstractCommand> {
    private Path currentPath;

    public MessageHandler() {

        currentPath = Paths.get("server_dir");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractCommand command) throws Exception {
        log.debug("received: {} ", command);
        switch (command.getType()){
            case FILE_MESSAGE:
                FileMessage message = (FileMessage) command;
                try (FileOutputStream fos = new FileOutputStream("server_dir/" + message.getName())){
                    fos.write(message.getData());
                }
                break;
            case PATH_UP:
                if(currentPath.getParent() != null) {
                    currentPath = currentPath.getParent();
                }
                ctx.writeAndFlush(new Process() {
                    @Override
                    public OutputStream getOutputStream() {
                        return null;
                    }

                    @Override
                    public InputStream getInputStream() {
                        return null;
                    }

                    @Override
                    public InputStream getErrorStream() {
                        return null;
                    }

                    @Override
                    public int waitcurrentDir.resolve(item)For() throws InterruptedException {
                        return 0;
                    }

                    @Override
                    public int exitValue() {
                        return 0;
                    }

                    @Override
                    public void destroy() {

                    }
                })

            case LIST_REQUEST:
        }
    }


}
