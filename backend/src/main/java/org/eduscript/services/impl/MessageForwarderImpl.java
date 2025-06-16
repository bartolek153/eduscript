package org.eduscript.services.impl;

import java.util.Map;

import org.eduscript.grpc.MessageForwarderGrpc;
import org.eduscript.grpc.ProtoLogEntry;
import org.eduscript.utils.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.google.protobuf.Empty;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class MessageForwarderImpl extends MessageForwarderGrpc.MessageForwarderImplBase {
    
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Logger logger = LoggerFactory.getLogger(MessageForwarderImpl.class);

    public MessageForwarderImpl(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void forwardLog(ProtoLogEntry request, StreamObserver<Empty> responseObserver) {
        try {
            logger.info("Forward is being called [ui={}]", request.getUserId());

            Map<String, String> logPayload = Map.of(
                "timestamp", request.getTimestamp(),
                "level", request.getLevel(),
                "message", request.getMessage()
            );

            simpMessagingTemplate.convertAndSendToUser(
                request.getUserId(),
                AppConstants.Routes.WS_LOG_ROUTE,
                logPayload
            );

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                .withDescription("Failed to forward message")
                .withCause(e)
                .asRuntimeException());
        }
    }
}
