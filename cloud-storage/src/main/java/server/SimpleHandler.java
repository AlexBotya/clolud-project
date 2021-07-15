package server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class SimpleHandler extends ChannelInboundHandlerAdapter {

    private final SimpleDateFormat formatter;

    public SimpleHandler() {
        formatter = new SimpleDateFormat("[yyyy-MM-dd] HH:mm:ss");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client accepted..");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client disconnected..");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        log.debug("buf: {}", buf);
        StringBuilder sb = new StringBuilder();
        while (buf.isReadable()){
            char c = (char) buf.readByte();
            sb.append(c);

        }
        log.debug("received: {}", sb);
        Date now = new Date();

        String message = formatter.format(now) + " " +sb.toString();
        ByteBuf response = ctx.alloc().buffer();
        response.writeBytes(message.getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(response);
    }
}
