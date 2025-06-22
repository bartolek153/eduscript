package org.eduscript.exceptions;

public class NullStageException extends IllegalArgumentException {
    public NullStageException() {
        super("Stage cannot be null");
    }
}
