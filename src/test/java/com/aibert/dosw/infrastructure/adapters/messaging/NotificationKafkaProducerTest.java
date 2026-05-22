package com.aibert.dosw.infrastructure.adapters.messaging;

import com.aibert.dosw.application.dto.event.NotificationEvent;
import com.aibert.dosw.domain.model.NotificationEventType;
import com.aibert.dosw.domain.model.NotificationSeverity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationKafkaProducer Tests")
class NotificationKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, NotificationEvent> notificationKafkaTemplate;

    @InjectMocks
    private NotificationKafkaProducer producer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(producer, "topic", "notifications-topic");
    }

    @Test
    @DisplayName("Should publish notification using user id as message key")
    void shouldPublishNotificationUsingUserIdAsMessageKey() {
        NotificationEvent event = NotificationEvent.builder()
                .userId("student-1")
                .type(NotificationEventType.LOW_PERFORMANCE_ALERT)
                .title("Bajo rendimiento")
                .message("Tu nota bajó en el último corte")
                .severity(NotificationSeverity.MEDIUM)
                .relatedEntityId(100L)
                .build();

        producer.publish(event);

        verify(notificationKafkaTemplate).send("notifications-topic", "student-1", event);
    }
}
