package org.eduscript.controllers;

import org.eduscript.dto.JobDto;
import org.eduscript.services.JobRequestProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/api/compile")
public class CompilerController {
    
    private final JobRequestProducer jobRequestProducer;

    public CompilerController(
            JobRequestProducer jobRequestProducer) {
        this.jobRequestProducer = jobRequestProducer;
    }

    @PostMapping
    public ResponseEntity<?> compile(String sourceCode) throws JsonProcessingException {
        JobDto job = new JobDto("1", sourceCode);
        jobRequestProducer.sendMessage(job);
        return ResponseEntity.ok().build();
    }
}
