package com.example.niotest.time;

import com.example.niotest.utils.JedisUtils;
import redis.clients.jedis.Jedis;
import java.time.Instant;
import java.util.Set;

public class DelayQueueExample {
    // zset key
    private static final String _KEY = "myTaskQueue";
    
    public static void main(String[] args) throws InterruptedException {

        Jedis jedis = JedisUtils.getJedis();

        // 30s 后执行
        long delayTime = Instant.now().plusSeconds(20).getEpochSecond();
        jedis.zadd(_KEY, delayTime, "order_1");
        // 继续添加测试数据
        jedis.zadd(_KEY, Instant.now().plusSeconds(2).getEpochSecond(), "order_2");
        jedis.zadd(_KEY, Instant.now().plusSeconds(2).getEpochSecond(), "order_3");
        jedis.zadd(_KEY, Instant.now().plusSeconds(7).getEpochSecond(), "order_4");
        jedis.zadd(_KEY, Instant.now().plusSeconds(10).getEpochSecond(), "order_5");
        // 开启定时任务队列
        doDelayQueue(jedis);
    }

    /**
     * 定时任务队列消费
     * @param jedis Redis 客户端
     */
    public static void doDelayQueue(Jedis jedis) throws InterruptedException {
        while (true) {
            // 当前时间
            Instant nowInstant = Instant.now();
            long lastSecond = nowInstant.plusSeconds(-1).getEpochSecond(); // 上一秒时间
            long nowSecond = nowInstant.getEpochSecond();
            // 查询当前时间的所有任务
            Set<String> data = jedis.zrangeByScore(_KEY, lastSecond, nowSecond);
            for (String item : data) {
                // 消费任务
                System.out.println("消费：" + item);
            }
            // 删除已经执行的任务
            jedis.zremrangeByScore(_KEY, lastSecond, nowSecond);
            Thread.sleep(1000); // 每秒查询一次
        }
    }
}