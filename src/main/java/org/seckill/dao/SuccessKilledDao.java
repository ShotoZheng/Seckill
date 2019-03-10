package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entities.SuccessKilled;

public interface SuccessKilledDao {
    
    /**
     * @Description 插入购买成功明细，通过使用id+phone可以过滤重复
     * @Param 
     * @return 插入行数
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     * @Description 根据id和手机号查询SuccessKilled并携带秒杀产品对象实体
     * @Param 
     * @return 
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
}
