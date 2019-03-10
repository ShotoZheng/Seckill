package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entities.Seckill;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SeckillDao {

    /**
     * @Description 减库存，根据秒杀商品id和秒杀时间减少库存数量
     * @Param seckillId
     * @Param killTime
     * @Return 更新的行数
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime")Date killTime);

    /**
     * @return
     * @Description 根据Id查询秒杀对象
     * @Param
     */
    Seckill queryById(long seckillId);

    /**
     * @return
     * @Description 根据偏移量去查询秒杀商品列表
     * @Param
     */
    List<Seckill> queryAll(@Param("offset")int offset, @Param("limit") int limit);

    /**
     * @Description 使用存储过程执行秒杀
     * @Param 
     * @return 
     */
    void killByProcedure(Map<String, Object> paramMap);

}
