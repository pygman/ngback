package com.pygman.ngback.server.boot;

import com.pygman.ngback.codec.MessageDecoder;
import com.pygman.ngback.codec.MessageEncoder;
import com.pygman.ngback.server.handler.*;
import com.pygman.ngback.service.SslContextMaker;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 服务端
 * Created by pygman on 2016/9/13.
 */
@Component
public class ServerMain {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ServiceHandler serviceHandler;

    @Autowired
    RouteHandler routeHandler;

    @Autowired
    ReRouteHandler reRouteHandler;

    @Autowired
    IdleHandler idleHandler;

    @Autowired
    LoginHandler loginHandler;

    @Autowired
    ServerRegIdle serverRegIdle;

    @Autowired
    SslContextMaker sslContextMaker;

    EventLoopGroup boss;
    EventLoopGroup worker;

    @Async
    void start(Integer serverPort) {

        final SslContext sslContext = sslContextMaker.serverSslContext();

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker).channel(NioServerSocketChannel.class);
            bootstrap.handler(new LoggingHandler(LogLevel.INFO));
            bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                    ChannelPipeline pipeline = nioSocketChannel.pipeline();
                    pipeline.addLast(sslContext.newHandler(nioSocketChannel.alloc()));
                    pipeline.addLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS));
                    pipeline.addLast("framedecoder", new LengthFieldBasedFrameDecoder(1024 * 1024 * 1024, 0, 4, 0, 0));
                    pipeline.addLast("messageDecoder", new MessageDecoder());
                    pipeline.addLast("messageEncoder", new MessageEncoder());
//                    pipeline.addLast("heartBeatHandler", heartBeatHandler);
                    pipeline.addLast("routeHandler", routeHandler);
                    pipeline.addLast("reRouteHandler", reRouteHandler);
                    pipeline.addLast("serviceHandler", serviceHandler);
                    pipeline.addLast("idleHandler", idleHandler);
                    pipeline.addLast("loginHandler", loginHandler);
                }
            });
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            ChannelFuture future = bootstrap.bind(serverPort).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    void shutdown() {
        logger.info("Exit...Start");
        //TODO 不接收新来信息 现有信息处理完成 或持久化
        if(boss != null) boss.shutdownGracefully();
        if(worker != null) worker.shutdownGracefully();
        logger.info("Exit...End");
    }

}
