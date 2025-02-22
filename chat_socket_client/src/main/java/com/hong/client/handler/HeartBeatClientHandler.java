package com.hong.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.LoggerFactory;

public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HeartBeatClientHandler.class);

    private static final ByteBuf HEARTBEAT_REQ = Unpooled.unreleasableBuffer(
            Unpooled.copiedBuffer("PING", CharsetUtil.UTF_8));

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                // 写空闲超时，发送心跳包
                logger.debug("客户端发送心跳: PING");
                ctx.writeAndFlush(HEARTBEAT_REQ.duplicate()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else if (event.state() == IdleState.READER_IDLE) {
                // 可能服务端挂掉了
            } else if (event.state() == IdleState.ALL_IDLE) {

            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
