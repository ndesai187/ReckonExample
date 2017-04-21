package com.mkyong.rest;

/**
 * Created by bennettzhou on 17/04/2017.
 */
public interface CallbackHandler {

    public abstract void ready() throws Exception;

    public abstract String getCallbackURL();

    public abstract String getVerifier();

    public abstract void done();

}
