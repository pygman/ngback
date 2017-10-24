package com.pygman.ngback.client.boot;

import com.pygman.ngback.client.core.ServerTableManager;
import com.pygman.ngback.client.handler.ClientIdle;
import com.pygman.ngback.client.handler.ClientLogin;
import com.pygman.ngback.client.handler.ClientServiceReqHandler;
import com.pygman.ngback.client.handler.ClientServiceRespHandler;
import com.pygman.ngback.codec.MessageDecoder;
import com.pygman.ngback.codec.MessageEncoder;
import com.pygman.ngback.service.SslContextMaker;
import com.pygman.ngback.struct.Pair;
import com.pygman.ngback.util.IPParser;
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
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 客户端主执行
 * Created by pygman on 2016/9/14.
 */
@Component
public class ClientMain {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Lazy
    @Autowired
    ClientBoot clientBoot;

    @Autowired
    ClientLogin clientLogin;

    @Autowired
    ClientServiceReqHandler clientServiceReqHandler;

    @Autowired
    ClientServiceRespHandler clientServiceRespHandler;

    @Autowired
    ClientIdle clientIdle;

    @Autowired
    ServerTableManager serverTableManager;

    @Autowired
    IPParser ipParser;

    @Autowired
    SslContextMaker sslContextMaker;

    public interface OnMessageReadListener {
        String onMessageRead(Object object);
    }

    @Async
    void start(final String serverIPStr, Pair<EventLoopGroup, Channel> nettyPair) {
        EventLoopGroup group = new NioEventLoopGroup();
        nettyPair.setFirst(group);
        start(serverIPStr, group, nettyPair);
    }

    @Async
    public void start(final String serverIPStr, final EventLoopGroup group) {
        Pair<EventLoopGroup, Channel> nettyPair = new Pair<>();
        nettyPair.setFirst(group);
        start(serverIPStr, group, nettyPair);
    }

    @Async
    public void start(final String serverIPStr, final EventLoopGroup group, final Pair<EventLoopGroup, Channel> nettyPair) {

        Pair<String, Integer> ipPair;
        try {
            ipPair = ipParser.parse(serverIPStr);
        } catch (Exception e) {
            logger.error("客户端获取异常服务器");
            e.printStackTrace();
            //TODO 通知ServerTableManager启动失败
            return;
        }

        String host = ipPair.getFirst();
        int port = ipPair.getSecond();

        final SslContext sslContext = sslContextMaker.clientSslContext();

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
                    pipeline.addLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS));
                    pipeline.addLast("framedecoder", new LengthFieldBasedFrameDecoder(1024 * 1024 * 1024, 0, 4, 0, 0));
                    pipeline.addLast("messageDecoder", new MessageDecoder());
                    pipeline.addLast("messageEncoder", new MessageEncoder());
//                    pipeline.addLast("heartBeatHandler", clientHeartBeatHandler);
                    pipeline.addLast("idleStateHandler", new IdleStateHandler(0, 0, 10, TimeUnit.SECONDS));
                    pipeline.addLast("clientServiceReqHandler", clientServiceReqHandler);
                    pipeline.addLast("clientServiceRespHandler", clientServiceRespHandler);
                    pipeline.addLast("clientIdle", clientIdle);
                    pipeline.addLast("clientLogin", clientLogin);
                }
            });

            ChannelFuture future = bootstrap.connect(host, port).sync();

            nettyPair.setSecond(future.channel());

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("---new connection exception before---");
			String errorMessage = e.getMessage().toLowerCase();
			logger.error("---e.getMessage().toLowerCase():"+errorMessage+"---");
            if (errorMessage.contains("connection timed out") || errorMessage.contains("unknown error")){
            	logger.debug("---happen exception,restart register client---");
                clientBoot.restartRegister();
            }
            logger.error("---new connection exception after---");
        } finally {
            // TODO 所有资源释放完成之后，清空资源，再次发起重连操作
            logger.info("---register finally start:"+serverIPStr+"---");
            group.shutdownGracefully();
            serverTableManager.shutdown(serverIPStr);
//            serverTableManager.restartWork(serverIPStr);
            logger.info("---register finally end:"+serverIPStr+"---");
        }

    }

    /*
    void shutdown() {
        try {
            group.shutdownGracefully();
            logger.info("Exit...");
        } catch (Exception e) {
            logger.error("退出异常" + e.getMessage());
        }
    }
    */
}
