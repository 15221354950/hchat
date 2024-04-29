package com.hong.socket;

import com.hong.socket.components.ChatChannelHandler;
import com.hong.socket.components.HeartbeatServerHandle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class NetServer {
    private static Logger logger = LoggerFactory.getLogger(NetServer.class);

    @Resource
    private ChatChannelHandler chatChannelHandler;

    private int port = 1234;

//    public NetServer(int port) {
//        this.port = port;
//    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(1024, 10496, 1048576))
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(1024, 10496, 1048576))
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            logger.info("ch:{},pipeline:{}", ch, ch.pipeline().hashCode());
                            ByteBuf delemiter = Unpooled.buffer();
                            delemiter.writeBytes("$".getBytes());
                            //  这里就是解决数据过长问题,而且数据是以$结尾的
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(907200, true, true, delemiter));

                            //第一个参数设置未读时间,第二个参数设置为未写时间,第三个为都未进行操作的时间,单位秒
                            ch.pipeline().addLast(new IdleStateHandler(4, 8, 12));
                            //添加超时检查机制--事件消息捕获类
                            //在处理器该userEventTriggered方法中去处理 IdleStaateEvent(读空闲,写空闲,读写空闲)
                            ch.pipeline().addLast(new HeartbeatServerHandle());


//                            ch.pipeline().addLast("encoder", new StringEncoder());
//                            ch.pipeline().addLast("decoder", new StringDecoder());
                            //在这里添加ChannelHandler来处理数据
                            ch.pipeline().addLast(new ChatChannelHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

