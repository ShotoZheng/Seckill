package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entities.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

public interface SeckillService {

    /**
     * @Description  查询所有秒杀记录
     * @Param
     * @return
     */
    List<Seckill> getSeckillList();
    
    /**
     * @Description 通过id来查询获取秒杀记录 
     * @Param 
     * @return 
     */
    Seckill getById(long seckillId);
    
    /**
     * @Description 秒杀开启时输出秒杀接口地址，否则输出系统时间和秒杀时间
     * @Param 
     * @return 
     */
    Exposer exportSeckillUrl(long seckillId);
    
    /**
     * @Description 执行秒杀操作，带有加密机制md5，防止加密被串改
     * 方法会抛出父异常及其子异常，方便web层分别处理
     * @Param 
     * @return 
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
    throws SeckillCloseException, RepeatKillException, SeckillException;

    /**
     * @Description 通过存储过程执行秒杀的操作
     * @Param 
     * @return 
     */
    SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5);
}
