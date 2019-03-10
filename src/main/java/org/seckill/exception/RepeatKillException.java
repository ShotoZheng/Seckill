package org.seckill.exception;

/**
 * @author 郑松涛
 * @description: 重复秒杀异常，运行期异常
 * @create 2019-03-08
 * @since 1.0.0
 */
public class RepeatKillException extends SeckillException {

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}

