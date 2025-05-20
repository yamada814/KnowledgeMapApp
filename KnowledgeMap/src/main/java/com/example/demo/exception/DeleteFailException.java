package com.example.demo.exception;

public class DeleteFailException extends RuntimeException{
	public DeleteFailException(String msg) {
		super(msg);
	}
    public DeleteFailException(String message, Throwable cause) {
        super(message, cause);
    }

}
