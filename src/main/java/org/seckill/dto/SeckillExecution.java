package org.seckill.dto;

import org.seckill.entities.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;

/**
 * @author 郑松涛
 * @description: 封装秒杀执行后的接口
 * @create 2019-03-08
 * @since 1.0.0
 */
public class SeckillExecution {

    //秒杀商品id
    private long seckillId;

    //秒杀执行结果状态
    private int state;

    //秒杀结果状态具体信息
    private String stateInfo;

    //秒杀成功明细对象
    private SuccessKilled successKilled;

    //秒杀成功时的构造方法
    public SeckillExecution(long seckillId, SeckillStatEnum statEnum, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getStateInfo();
        this.successKilled = successKilled;
    }

    //秒杀失败时的构造方法
    public SeckillExecution(long seckillId, SeckillStatEnum statEnum) {
        this.seckillId = seckillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getStateInfo();
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }

    @Override
    public String toString() {
        return "SeckillExecution{" +
                "seckillId=" + seckillId +
                ", state=" + state +
                ", stateInfo='" + stateInfo + '\'' +
                ", successKilled=" + successKilled +
                '}';
    }
}

