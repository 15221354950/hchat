package com.hong.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class SocketClientHandler extends ChannelInboundHandlerAdapter {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SocketClientHandler.class);

    /**
     * Creates a client-side handler.
     */
    public SocketClientHandler(int firstMessageSize) {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
//        ctx.writeAndFlush(Unpooled.copiedBuffer("收到over", StandardCharsets.UTF_8));
        logger.info("channelActive");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("ChatChannelHandler.channelRead");
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            try {
                byteBuf.retain();
                String receiveData = byteBuf.toString(StandardCharsets.UTF_8);
                logger.info("receiveData:{}", receiveData);
                // todo
            } finally {
                logger.info("byteBuf.refCnt:{}", byteBuf.refCnt());
                byteBuf.release();
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

}
