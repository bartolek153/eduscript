package org.eduscript.configs.grpc;

public interface GrpcStubFactory<T> {
    T create(String host, int port);
}
