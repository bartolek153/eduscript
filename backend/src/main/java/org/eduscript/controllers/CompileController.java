package org.eduscript.controllers;

import java.util.UUID;

import org.eduscript.model.CompileRequest;
import org.eduscript.model.CompileResponse;
import org.eduscript.model.JobMessage;
import org.eduscript.services.JobRequestProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/api/compile")
public class CompileController {
    
    private final JobRequestProducer jobRequestProducer;

    public CompileController(
            JobRequestProducer jobRequestProducer) {
        this.jobRequestProducer = jobRequestProducer;
    }

    @PostMapping("/async")
    public ResponseEntity<CompileResponse> compile(CompileRequest req) throws JsonProcessingException {
        JobMessage job = new JobMessage(UUID.randomUUID(), req.getSourceCode());
        jobRequestProducer.send(job);

        CompileResponse res = new CompileResponse(job);
        return ResponseEntity.ok(res);
    }
}
