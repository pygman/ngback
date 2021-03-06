package com.pygman.ngback.server.boot;

import com.pygman.ngback.codec.MessageDecoder;
import com.pygman.ngback.codec.MessageEncoder;
import com.pygman.ngback.server.handler.ServerRegIdle;
import com.pygman.ngback.server.handler.ServerRegister;
import com.pygman.ngback.service.SslContextMaker;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 服务端注册器
 * Created by pygman on 16-10-27.
 */
@Component
public class ServerRegisterMain {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Lazy
    @Autowired
    ServerBoot serverBoot;

    @Autowired
    ServerRegister serverRegister;

    @Autowired
    ServerRegIdle serverRegIdle;

    @Autowired
    SslContextMaker sslContextMaker;

    EventLoopGroup group;

    @Async
    public void start(final String host, final int port) {

        final SslContext sslContext = sslContextMaker.serverRegisterSslContext();

        group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true);
            bootstrap.handler(new LoggingHandler(LogLevel.INFO));

            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(sslContext.newHandler(ch.alloc()));
                    pipeline.addLast(new WriteTimeoutHandler(60, TimeUnit.SECONDS));
//                    pipeline.addLast("serverRegHeartBeat",serverRegHeartBeat);
                    pipeline.addLast("framedecoder", new LengthFieldBasedFrameDecoder(1024 * 1024 * 1024, 0, 4, 0, 0));
                    pipeline.addLast("messageDecoder", new MessageDecoder());
                    pipeline.addLast("messageEncoder", new MessageEncoder());
                    pipeline.addLast("idleStateHandler", new IdleStateHandler(0, 0, 10, TimeUnit.SECONDS));
                    pipeline.addLast("serverRegIdle", serverRegIdle);
                    pipeline.addLast("serverRegister", serverRegister);

                }
            });

            ChannelFuture future = bootstrap.connect(host, port).sync();

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            // 所有资源释放完成之后，清空资源，再次发起重连操作
            logger.info("---serverBoot.restartRegister:"+serverBoot+"---");
            serverBoot.restartRegister();
        }
    }

    void shutdown() {
        try {
            group.shutdownGracefully();
            logger.info("Exit...");
        } catch (Exception e) {
            logger.error("退出异常" + e.getMessage());
        }
    }
}
