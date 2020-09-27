package com.example.niotest;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

public class TestBuffer {
    @Test
    public void test1(){
        String str = "abcde";
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put(str.getBytes());
        buf.flip();     //切换为读取数据模式
        byte[] bytes = new byte[buf.limit()];
        buf.get(bytes,0,2);
        System.out.println(new String(bytes, 0, 2));	//ab
        System.out.println(buf.position()); //2
        buf.mark();
        buf.get(bytes,2,2);
        System.out.println(new String(bytes, 2, 2));//cd
        System.out.println(buf.position()); //4
        buf.reset();
        System.out.println(buf.position());//2
        if(buf.hasRemaining()){
            System.out.println(buf.remaining());	//3
        }
        //判断是不是直接缓冲区
        System.out.println(buf.isDirect());		//false  非直接缓存区
    }

    @Test
    void contextLoads() {
        String str = "abcde";
        ByteBuffer buf = ByteBuffer.allocate(1024);
        System.out.println(buf.position());  //0
        System.out.println(buf.limit());     //1024
        System.out.println(buf.capacity());  //1024

        //利用put()存入缓冲区数据
        System.out.println("put=================================");

        buf.put(str.getBytes());
        System.out.println(buf.position());  //5
        System.out.println(buf.limit());     //1024
        System.out.println(buf.capacity());  //1024

        //切换读取数据模式
        System.out.println("flip=================================");
        buf.flip();
        System.out.println(buf.position());  //0
        System.out.println(buf.limit());     //5
        System.out.println(buf.capacity());  //1024

        //利用get()读取缓冲区数据
        System.out.println("get=================================");
        byte[] bytes = new byte[buf.limit()];
        buf.get(bytes,0,bytes.length);
        System.out.println(new String(bytes, 0, bytes.length));//abcde
        System.out.println(buf.position());  //5
        System.out.println(buf.limit());     //5
        System.out.println(buf.capacity());  //1024

        //可重复读
        System.out.println("rewind=================================");
        buf.rewind();
        System.out.println(buf.position());  //0
        System.out.println(buf.limit());     //5
        System.out.println(buf.capacity());  //1024

        //clear 清空缓存区
        System.out.println("clear=================================");
        buf.clear();
        System.out.println(buf.position());  //0
        System.out.println(buf.limit());     //1024
        System.out.println(buf.capacity());  //1024
    }
}
