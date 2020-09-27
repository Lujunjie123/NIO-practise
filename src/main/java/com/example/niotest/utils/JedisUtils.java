package com.example.niotest.utils;

import redis.clients.jedis.Jedis;

public class JedisUtils {

    public static Jedis getJedis(){
        return new Jedis("192.168.1.104",6379);
    }
}
