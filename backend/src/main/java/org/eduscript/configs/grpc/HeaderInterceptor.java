package org.eduscript.configs.grpc;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

@Component
@Order(100)
public class HeaderInterceptor implements ServerInterceptor {

    private final static Logger logger = LoggerFactory.getLogger(HeaderInterceptor.class);

    public static final Context.Key<Object> USER_IDENTITY = Context.key("${app.constants.user-id-attribute}");

    @Value("${app.constants.user-id-attribute}")
    private String userIdHdAttr;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        Object userId = headers.get(Metadata.Key.of(userIdHdAttr, Metadata.ASCII_STRING_MARSHALLER));

        if (userId == null) {
            // TODO: handle non-authenticated users
        }

        Context context = Context.current()
                .withValue(USER_IDENTITY, userId);

        return Contexts.interceptCall(context, call, headers, next);
    }
}
