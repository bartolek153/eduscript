package org.eduscript.configs.grpc;

import org.eduscript.grpc.MessageForwarderGrpc;
import org.eduscript.grpc.MessageForwarderGrpc.MessageForwarderBlockingStub;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.stub.AbstractStub.StubFactory;

@Configuration
public class GrpcConfig {

    @Bean
    StubFactory<MessageForwarderBlockingStub> sf() {
        StubFactory<MessageForwarderBlockingStub> stubFactory = (channel, callOptions) -> MessageForwarderGrpc
                .newBlockingStub(channel);

        return stubFactory;
    }
}
