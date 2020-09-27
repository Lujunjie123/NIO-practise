package com.example.niotest.time;

import com.example.niotest.utils.JedisUtils;
import redis.clients.jedis.Jedis;

public class TaskExample2 {
    public static final String _TOPIC = "__keyevent@0__:expired"; // 订阅频道名称
    public static void main(String[] args) {
        Jedis jedis = JedisUtils.getJedis();
        // 执行定时任务
        jedis.publish(_TOPIC,"hello");
    }
}