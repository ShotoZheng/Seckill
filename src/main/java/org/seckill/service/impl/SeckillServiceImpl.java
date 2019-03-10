package org.seckill.service.impl;

import org.apache.commons.collections4.MapUtils;
import org.seckill.dao.RedisDao;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entities.Seckill;
import org.seckill.entities.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 郑松涛
 * @description: 秒杀业务实现类
 * @create 2019-03-08
 * @since 1.0.0
 */
@Service
public class SeckillServiceImpl implements SeckillService {
    //统一日志API
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    //md5盐值字符串，用于混淆MD5
    private final String slat = "gaudhq878%^%^$8979*/$%#$(YhhJHgy246841)_+";

    /**
     * @Description queryAll方法传入的offset和limit值应该从前端的分页信息传递过来，
     * 以实现数据库查询表时实现物理分页。
     * @Param
     * @return
     * //TODO
     */
    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //使用Redis优化查询
        //读取缓存
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            //缓存没有数据，读取数据库
            seckill = seckillDao.queryById(seckillId);
            //没有查到指定的秒杀产品记录
            if (seckill == null)
                return new Exposer(false, seckillId);
            else {
                //存储进缓存中
                String result = redisDao.putSeckill(seckill);
                logger.info("存储进缓存={}", result);
            }
        }
        //当前系统时间
        long nowTime = new Date().getTime();
        long startTime = seckill.getStartTime().getTime();
        long endTime = seckill.getEndTime().getTime();
        //秒杀还没开始或秒杀已经结束
        if (nowTime < startTime || nowTime > endTime)
            return new Exposer(false, seckillId, nowTime, startTime, endTime);
        //秒杀开启时暴露秒杀接口地址
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        //md5DigestAsHex该方法专门用于生成MD5的Spring提供的方法
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    /**
     * @return
     * @Description 执行秒杀操作
     * @Param
     */
    //声明为事务方法
    @Transactional
    @Override
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillException {
        //在执行秒杀时或传入的md5不一致（篡改等）则抛出异常
        if (md5 == null || !md5.equals(getMD5(seckillId)))
            throw new SeckillException("seckill data rewrite");
        //获取秒杀的执行时间
        Date nowTime = new Date();
        try {
            //插入购买行为信息到成功明细表中
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            if (insertCount <= 0) {
                //插入失败返回0，抛出异常
                throw new RepeatKillException("seckill repeated");
            } else {
                //插入成功。执行减库存操作，*获取行级锁*。
                /*
                注意：在执行update语句时才会获取行级锁，因为该update语句是通过索引条件来更新数据的，即
                使用行级锁一定要使用索引。而insert语句不具有索引条件，故在进行更新时才会获取行级锁减少
                锁持有时间，优化速度。
                 */
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
                if (updateCount <= 0) {
                    //没有更新成功，秒杀结束
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }

        } catch (RepeatKillException e1) {
            throw e1;
        } catch (SeckillCloseException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }

    @Override
    public SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            return new SeckillExecution(seckillId, SeckillStatEnum.DATA_REWRITE);
        }
        //获取秒杀的执行时间
        Date killTime = new Date();
        //调用存储过程，执行秒杀操作
        //传入三个参数 seckillId, userPhone, killTime
        Map<String, Object> map = new HashMap<>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);
        //执行完存储过程后，result会自定被赋值
        try {
            seckillDao.killByProcedure(map);
            //通过工具类获取map集合的result值，没有则返回-2
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                //秒杀成功
                SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
            } else {
                return new SeckillExecution(seckillId, SeckillStatEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
        }
    }

}

