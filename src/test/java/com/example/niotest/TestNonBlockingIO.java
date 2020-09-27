package com.example.niotest;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Iterator;


/*
    使用NIO完成网络通信的三个核心
    1.通道(Channel) 负责连接
        java.nio.channels.Channel 接口
          |--SelectableChannel
            |--SocketChannel
            |--ServerSocketChannel
            |--DatagramChannel

            |--Pipe.SinkChannel
            |--Pipe.SourceChannel
     2.缓冲区(Buffer) 负责数据的存储

     3.选择器(Selector) 是SelectableChannel的多路复用器，用于监控SelectableChannel的IO状况

 */
public class TestNonBlockingIO {

    //客户端
    @Test
    public void client() throws IOException {
        //获取通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
        //切换为非阻塞模式
        socketChannel.configureBlocking(false);
        //分配指定大小的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

        //发送数据给服务端
        buffer.put(Calendar.getInstance().getTime().toString().getBytes());
        buffer.flip();
        socketChannel.write(buffer);
        buffer.clear();
        //关闭通道
        socketChannel.close();
    }

    //服务端
    @Test
    public void server() throws IOException {
        //1.获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //2.切换为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //3.绑定连接
        serverSocketChannel.bind(new InetSocketAddress(9898));
        //4.获取selector
        Selector selector = Selector.open();
        //5.将通道注册到选择器中，并指定"监听接受事件"
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //6.轮询式的获取选择器上已经"准备就绪"的事件
        while(selector.select()>0){
            //7.获取选择器中所有注册的"选择键(已就绪的监听事件)"
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()){
                //8.获取准备"就绪"的事件
                SelectionKey selectionKey = iterator.next();
                //9.判断具体是什么事件准备就绪
                if(selectionKey.isAcceptable()){
                    //10.获取客户端连接的通道
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    //11.切换为非阻塞模式
                    socketChannel.configureBlocking(false);
                    //12.将通道注册到选择器上
                    socketChannel.register(selector,SelectionKey.OP_READ);
                }else if(selectionKey.isReadable()){
                    //13.获取当前键上"读就绪"状态的通道
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    //14.读取数据
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int len = 0;
                    while((len =socketChannel.read(buffer))!=-1){
                        buffer.flip();
                        System.out.println(new String(buffer.array(), 0, len));
                        buffer.clear();
                    }
                }
                //15.取消选择键
                iterator.remove();
            }
        }

    }
}
