-- 秒杀执行存储过程
-- row_count()函数会返回上一条修改类型的SQL执行所影响的行数
-- DELIMITER // 自定义存储过程的换行符为//。因为在控制台编写存储过程时默认只支持;换行。
-- 这会导致存储过程体内不能有;。
DELIMITER //
CREATE PROCEDURE execute_seckill(
  in v_seckill_id bigint, -- 秒杀ID
  in v_phone bigint,  -- 手机号码
  in v_kill_time timestamp, -- 秒杀时间
  out r_result int  -- 存储过程执行的返回结果
)
  BEGIN
    DECLARE insert_count int DEFAULT 0; -- DECLARE定义一个默认值为0的变量
    START TRANSACTION ; -- 开启事务
    insert ignore into success_killed(seckill_id, user_phone, create_time, state)
    values (v_seckill_id, v_phone, v_kill_time, 0);
    select row_count() into insert_count; -- 调用函数并将结果存储在insert_count中
    IF (insert_count = 0) THEN  -- 为0说明插入不成功
      ROLLBACK ;
      set r_result = -1;  -- -1 代表 重复秒杀
    ELSEIF (insert_count < 0) THEN
      set r_result = -2;  -- -2 说明插入语句执行发生异常，代表 系统异常
    ELSE  -- 插入成功后执行减库存操作
      update seckill
      set number = number - 1
      where seckill_id = v_seckill_id
        and end_time > v_kill_time
        and start_time < v_kill_time
        and number > 0;
      select row_count() into insert_count;
      IF (insert_count = 0) THEN  -- 秒杀结束
        ROLLBACK ;
        set r_result = 0;
      ELSEIF (insert_count < 0) THEN  -- 更新出错
        ROLLBACK ;
        set r_result = -2;
      ELSE
        COMMIT ;
        set r_result = 1; -- 1 代表秒杀成功
      END IF;
    end IF;
  end ;
//

DELIMITER ;
set @r_result = -3;
--执行存储过程
call execute_seckill(1003, 13585115682, now(), @r_result);
--获取结果
select @r_result;
