package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entities.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    //自动注入SeckillDao类
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void testQueryById(){
        Seckill seckill = seckillDao.queryById(1000l);
        System.out.println(seckill.getGname());
        System.out.println(seckill);
        /*
        1000元秒杀iphone6
        Seckill{seckillId=1000, gname='1000元秒杀iphone6', number=100,
        startTime=Thu Nov 01 00:00:00 CST 2018,
        endTime=Fri Nov 02 00:00:00 CST 2018,
        createTime=Thu Mar 07 22:25:20 CST 2019}
         */
    }

    @Test
    public void tsetQueryAll(){
        List<Seckill> seckills = seckillDao.queryAll(0,10);
        for (Seckill s: seckills) {
            System.out.println(s);
        }
    }

    @Test
    public void testReduceNumber(){
        Date date = new Date();
        int num = seckillDao.reduceNumber(1000l, date);
        System.out.println(num);
    }
}