package server;

import client.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<Message> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        ctx.writeAndFlush(message);
        log.debug("received: {} ", message);
    }
}
