package org.seckill.dao;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entities.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author 郑松涛
 * @description:
 * @create 2019-03-10
 * @since 1.0.0
 */
public class RedisDao {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //JedisPool为Redis的连接池对象
    private final JedisPool jedisPool;

    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }

    /**
     * @return
     * @Description 从缓存中获取Seckill对象
     * @Param
     */
    public Seckill getSeckill(Long seckillId) {
        try {
            //从连接池中获取单个连接
            Jedis jedis = jedisPool.getResource();
            try {
                // 通过key查询获取对应的对象
                String key = "seckill:" + seckillId;
                // 因为Redis内部并没有实现序列化与反序列化操作，考虑到Java内部的序列化的性能，
                // 这里使用自定义的序列化工具protostuff
                //获取Seckill对象为反序列化操作：
                // 步骤：get -> byte[] -> 反序列化 -> Seckill对象
                byte[] bytes = jedis.get(key.getBytes());
                if (bytes != null) {
                    //创建一个空对象Seckill
                    Seckill seckill = schema.newMessage();
                    //seckill被反序列化
                    ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
                    return seckill;
                }
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * @return
     * @Description 将Seckill对象放进缓存中
     * @Param
     */
    public String putSeckill(Seckill seckill) {
        //反序列过程：seckill对象 -> byte[] -> set
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckill.getSeckillId();
                //LinkedBuffer为序列化过程的缓存器，默认大小
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //超时缓存，缓存最大时限为一个小时
                int timeout = 60 * 60;
                //result为回应状态码
                String result = jedis.setex(key.getBytes(), timeout, bytes);
                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}

