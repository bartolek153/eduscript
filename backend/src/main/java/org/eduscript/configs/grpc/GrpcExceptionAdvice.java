package org.eduscript.configs.grpc;

import org.eduscript.exceptions.UnauthenticatedException;

import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GrpcExceptionAdvice {

    @GrpcExceptionHandler
    public Status handleUnauthenticatedUser(UnauthenticatedException e) {
        return Status.UNAUTHENTICATED.withDescription(e.getMessage());
    }
}
