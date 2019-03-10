package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entities.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void testSeckill() {
        Long seckillId = 1001l;
        //先从缓存中获取seckill对象
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null){
            //缓存中没有数据则从数据库中读取
            Seckill newSeckill = seckillDao.queryById(seckillId);
            System.out.println("从数据库读取的对象：" + newSeckill);
            //将数据库中读取到的数据存储在缓存中
            String result = redisDao.putSeckill(newSeckill);
            System.out.println("缓存状态回应码：" + result);
        } else {
            //从缓存中读取到数据了
            System.out.println("从缓存中读取的对象：" + seckill);
        }
    }

}