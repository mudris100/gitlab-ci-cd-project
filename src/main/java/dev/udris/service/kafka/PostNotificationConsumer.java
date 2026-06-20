package dev.udris.service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
@Service
public class PostNotificationConsumer {
    private final Logger LOGGER = LoggerFactory.getLogger(PostNotificationConsumer.class);

    @KafkaListener(topics = "new-post-events", groupId = "blog-service")
    public void handleNewPostEvent(NewPostEvent event) {
        LOGGER.info("Received New Post Event: {}", event.toString());

    }
}
