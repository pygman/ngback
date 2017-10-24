package com.pygman.ngback.service;

import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLException;
import java.io.File;
import java.io.InputStream;

/**
 * 构造 项目相关 SslContext
 * Created by pygman on 16-11-21.
 */
@Component
public class SslContextMaker {

    private final ClassLoader classLoader = SslContextMaker.class.getClassLoader();

    @Value("${ca.folder}")
    private String PATH;

    @Value("${ca.root}")
    private String ROOT_CA;
    @Value("${ca.monitor.crt}")
    private String MONITOR_CRT;
    @Value("${ca.server.crt}")
    private String SERVER_CRT;
    @Value("${ca.client.crt}")
    private String CLIENT_CRT;

    @Value("${ca.monitor.key}")
    private String MONITOR_KEY;
    @Value("${ca.server.key}")
    private String SERVER_KEY;
    @Value("${ca.client.key}")
    private String CLIENT_KEY;

    private boolean useResources;

    @PostConstruct
    public void init() {
        if ("default".equals(PATH)) useResources = true;
        else if (!PATH.endsWith("/")) PATH += '/';
    }

    public SslContext monitorSslContext() {
        return useResources ? forServer(MONITOR_CRT, MONITOR_KEY) : forServerUseFolder(MONITOR_CRT, MONITOR_KEY);
    }

    public SslContext serverSslContext() {
        return useResources?forServer(SERVER_CRT, SERVER_KEY): forServerUseFolder(SERVER_CRT, SERVER_KEY);
    }

    public SslContext serverRegisterSslContext() {
        return useResources?forClient(SERVER_CRT, SERVER_KEY): forClientUseFolder(SERVER_CRT, SERVER_KEY);
    }


    public SslContext clientRegisterSslContext() {
        return useResources?forClient(CLIENT_CRT, CLIENT_KEY): forClientUseFolder(CLIENT_CRT, CLIENT_KEY);
    }


    public SslContext clientSslContext() {
        return useResources?forClient(CLIENT_CRT, CLIENT_KEY): forClientUseFolder(CLIENT_CRT, CLIENT_KEY);
    }

    private SslContext forServer(String crtPath, String keyPath) {
        InputStream crt = classLoader.getResourceAsStream(crtPath);
        InputStream key = classLoader.getResourceAsStream(keyPath);
        InputStream root = classLoader.getResourceAsStream(ROOT_CA);
        SslContext sslCtx = null;
        try {
            sslCtx = SslContextBuilder.forServer(crt, key)
                    .trustManager(root)
                    .clientAuth(ClientAuth.REQUIRE)
                    .build();
        } catch (SSLException e) {
            //TODO 启动失败
            e.printStackTrace();
        }
        return sslCtx;
    }

    private SslContext forServerUseFolder(String crtPath, String keyPath) {
        File crt = new File(PATH + crtPath);
        File key = new File(PATH + keyPath);
        File root = new File(PATH + ROOT_CA);
        SslContext sslCtx = null;
        try {
            sslCtx = SslContextBuilder.forServer(crt, key)
                    .trustManager(root)
                    .clientAuth(ClientAuth.REQUIRE)
                    .build();
        } catch (SSLException e) {
            //TODO 启动失败
            e.printStackTrace();
        }
        return sslCtx;
    }

    private SslContext forClient(String crtPath, String keyPath) {
        InputStream crt = classLoader.getResourceAsStream(crtPath);
        InputStream key = classLoader.getResourceAsStream(keyPath);
        InputStream root = classLoader.getResourceAsStream(ROOT_CA);
        SslContext sslCtx = null;
        try {
            sslCtx = SslContextBuilder.forClient()
                    .keyManager(crt, key)
                    .trustManager(root).build();
        } catch (SSLException e) {
            //TODO 启动失败
            e.printStackTrace();
        }
        return sslCtx;
    }

    private SslContext forClientUseFolder(String crtPath, String keyPath) {
        File crt = new File(PATH + crtPath);
        File key = new File(PATH + keyPath);
        File root = new File(PATH + ROOT_CA);
        SslContext sslCtx = null;
        try {
            sslCtx = SslContextBuilder.forClient()
                    .keyManager(crt, key)
                    .trustManager(root).build();
        } catch (SSLException e) {
            //TODO 启动失败
            e.printStackTrace();
        }
        return sslCtx;
    }
}
