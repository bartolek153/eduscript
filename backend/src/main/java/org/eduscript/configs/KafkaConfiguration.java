package org.eduscript.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {

    @Value("${app.kafka.topics.jobs}")
    private String jobRequestsTopic;

    @Bean
    public NewTopic jobRequestTopicBuilder() {
        return TopicBuilder
                .name(jobRequestsTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}