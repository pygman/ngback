# ngback 内网消息通讯中间件
> 基本仿照ngrok功能实现并扩展 分布式高可用系统 保证消息送达 断线重连 重发机制

## 使用步骤
    1. monitor启动参数 --center=${monitor的ip:port}
        java -jar monitor-0.6.0-exec.jar --center=0.0.0.0:4399
    2. server启动参数 --center=${monitor的ip:port} --server=${自身server的ip:port}
        java -jar server-0.6.0-exec.jar --center=0.0.0.0:4399 --server=0.0.0.0:9898
    3. client启动参数 --center=${monitor的ip:port} --name=${自身client的名字} --host=${代理内网服务的ip:port} --server.port=${接收请求的端口}
        java -jar client-0.6.0-exec.jar --center=0.0.0.0:4399 --name=client0
    4. 例: 向代理client:client0发送消息，使代理访问其host指定服务:
        访问任意一台client的 http://ip:port/client0/${原服务路径}

## 基本流程 TODO
    1. server 端 配置 http-port 、 server-port 启动
    2. server 端 server-port 监听 client 请求
    3. client 端 配置 http-ip 、 server-port 、 http-port 启动
    4. client 端 根据配置 与 server通信  证书验证 短连接
    5. 验证通过 server端 在50000以上选定一个端口 返回client端 server与client在该端口建立工作长连接通道
    6. server 端 接收http请求 并根据约定 发送到client 同步阻塞等待 
    7. client 端 模拟请求 本地环境 将返回值发送给server端
    8. server 端 获得返回数据 响应6中的http请求
    
---------