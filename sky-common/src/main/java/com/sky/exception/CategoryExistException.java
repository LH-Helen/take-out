package com.sky.exception;

/**
 * 账号不存在异常
 */
public class CategoryExistException extends BaseException {

    public CategoryExistException() {
    }

    public CategoryExistException(String msg) {
        super(msg);
    }

}
