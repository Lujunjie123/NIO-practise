package com.example.niotest.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpServerCodec;

//初始化器，channel注册后，会执行里面的相应的初始化方法
public class HelloServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //每一个channel由多个handler共同组成管道(pipeline)

        //获取对应的管道
        ChannelPipeline pipeline = socketChannel.pipeline();

        //HttpServerCodec是netty自带的助手类，当请求到服务端，我们需要解码，响应的客户端做编码
        //WebSocket基于http协议 所有要有http编解码器
        pipeline.addLast("HttpServerCodec", new HttpServerCodec());
        //添加自定义助手类，返回"hello netty~"
        pipeline.addLast("customHandler",new CustomHandler());
    }
}























