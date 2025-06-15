package org.eduscript.configs.grpc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;

@GrpcGlobalServerInterceptor
@Order(100)
public class UserAuthHeaderInterceptor implements ServerInterceptor {

    // private final static Logger logger = LoggerFactory.getLogger(HeaderInterceptor.class);

    public static final Context.Key<Object> USER_IDENTITY = Context.key("${app.constants.user-id-attribute}");

    @Value("${app.constants.user-id-attribute}")
    private String userIdHdAttr;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        Object userId = headers.get(Metadata.Key.of(userIdHdAttr, Metadata.ASCII_STRING_MARSHALLER));

        // if (userId == null) {
        //     throw new UnauthenticatedException();
        // }

        Context context = Context.current()
                .withValue(USER_IDENTITY, userId);

        return Contexts.interceptCall(context, call, headers, next);
    }
}
