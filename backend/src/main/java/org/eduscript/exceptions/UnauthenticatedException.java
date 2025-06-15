package org.eduscript.exceptions;

public class UnauthenticatedException extends RuntimeException {
    public UnauthenticatedException() {
        super("Client must be authenticated to continue");
    }
}
