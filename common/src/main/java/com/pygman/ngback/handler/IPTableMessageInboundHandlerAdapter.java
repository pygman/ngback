package com.pygman.ngback.handler;

import com.pygman.ngback.core.IPTable;
import com.pygman.ngback.struct.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 需释放资源Handler公共代码
 * Created by pygman on 16-11-7.
 */
@ChannelHandler.Sharable
public abstract class IPTableMessageInboundHandlerAdapter extends SimpleMessageInboundHandlerAdapter{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    protected abstract IPTable getTable();
    protected abstract void channelRead1(ChannelHandlerContext ctx, Message message);

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //移除client,关闭连接
        IPTable ipTable = getTable();
        Channel channel = ctx.channel();
        logger.info("channelInactive: before remove == "+ipTable.toString());
        ipTable.remove(channel);
        logger.info("channelInactive: after remove == "+ipTable.toString());
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //移除client,关闭连接
        IPTable ipTable = getTable();
        cause.printStackTrace();
        Channel channel = ctx.channel();
        logger.info("exceptionCaught: before remove == "+ipTable.toString());
        ipTable.remove(channel);
        logger.info("exceptionCaught: after remove == "+ipTable.toString());
        ctx.close();
    }

}
