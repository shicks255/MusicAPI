package com.steven.hicks;

public class MissingConfigKeyException extends RuntimeException
{
    public MissingConfigKeyException(String message)
    {
        super(message);
    }
}
