package com.example.niotest.netty.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Date;

//TextWebSocketFrame：在Netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
public class WSChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //用于记录和管理所有客户端的channel
    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //获取客户端发来的消息
        String text = msg.text();
        System.out.println("服务端接受到消息:" + text);

//        for (Channel client : clients) {
//            client.writeAndFlush(msg);
//        }
        //这个方法和上面的for一致
        clients.writeAndFlush(new TextWebSocketFrame("服务器在"+new Date() +"接受到消息，消息为："+text));
    }

    //当客户端连接服务器之后(打开连接)
    //获取客户端的channel，并且放到ChannelGroup中去进行管理
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        clients.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //当触发handlerRemoved ChannelGroup会自动移除对应客户端的channel
//        clients.remove(ctx);
        System.out.println("客户端断开，channel对应的长id为"+ctx.channel().id().asLongText());
        System.out.println("客户端断开，channel对应的短id为"+ctx.channel().id().asShortText());
    }
}



















