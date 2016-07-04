package com.pulin.dubboserver.exception;

public class BizException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 5371047353195812320L;
    private int code;
    private String message;

    public BizException(Exception e) {
        this.message = e.getMessage();
    }
    public BizException(int code,String message) {
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

