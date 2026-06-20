package dev.udris.service.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import dev.udris.dto.UserDto;

@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
@Service
public class UserEventPublisher {
    private final KafkaTemplate<String, UserDto> kafkaTemplate;

    public UserEventPublisher(KafkaTemplate<String, UserDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(UserDto event) {
        kafkaTemplate.send("new-user-events", event);
    }
}
