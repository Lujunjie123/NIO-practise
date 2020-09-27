package com.example.niotest;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class TestBlockingIO2 {
    @Test
    public void client(){
        SocketChannel socketChannel = null;
        FileChannel inChannel = null;
        try {
            //获取通道
            socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));

            inChannel = FileChannel.open(Paths.get("avatar.jpeg"), StandardOpenOption.READ);
            ByteBuffer buf = ByteBuffer.allocate(1024);

            while(inChannel.read(buf)!=-1){
                buf.flip();
                socketChannel.write(buf);
                buf.clear();
            }
            //告诉服务器我已经发送完了
            socketChannel.shutdownOutput();

            //接受服务端的反馈
            int len = 0;
            while ((len=socketChannel.read(buf))!=-1){
                buf.flip();
                System.out.println(new String(buf.array(), 0, len));
                buf.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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
        @Test
        public void server() {
            ServerSocketChannel serverSocketChannel = null;
            SocketChannel accept = null;
            FileChannel outChannel = null;
            try {
                serverSocketChannel = ServerSocketChannel.open();

                outChannel = FileChannel.open(Paths.get("avatar2.jpeg"),
                        StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE_NEW);
                serverSocketChannel.bind(new InetSocketAddress(9898));
                accept = serverSocketChannel.accept();

                ByteBuffer buf = ByteBuffer.allocate(1024);
                while(accept.read(buf)!=-1){
                    buf.flip();
                    outChannel.write(buf);
                    buf.clear();
                }
                //给客户端发送反馈
                buf.put("服务端成功接受客户端的数据".getBytes());
                buf.flip();
                accept.write(buf);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(accept!=null){
                    try {
                        accept.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(outChannel!=null){
                    try {
                        outChannel.close();
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
















































