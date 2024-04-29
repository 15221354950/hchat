package com.hong.socket.components;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.*;
import java.nio.charset.StandardCharsets;

@Component
@ChannelHandler.Sharable
public class ChatChannelHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(ChatChannelHandler.class);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("ChatChannelHandler.channelRead");
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            try {
                String receiveData = byteBuf.toString(StandardCharsets.UTF_8);
                logger.info("receiveData:{}", receiveData);
                // todo
            } finally {
                byteBuf.release();
            }
        }
        ctx.write(Unpooled.copiedBuffer(ctx.channel().id() + "收到消息啦", StandardCharsets.UTF_8));
//                ctx.writeAndFlush(Unpooled.copiedBuffer("收到", StandardCharsets.UTF_8));
//                ctx.flush();
        logger.info("response.over");
//        super.channelRead(ctx, msg);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelReadComplete");
//        super.channelReadComplete(ctx);
//        ctx.writeAndFlush(Unpooled.copiedBuffer("收到over", StandardCharsets.UTF_8));
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        logger.info("exceptionCaught");
        ctx.close();
    }

    /**
     * 和客户端第一次建立连接时调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
//        Channel channel =
                ctx.channel().read();
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        InetAddress inetAddress = socketAddress.getAddress();
        logger.info("channelActive.IP:{},name:{}", inetAddress.getHostAddress(), ctx.name());
        ctx.writeAndFlush(Unpooled.copiedBuffer("收到over", StandardCharsets.UTF_8));
    }


    /**
     * 断开连接时调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelInactive");
//        super.channelInactive(ctx);
        ctx.close();
    }

}
