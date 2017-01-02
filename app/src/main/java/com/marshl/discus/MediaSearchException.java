package com.marshl.discus;

public class MediaSearchException extends Exception {
    public MediaSearchException() {
    }

    public MediaSearchException(String message) {
        super(message);
    }

    public MediaSearchException(Exception exception, String message)
    {
        super(message, exception);
    }

}
