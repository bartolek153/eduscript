package org.eduscript.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eduscript.configs.grpc.UserAuthHeaderInterceptor;
import org.eduscript.datastructures.Pipeline;
import org.eduscript.datastructures.Stage;
import org.eduscript.enums.JobStatus;
import org.eduscript.exceptions.JobNotFoundException;
import org.eduscript.grpc.CancelRequest;
import org.eduscript.grpc.CancelResponse;
import org.eduscript.grpc.CompileRequest;
import org.eduscript.grpc.CompileResponse;
import org.eduscript.grpc.CompileServiceGrpc;
import org.eduscript.model.JobMessage;
import org.eduscript.model.JobMetadata;
import org.eduscript.model.JobTask;
import org.eduscript.parser.SyntaxErrorListener;
import org.eduscript.repositories.JobMetadataRepository;
import org.eduscript.semantic.SemanticAnalyzer;
import org.eduscript.services.JobRequestProducer;
import org.eduscript.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.grpc.stub.StreamObserver;
import main.antlr4.EduScriptLexer;
import main.antlr4.EduScriptParser;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class CompileServiceImpl extends CompileServiceGrpc.CompileServiceImplBase {

    private final JobRequestProducer jobRequestProducer;
    private final JobMetadataRepository jobMetadataRepository;
    private final SemanticAnalyzer semanticAnalyzer;

    private final static Logger logger = LoggerFactory.getLogger(CompileServiceImpl.class);

    public CompileServiceImpl(
            JobRequestProducer jobRequestProducer,
            JobMetadataRepository jobMetadataRepository,
            SemanticAnalyzer semanticAnalyzer) {
        this.jobRequestProducer = jobRequestProducer;
        this.jobMetadataRepository = jobMetadataRepository;
        this.semanticAnalyzer = semanticAnalyzer;
    }

    @Override
    public void compileCode(CompileRequest request, StreamObserver<CompileResponse> responseObserver) {
        UUID userId = getUserId();

        Pipeline ppl = extractPipeline(request.getSourceCode());
        List<JobTask> tasks = new ArrayList<>();

        for (Stage st : ppl.getPlan()) {
            Map<String, String> args = new HashMap<>();

            args.putAll(ppl.getEnvs());
            args.putAll(st.getConfig().getCustomArgs());

            tasks.add(new JobTask(st.getName(), st.getImage(), st.getRunCommands(), args));
        }

        JobMessage job = new JobMessage(UUID.randomUUID(), tasks);

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

    @Override
    public void cancelJob(CancelRequest request, StreamObserver<CancelResponse> responseObserver) {
        UUID jobId = Utils.strToUUID(request.getJobId());

        JobMetadata jm = jobMetadataRepository.findById(jobId).orElseThrow(
                () -> new JobNotFoundException(jobId));

        jm.setStatus(JobStatus.CANCELED);

        jobMetadataRepository.save(jm);
    }

    private Pipeline extractPipeline(String source) {
        CharStream input = CharStreams.fromString(source);
        EduScriptLexer lexer = new EduScriptLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        EduScriptParser parser = new EduScriptParser(tokens);

        SyntaxErrorListener errorListener = new SyntaxErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        ParseTree tree = parser.pipeline();

        if (errorListener.hasError()) {
            logger.error(errorListener.getErrorMessages());
            throw new RuntimeException();
        }

        semanticAnalyzer.visit(tree);

        if (semanticAnalyzer.getErrorHandler().hasErrors()) {
            semanticAnalyzer.getErrorHandler().printSummary();
        }

        return semanticAnalyzer.exportPipelineObj();
    }

    private UUID getUserId() {
        String id = (String) UserAuthHeaderInterceptor.USER_IDENTITY.get();
        return UUID.fromString(id);
    }
}
