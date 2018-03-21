package com.yuanben.common;

public class InvalidException extends Exception {
    public InvalidException() {
        super();
    }

    public InvalidException(String msg) {
        super(msg);
    }

    public InvalidException(String msg, Throwable e) {
        super(msg, e);
    }
}
