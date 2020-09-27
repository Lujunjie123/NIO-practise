package com.example.niotest;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.file.Files;

public class TestPipe {

    @Test
    public void test() throws IOException {
        //获取通道
        Pipe pipe = Pipe.open();
        //将缓冲区中的数据写入通道
        Pipe.SinkChannel sinkChannel = pipe.sink();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put("通过单向管道传输数据".getBytes());
        buffer.flip();
        sinkChannel.write(buffer);
        //读取数据
        Pipe.SourceChannel source = pipe.source();
        buffer.flip();
        int len = source.read(buffer);
        System.out.println(new String(buffer.array(), 0, len));

    }
}
