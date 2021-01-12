package com.ben.lib_arouter.exception;

/**
 * @author: BD
 */
public class NoRouterFoundException extends RuntimeException{
    public NoRouterFoundException(String message) {
        super(message);
    }
}
