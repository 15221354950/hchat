package com.hong.socket.components;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class HeartbeatServerHandle extends SimpleChannelInboundHandler<Object> {

    private static Logger logger = LoggerFactory.getLogger(HeartbeatServerHandle.class);
    private static ConcurrentHashMap<String, Long> concurrentHashMap = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object evt) throws Exception {
            //        判断事件是否是IdleStateEvent
        if (evt instanceof IdleStateEvent) {
            String evtState = null;
            String key = ctx.channel().id().asLongText();
            Long count = concurrentHashMap.getOrDefault(key, 0L);
            //将该事件消息强转为心跳事件
            IdleStateEvent idleStateHandler = (IdleStateEvent) evt;
            IdleState state = idleStateHandler.state();
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
        } else {
            // 事件不是一个IdleStateEvent的话, 就将它传递给下一个处理程序
            super.userEventTriggered(ctx, evt);
        }
    }
}