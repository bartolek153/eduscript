package org.eduscript.services.impl;


import java.util.UUID;

import org.eduscript.configs.grpc.UserAuthHeaderInterceptor;
import org.eduscript.enums.JobStatus;
import org.eduscript.grpc.CompileRequest;
import org.eduscript.grpc.CompileResponse;
import org.eduscript.grpc.CompileServiceGrpc;
import org.eduscript.model.JobMessage;
import org.eduscript.model.JobMetadata;
import org.eduscript.repositories.JobMetadataRepository;
import org.eduscript.services.JobRequestProducer;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class CompileServiceImpl extends CompileServiceGrpc.CompileServiceImplBase {

    private final JobRequestProducer jobRequestProducer;
    private final JobMetadataRepository jobMetadataRepository;

    public CompileServiceImpl(
            JobRequestProducer jobRequestProducer,
            JobMetadataRepository jobMetadataRepository) {
        this.jobRequestProducer = jobRequestProducer;
        this.jobMetadataRepository = jobMetadataRepository;
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
        
        jobMetadataRepository.save(
                new JobMetadata(job.getId(), userId, JobStatus.PENDING));

        CompileResponse resp = CompileResponse.newBuilder().setJobId(job.getId().toString()).build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

    private UUID getUserId() {
        String id = (String) UserAuthHeaderInterceptor.USER_IDENTITY.get();
        return UUID.fromString(id);
    }
}
