package dev.udris.service.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
@Service
public class PostEventProducer {
    private final KafkaTemplate<String, NewPostEvent> kafkaTemplate;

    public PostEventProducer(KafkaTemplate<String, NewPostEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNewPostEvent(NewPostEvent event) {
        kafkaTemplate.send("new-post-events", event);
    }
}
