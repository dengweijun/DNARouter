package com.ben.router_compiler.utils;


import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * 日志打印类
 *
 * @author: BD
 */
public class Log {

    private Messager messager;

    public Log(Messager messager) {
        this.messager = messager;
    }

    /**
     * 调用Messager的printMessage()来打印日志
     *
     * @param msg
     */
    public void i(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }
}
