package com.example.niotest.time;

import com.example.niotest.utils.JedisUtils;
import redis.clients.jedis.Jedis;

import java.time.Instant;
import java.util.Set;

public class MyTaskTest {

    private static final String KEY = "task";

    public static void main(String[] args) throws InterruptedException {

        Jedis jedis = JedisUtils.getJedis();
        jedis.zadd(KEY, Instant.now().plusSeconds(10).getEpochSecond(),"time1");
        jedis.zadd(KEY, Instant.now().plusSeconds(2).getEpochSecond(),"time2");
        jedis.zadd(KEY, Instant.now().plusSeconds(2).getEpochSecond(),"time3");
        jedis.zadd(KEY, Instant.now().plusSeconds(5).getEpochSecond(),"time4");
        doDelay(jedis);
    }

    private static void doDelay(Jedis jedis) throws InterruptedException {
        while (true){
            long min = Instant.now().plusSeconds(-1).getEpochSecond();
            long now = Instant.now().getEpochSecond();
            Set<String> set = jedis.zrangeByScore(KEY, min, now);
            for (String s : set) {
                System.out.println(s);
            }
            jedis.zremrangeByScore(KEY,min,now);
            Thread.sleep(1000);
        }
    }
}
