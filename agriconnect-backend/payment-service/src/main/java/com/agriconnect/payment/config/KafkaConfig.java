package com.agriconnect.payment.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean public NewTopic paymentSuccess()   { return TopicBuilder.name("payment.success").partitions(3).replicas(1).build(); }
    @Bean public NewTopic paymentFailed()    { return TopicBuilder.name("payment.failed").partitions(3).replicas(1).build(); }
    @Bean public NewTopic escrowLocked()     { return TopicBuilder.name("payment.escrow.locked").partitions(3).replicas(1).build(); }
    @Bean public NewTopic escrowReleased()   { return TopicBuilder.name("payment.escrow.released").partitions(3).replicas(1).build(); }
}
