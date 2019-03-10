package org.seckill.exception;

/**
 * @author 郑松涛
 * @description: 秒杀业务相关的异常，即与秒杀相关的异常的父异常
 * @create 2019-03-08
 * @since 1.0.0
 */
public class SeckillException extends RuntimeException {
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}

