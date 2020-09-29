package com.example.niotest.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class WSServer {

    public static void main(String[] args) throws InterruptedException {
        //创建主从线程组
        EventLoopGroup mainGroup = new NioEventLoopGroup();
        EventLoopGroup subGroup = new NioEventLoopGroup();

        try {
            //netty服务器的创建，serverBootstrap是一个启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(mainGroup,subGroup)       // //设置主从线程组
                    .channel(NioServerSocketChannel.class)  ////设置nio的双向通道
                    .childHandler(new WsSeverInitializer());////子处理器，用于处理childGroup
            ChannelFuture future = serverBootstrap.bind(8800).sync();
            //监听关闭的通道
            future.channel().closeFuture().sync();
        } finally {
            ////优雅方式关闭
            mainGroup.shutdownGracefully();
            subGroup.shutdownGracefully();
        }
    }
}






































































