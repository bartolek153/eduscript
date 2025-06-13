package org.eduscript.controllers;

import java.util.UUID;

import org.eduscript.model.CompileRequest;
import org.eduscript.model.CompileResponse;
import org.eduscript.model.JobMessage;
import org.eduscript.model.JobSession;
import org.eduscript.repositories.JobSessionRepository;
import org.eduscript.services.JobRequestProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/api/compile")
public class CompileController {

    private final JobRequestProducer jobRequestProducer;
    private final JobSessionRepository jobSessionRepository;

    public CompileController(
            JobRequestProducer jobRequestProducer, JobSessionRepository jobSessionRepository) {
        this.jobRequestProducer = jobRequestProducer;
        this.jobSessionRepository = jobSessionRepository;
    }

    @PostMapping("/async")
    public ResponseEntity<CompileResponse> compile(
            @CookieValue("${app.constants.user-id-attribute}") UUID userId,
            CompileRequest req) throws JsonProcessingException {

        JobMessage job = new JobMessage(UUID.randomUUID(), req.getSourceCode());
        
        jobRequestProducer.send(job);
        jobSessionRepository.save(
                new JobSession(job.getId(), userId));

        return ResponseEntity.ok(new CompileResponse(job));
    }
}
