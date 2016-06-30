package com.pulin.dubboserver.exception;

public class DaoException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 5371047353195812320L;
    private String message;

    public DaoException(Exception e) {
        this.message = e.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }
}

