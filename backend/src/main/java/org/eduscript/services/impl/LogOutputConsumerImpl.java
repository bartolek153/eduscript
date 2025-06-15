package org.eduscript.services.impl;

import java.util.UUID;

import org.eduscript.configs.grpc.GrpcStubFactory;
import org.eduscript.exceptions.UserNotFoundException;
import org.eduscript.grpc.MessageForwarderGrpc.MessageForwarderBlockingStub;
import org.eduscript.grpc.ProtoLogEntry;
import org.eduscript.model.Instance;
import org.eduscript.model.JobSession;
import org.eduscript.model.LogEntry;
import org.eduscript.model.UserSession;
import org.eduscript.repositories.InstanceRepository;
import org.eduscript.repositories.JobSessionRepository;
import org.eduscript.services.LogOutputConsumer;
import org.eduscript.services.UserSessionService;
import org.eduscript.utils.AppConstants;
import org.eduscript.utils.InstanceRegistration;
import org.eduscript.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import io.grpc.StatusRuntimeException;

@Component
public class LogOutputConsumerImpl implements LogOutputConsumer {

    private static final Logger logger = LoggerFactory.getLogger(LogOutputConsumerImpl.class);

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final JobSessionRepository jobSessionRepository;
    private final InstanceRepository instanceRepository;
    private final UserSessionService userSessionService;
    private final GrpcStubFactory<MessageForwarderBlockingStub> grpcStubFactory;

    public LogOutputConsumerImpl(
            SimpMessagingTemplate simpMessagingTemplate,
            UserSessionService userSessionService,
            JobSessionRepository jobSessionRepository,
            GrpcStubFactory<MessageForwarderBlockingStub> grpcStubFactory,
            InstanceRepository instanceRepository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.jobSessionRepository = jobSessionRepository;
        this.instanceRepository = instanceRepository;
        this.userSessionService = userSessionService;
        this.grpcStubFactory = grpcStubFactory;
    }

    @Override
    @KafkaListener(topics = "${app.kafka.topics.logs}")
    public void consume(LogEntry log) {
        UUID jobId = Utils.uuidToStr(log.getJobId());

        jobSessionRepository.findById(jobId).ifPresentOrElse(
                jobSession -> handleLog(log, jobSession),
                () -> logger.error(
                        "Received log with inexistent job id {}. Could not proceed to get corresponding client",
                        jobId));
    }

    private void handleLog(LogEntry log, JobSession jobSession) {
        UserSession userSession = userSessionService.getUser(jobSession.getUserId())
                .orElseThrow(() -> new UserNotFoundException(jobSession.getUserId()));

        if (isCurrentInstance(userSession)) {
            sendLogToLocalUser(jobSession, log);
        } else {
            forwardLogToInstance(userSession, log, jobSession);
        }
    }

    private boolean isCurrentInstance(UserSession userSession) {
        return userSession.getInstanceId().equals(InstanceRegistration.getId());
    }

    private void sendLogToLocalUser(JobSession jobSession, LogEntry log) {
        simpMessagingTemplate.convertAndSendToUser(
                jobSession.getUserId().toString(),
                AppConstants.Routes.WS_LOG_ROUTE,
                log);
    }

    private void forwardLogToInstance(UserSession userSession, LogEntry log, JobSession jobSession) {
        Instance targetInstance = instanceRepository.findById(userSession.getInstanceId())
                .orElseThrow(() -> new IllegalStateException("Instance not found: " + userSession.getInstanceId()));

        ProtoLogEntry req = ProtoLogEntry.newBuilder()
                .setTimestamp(log.timeKey())
                .setLevel(log.getLevel())
                .setMessage(log.getMessage())
                .setUserId(userSession.getUserId().toString())
                .build();

        MessageForwarderBlockingStub stub = grpcStubFactory.create(
                targetInstance.getHost(),
                targetInstance.getPort());

        try {
            stub.forwardLog(req);

        } catch (StatusRuntimeException e) {
            logger.error("An error occurred while forwarding log: {}", e.getStatus().getDescription(), e);
            // TODO: send to monitoring
        }

    }
}
