package com.spider.engine;

/**
 * 执行远程访问
 */
public interface Request<T, D> {

    /**
     * 执行请求
     */
    T execute(D model) throws Exception;
}
