package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entities.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void testInsertSuccessKilled(){
        int insertCount = successKilledDao.insertSuccessKilled(1000l, 15719532680l);
        System.out.println(insertCount);
    }

    @Test
    public void testQueryByIdWithSeckill(){
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(1000l, 15719532680l);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }
}