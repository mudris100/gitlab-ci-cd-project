package dev.udris.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic newPostEventsTopic() {
        return TopicBuilder.name("new-post-events")
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }

    @Bean
    public NewTopic newUserEventsTopic() {
        return TopicBuilder.name("new-user-events")
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }
}
