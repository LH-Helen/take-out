package com.sky.exception;

/**
 * 账号不存在异常
 */
public class AccountExistException extends BaseException {

    public AccountExistException() {
    }

    public AccountExistException(String msg) {
        super(msg);
    }

}
