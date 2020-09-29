package com.example.niotest.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

//实现客户端发送一个请求，服务器会返回hello netty
public class HelloServer {

    public static void main(String[] args) throws InterruptedException {
        //创建一对主从线程组
//        主线程组，用于接受客户端的连接，但是不做任何处理，交给从现场组处理
        EventLoopGroup parentGroup = new NioEventLoopGroup();
//        从线程组，处理主线程组的任务
        EventLoopGroup childGroup = new NioEventLoopGroup();

        try {
            //netty服务器的创建，serverBootstrap是一个启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(parentGroup,childGroup)   //设置主从线程组
                    .channel(NioServerSocketChannel.class)  //设置nio的双向通道
                            .childHandler(new HelloServerInitializer());            //子处理器，用于处理childGroup
            //启动server,开启启动方式为8088的端口，设置启动方式为同步
            ChannelFuture future = serverBootstrap.bind(8088).sync();

            //监听关闭的通道，设置为同步方式
            future.channel().closeFuture().sync();
        } finally {
            //优雅方式关闭
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }
}




































































