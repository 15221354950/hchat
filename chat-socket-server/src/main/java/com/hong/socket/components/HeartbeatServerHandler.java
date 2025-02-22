package com.hong.socket.components;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class HeartbeatServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(HeartbeatServerHandler.class);

    private static final ByteBuf HEARTBEAT_PACK = Unpooled.unreleasableBuffer(
            Unpooled.copiedBuffer("PONG", CharsetUtil.UTF_8));

    private static final ConcurrentHashMap<String, Long> concurrentHashMap = new ConcurrentHashMap<>();

    public HeartbeatServerHandler(boolean autoRelease) {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        logger.info("HeartbeatServerHandler.userEventTriggered");
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            String evtState = null;
            String key = ctx.channel().id().asLongText();
            Long count = concurrentHashMap.getOrDefault(key, 0L);
            //将该事件消息强转为心跳事件
            IdleState state = event.state();
            switch (state) {
                case READER_IDLE:
                    evtState = "读空闲";
                    break;
                case WRITER_IDLE:
                    evtState = "写空闲";
                    break;
                case ALL_IDLE:
                    evtState = "读写空闲";
                    count++;
                    break;
                default:
                    break;
            }
            logger.info("userEventTriggered-evtState:{}", evtState);
            //空闲计数达5次,进行测试连接是否正常
            if (count > 2L) {
                ctx.writeAndFlush("测试客户端是否能接收信息").addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                //发送失败时关闭通道,在或者可以在达到空闲多少次后,进行关闭通道
                concurrentHashMap.remove(key);
                return;
            }
            concurrentHashMap.put(key, count);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object event) throws Exception {
        logger.info("HeartbeatServerHandler.channelRead0,{}", event);
        if (event instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) event;
            String receiveData = byteBuf.toString(StandardCharsets.UTF_8);
            logger.info("HeartbeatServerHandler.channelRead:{}", receiveData);
            if ("PING".equals(receiveData)) {
                ctx.writeAndFlush(HEARTBEAT_PACK.duplicate()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                ReferenceCountUtil.release(event);
            }
        }
    }

}