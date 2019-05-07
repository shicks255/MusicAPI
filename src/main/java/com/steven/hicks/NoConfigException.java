package com.steven.hicks;

public class NoConfigException extends RuntimeException
{
    public NoConfigException(String error)
    {
        super(error);
    }
}
