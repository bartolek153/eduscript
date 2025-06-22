package org.eduscript.controllers;

import java.util.UUID;

import org.eduscript.repositories.JobMetadataRepository;
import org.eduscript.services.JobRequestProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/api/compile") // not using rest controllers for now
public class CompileController {

    private final JobRequestProducer jobRequestProducer;
    private final JobMetadataRepository jobMetadataRepository;

    public CompileController(
            JobRequestProducer jobRequestProducer, JobMetadataRepository jobMetadataRepository) {
        this.jobRequestProducer = jobRequestProducer;
        this.jobMetadataRepository = jobMetadataRepository;
    }

    @PostMapping("/async")
    public ResponseEntity<?> compile(
            @CookieValue("${app.constants.user-id-attribute}") UUID userId) throws JsonProcessingException {

        return ResponseEntity.ok().build();
    }
}
