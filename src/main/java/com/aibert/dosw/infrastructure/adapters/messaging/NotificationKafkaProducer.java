package com.aibert.dosw.infrastructure.adapters.messaging;

import com.aibert.dosw.application.dto.event.NotificationEvent;
import com.aibert.dosw.domain.ports.out.NotificationProducerPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaProducer implements NotificationProducerPort {

    private final KafkaTemplate<String, NotificationEvent> notificationKafkaTemplate;

    @Value("${app.kafka.topics.notifications}")
    private String topic;

    @Override
    public void publish(NotificationEvent event) {
        log.info("Publishing notification - userId={}, type={}", event.getUserId(), event.getType());
        notificationKafkaTemplate.send(topic, event.getUserId(), event);
    }
}
