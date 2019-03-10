package org.seckill.exception;

/**
 * @author 郑松涛
 * @description: 秒杀关闭异常
 * @create 2019-03-08
 * @since 1.0.0
 */
public class SeckillCloseException extends SeckillException {

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}

