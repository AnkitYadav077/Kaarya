package com.Ankit.Kaarya.Exceptions;

public class ChatAccessDeniedException extends RuntimeException {
    public ChatAccessDeniedException(String message) {
        super(message);
    }
}