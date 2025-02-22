package com.hong;

import com.hong.client.handler.HeartBeatClientHandler;
import com.hong.client.handler.SocketClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class SocketClient {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private final static String host = "127.0.0.1";

    private final static int port = 1234;

    private final static int firstMessageSize = 10;

    private Channel channel;

    private Bootstrap bootstrap;

    private EventLoopGroup group;

    public void run() throws Exception {
        // Configure the client.
        group = new NioEventLoopGroup();
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline channelPipeline = ch.pipeline();
                            channelPipeline.addLast(new IdleStateHandler(0, 5, 0));
                            channelPipeline.addLast(new HeartBeatClientHandler());
                            channelPipeline.addLast(new LengthFieldPrepender(4));
                            channelPipeline.addLast(new SocketClientHandler(firstMessageSize));
//                            .addLast("encoder", new StringEncoder())
//                            .addLast("decoder", new StringDecoder());
                        }
                    });

            // Start the client.
            ChannelFuture f = bootstrap.connect(host, port).sync();
            channel = f.channel();
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }

    }


    public void sendMsg(String text) {
        channel.writeAndFlush(Unpooled.copiedBuffer(text, StandardCharsets.UTF_8));
//        channel.writeAndFlush(text);
    }


    public void shutDown() {
        if (channel != null && channel.isActive()) {
            channel.close().awaitUninterruptibly();
            channel = null;
        }
        bootstrap = null;
        if (group != null) {
            group.shutdownGracefully();
        }
        group = null;
        logger.error("客户端关闭连接，雷达服务端信息为：{} : {}", host, port);
    }

}
