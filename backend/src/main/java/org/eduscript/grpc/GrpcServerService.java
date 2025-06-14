package org.eduscript.grpc;


import java.util.UUID;

import org.eduscript.configs.grpc.HeaderInterceptor;
import org.eduscript.grpc.CompileServiceGrpc.CompileServiceImplBase;
import org.eduscript.model.JobMessage;
import org.eduscript.model.JobSession;
import org.eduscript.repositories.JobSessionRepository;
import org.eduscript.services.JobRequestProducer;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.grpc.stub.StreamObserver;

// @GrpcService
@Service
public class GrpcServerService extends CompileServiceImplBase {

    private final JobRequestProducer jobRequestProducer;
    private final JobSessionRepository jobSessionRepository;

    public GrpcServerService(
            JobRequestProducer jobRequestProducer,
            JobSessionRepository jobSessionRepository) {
        this.jobRequestProducer = jobRequestProducer;
        this.jobSessionRepository = jobSessionRepository;
    }

    @Override
    public void compileCode(CompileRequest request, StreamObserver<CompileResponse> responseObserver) {
        UUID userId = getUserId();
        JobMessage job = new JobMessage(UUID.randomUUID(), request.getSourceCode());

        try {
            jobRequestProducer.send(job);
        } catch (JsonProcessingException ex) {
            // TODO: handle JsonProcessingException
        }
        
        jobSessionRepository.save(
                new JobSession(job.getId(), userId));

        CompileResponse resp = CompileResponse.newBuilder().setJobId(job.getId().toString()).build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

    private UUID getUserId() {
        return (UUID) HeaderInterceptor.USER_IDENTITY.get();
    }
}
