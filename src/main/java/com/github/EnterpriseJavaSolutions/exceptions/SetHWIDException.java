package com.github.EnterpriseJavaSolutions.exceptions;

public class SetHWIDException extends RuntimeException {
    public SetHWIDException(String message) {
        super(message);
    }

    public SetHWIDException(String message, Throwable cause) {
        super(message, cause);
    }
}
