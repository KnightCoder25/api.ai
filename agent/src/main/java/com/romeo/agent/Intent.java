package com.romeo.agent;

/**
 * Created by romeo on 15/03/16.
 */
public abstract class Intent<T> {

    public abstract String getAction();

    public boolean shouldAuthorize(){
     return  false;
    }

    public abstract T call();


}
