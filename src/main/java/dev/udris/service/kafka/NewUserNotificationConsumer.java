package dev.udris.service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import dev.udris.dto.UserDto;

@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
@Service
public class NewUserNotificationConsumer {
    private final Logger LOGGER = LoggerFactory.getLogger(NewUserNotificationConsumer.class);

    @KafkaListener(topics = "new-user-events", groupId = "user-service")
    public void handleNewPostEvent(UserDto userDto) {
        LOGGER.info("New User Registered Event: {}", userDto.toString());

    }
}
