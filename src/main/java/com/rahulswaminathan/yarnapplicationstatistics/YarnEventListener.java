package com.rahulswaminathan.yarnapplicationstatistics;

/**
 * Created by rahulswaminathan on 11/19/14.
 */
public interface YarnEventListener {

    public abstract void listen();

    public abstract void onEventBegin();

    public abstract void onEventEnd();



}
