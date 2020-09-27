package com.example.niotest;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public class TestBlockingIO {
    //客户端
    @Test
    public void client(){
        SocketChannel socketChannel = null;
        FileChannel inChannel = null;
        try {
            //获取通道
            socketChannel= SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));

            inChannel= FileChannel.open(Paths.get("avatar.jpeg"), StandardOpenOption.READ);
            //分配指定大小的缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);
            //读取本地文件，并发送到服务器
            while (inChannel.read(buf)!=-1){
                buf.flip();
                socketChannel.write(buf);
                buf.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭通道
            if(inChannel!=null){
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(socketChannel!=null){
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //服务器
    @Test
    public void server(){
        ServerSocketChannel serverSocketChannel = null;
        SocketChannel accept = null;
        try {
            //获取通道
            serverSocketChannel = ServerSocketChannel.open();

            FileChannel outChannel = FileChannel.open(Paths.get("avatar2.jpeg"),
                    StandardOpenOption.WRITE, StandardOpenOption.READ,StandardOpenOption.CREATE_NEW);
            //绑定连接
            serverSocketChannel.bind(new InetSocketAddress(9898));
            //获取客户端连接的通道
            accept = serverSocketChannel.accept();
            //分配指定大小的缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);
            //接受客户端的数据并保存到本地
            while (accept.read(buf)!=-1){
                buf.flip();
                outChannel.write(buf);
                buf.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭通道
            if(accept!=null){
                try {
                    accept.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(serverSocketChannel!=null){
                try {
                    serverSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}

