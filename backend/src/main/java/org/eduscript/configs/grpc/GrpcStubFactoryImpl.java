package org.eduscript.configs.grpc;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import io.grpc.CallOptions;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractBlockingStub;
import io.grpc.stub.AbstractStub.StubFactory;
import jakarta.annotation.PreDestroy;

// credits: https://github.com/grpc/grpc-java/blob/master/examples/src/main/java/io/grpc/examples/header/README.md

@Component
public class GrpcStubFactoryImpl<T extends AbstractBlockingStub<T>> implements GrpcStubFactory<T> {

    private final StubFactory<T> stubFactory;
    private final ConcurrentHashMap<String, ManagedChannel> channelCache = new ConcurrentHashMap<>();

    public GrpcStubFactoryImpl(StubFactory<T> stubFactory) {
        this.stubFactory = stubFactory;
    }

    @Override
    public T create(String host, int port) {
        String key = host + ":" + port;  // TODO: clean up dead servers

        ManagedChannel channel = channelCache.computeIfAbsent(key,
                k -> ManagedChannelBuilder.forAddress(host, port)
                        .usePlaintext() // .useTransportSecurity() for TLS
                        .build()
        );

        return stubFactory.newStub(channel, CallOptions.DEFAULT);
    }

    @PreDestroy
    public void shutdownAll() {
        for (ManagedChannel channel : channelCache.values()) {
            channel.shutdown();
        }
    }
}
