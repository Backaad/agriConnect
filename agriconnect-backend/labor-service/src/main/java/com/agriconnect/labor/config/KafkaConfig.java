package com.agriconnect.labor.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean public NewTopic applicationAccepted() { return TopicBuilder.name("labor.application.accepted").partitions(3).replicas(1).build(); }
    @Bean public NewTopic contractSigned()       { return TopicBuilder.name("labor.contract.signed").partitions(3).replicas(1).build(); }
    @Bean public NewTopic missionCompleted()     { return TopicBuilder.name("labor.mission.completed").partitions(3).replicas(1).build(); }
    @Bean public NewTopic missionDisputed()      { return TopicBuilder.name("labor.mission.disputed").partitions(3).replicas(1).build(); }
}
