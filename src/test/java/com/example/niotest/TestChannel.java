package com.example.niotest;
/*
    通道(Cannel) 用于源节点与目标节点的连接，在Java NIO中负责缓冲区中数据的传输，Channel本身不存储数据，因此需要配合缓冲区进行传输

    通道的主要实现类
    java.nio.channels.Channel接口：
        FileChannel
        SocketChannel
        ServerSocketChannel
        DatagramChannel

     获取通道
     java针对支持通道的类提供了getChannel()方法
        本地IO：
           FileInputStream/FileOutputStream
           RandomAccessFile

        网络IO
        Socket
        ServerSocket
        DatagramSocket

       在JDK1.7中的NIO.2针对各个通道提供了静态方法open()
       在JDK1.7中的NIO.2的Files工具类的newByteChannel()

       通道之间的数据传输
       transferFrom()
       transferTo()

        分散(Scatter)与聚集(Gather)
        分散读取(Scatter Reads) 将通道中的数据分散到多个缓冲区中
        聚集写入(Gathering Writes) 将多个缓冲区中的数据聚集到通道中

        字符集 Charset
        编码： 字符串->字节数组
        解码： 字节数组->字符串
 */

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class TestChannel {
    @Test
    public void test5() throws IOException {
        Charset charset = Charset.forName("GBK");
//        //编码器
//        CharsetEncoder charsetEncoder = charset.newEncoder();
//        //解码器
//        CharsetDecoder charsetDecoder = charset.newDecoder();

        CharBuffer charBuffer = CharBuffer.allocate(1024);
        charBuffer.put("你好！");
        charBuffer.flip();
        //编码
        ByteBuffer encode = charset.encode(charBuffer);
        for (int i = 0; i < 6; i++) {
            System.out.println(encode.get());
        }
        //解码
        encode.flip();
        CharBuffer decode = charset.decode(encode);
        System.out.println(decode);
    }

    //分散和聚集
    @Test
    public void test4(){
        RandomAccessFile raf1 = null;
        RandomAccessFile raf2 = null;
        try {
            raf1 = new RandomAccessFile("1.txt", "rw");
            FileChannel channel1 = raf1.getChannel();

            //分配指定大小的缓冲区
            ByteBuffer buf1 = ByteBuffer.allocate(100);
            ByteBuffer buf2 = ByteBuffer.allocate(1024);
            //分散读取
            ByteBuffer[] bufs = {buf1,buf2};
            channel1.read(bufs);

            for (ByteBuffer buf : bufs) {
                buf.flip();
            }
            System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));
            System.out.println("-------------------");
            System.out.println(new String(bufs[1].array(), 0, bufs[1].limit()));

            //聚集写入
            raf2 = new RandomAccessFile("2.txt", "rw");
            FileChannel channel2 = raf2.getChannel();
            channel2.write(bufs);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(raf1!=null){
                try {
                    raf1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(raf2!=null){
                try {
                    raf2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Test
    public void test3(){
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = FileChannel.open(Paths.get("avatar.jpeg"), StandardOpenOption.READ);
            outChannel = FileChannel.open(Paths.get("avatar3.jpeg"),
                    StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
//            inChannel.transferTo(0,inChannel.size(),outChannel);
            outChannel.transferFrom(inChannel,0,inChannel.size());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(outChannel!=null){
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inChannel!=null){
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //利用通道完成文件的复制(映射文件方式)   只能传输byte
    @Test
    public void test2(){
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            //获取通道
            inChannel = FileChannel.open(Paths.get("avatar.jpeg"), StandardOpenOption.READ);
            outChannel = FileChannel.open(Paths.get("avatar3.jpeg"),
                    StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
            //内存映射文件
            MappedByteBuffer inMapperBuf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
            MappedByteBuffer outMapperBuf = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

            //直接对缓冲区进行数据读写操作
            byte[] dst = new byte[inMapperBuf.limit()];
            inMapperBuf.get(dst);
            outMapperBuf.put(dst);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(outChannel!=null){
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inChannel!=null){
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //利用通道完成文件的复制(非直接缓冲区)
    @Test
    public void test1(){
        FileInputStream fis=null;
        FileOutputStream fos=null;
        FileChannel fisChannel = null;
        FileChannel fosChannel = null;
        try{
            fis = new FileInputStream("avatar.jpeg");
            fos = new FileOutputStream("avatar2.jpeg");

            //获取通道
            fisChannel = fis.getChannel();
            fosChannel = fos.getChannel();

            //分配缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);
            //将通道中的数据读取到缓存区中
            while (fisChannel.read(buf)!=-1){
                buf.flip(); //切换读取数据的模式
                //将缓冲区中的数据读取到通道中
                fosChannel.write(buf);
                buf.clear();    //清空缓存区
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(fosChannel!=null){
                try {
                    fosChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fisChannel!=null){
                try {
                    fisChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }









}











































