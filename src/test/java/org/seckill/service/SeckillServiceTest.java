package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entities.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void testGetSeckillList() {
        List<Seckill> seckills = seckillService.getSeckillList();
        logger.info("list={}", seckills);
    }

    @Test
    public void testgetById() {
        Seckill seckill = seckillService.getById(1000l);
        logger.info("seckill={}", seckill);
    }

    @Test
    public void testSeckillLogic() {
        long seckillId = 1002l;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()) {
            logger.info("ecposer={}", exposer);
            long userPhone = 13475685462l;
            String md5 = exposer.getMd5();
            try {
                //执行秒杀
                SeckillExecution execution = seckillService.executeSeckill(seckillId, userPhone, md5);
                logger.info("execution={}", execution);
            } catch (SeckillCloseException e) {
                logger.error(e.getMessage());
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            }
        } else {
            //秒杀未开启或id错误等
            logger.warn("exposer={}", exposer);
        }
    }

    @Test
    public void testExportSeckillUrl() {
        Exposer exposer = seckillService.exportSeckillUrl(1000l);
        logger.info("ecposer={}", exposer);
        //ecposer=
        // Exposer{
        // exposed=true,
        // md5='607ee48a368c48a3926f6ef3f589f397',
        // seckillId=1000, now=0, start=0, end=0}
    }

    @Test
    public void testExecuteSeckill() {
        long id = 1000l;
        long userPhone = 13475685462l;
        String md5 = "607ee48a368c48a3926f6ef3f589f397";
        SeckillExecution execution = seckillService.executeSeckill(id, userPhone, md5);
        logger.info("execution={}", execution);
        //execution=
        // SeckillExecution{
        // seckillId=1000,
        // state=1,
        // stateInfo='秒杀成功',
        // successKilled=SuccessKilled{seckillId=1000, userPhone=13475685462, *state=0*, createTime=Fri Mar 08 21:20:57 CST 2019}}
    }

    @Test
    public void executeSeckillByProcedure() {
        long seckillId = 1001l;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()) {
            logger.info("ecposer={}", exposer);
            long userPhone = 15654123578l;
            String md5 = exposer.getMd5();
            try {
                //执行秒杀
                SeckillExecution execution = seckillService.executeSeckillByProcedure(seckillId, userPhone, md5);
                logger.info("execution={}", execution);
            } catch (SeckillCloseException e) {
                logger.error(e.getMessage());
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            }
        } else {
            //秒杀未开启或id错误等
            logger.warn("exposer={}", exposer);
        }
    }
}