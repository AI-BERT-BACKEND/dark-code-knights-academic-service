package com.aibert.dosw.application.dto.event;

import com.aibert.dosw.domain.model.NotificationEventType;
import com.aibert.dosw.domain.model.NotificationSeverity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("NotificationEvent DTO Tests")
class NotificationEventTest {

    @Test
    @DisplayName("Should build notification event with all fields")
    void shouldBuildNotificationEventWithAllFields() {
        NotificationEvent event = NotificationEvent.builder()
                .userId("student-123")
                .type(NotificationEventType.LOW_PERFORMANCE_ALERT)
                .title("Alerta")
                .message("Mensaje")
                .severity(NotificationSeverity.HIGH)
                .relatedEntityId(55L)
                .build();

        assertEquals("student-123", event.getUserId());
        assertEquals(NotificationEventType.LOW_PERFORMANCE_ALERT, event.getType());
        assertEquals("Alerta", event.getTitle());
        assertEquals("Mensaje", event.getMessage());
        assertEquals(NotificationSeverity.HIGH, event.getSeverity());
        assertEquals(55L, event.getRelatedEntityId());
    }

    @Test
    @DisplayName("Should create empty notification with no-args constructor")
    void shouldCreateEmptyNotificationWithNoArgsConstructor() {
        NotificationEvent event = new NotificationEvent();

        assertNull(event.getUserId());
        assertNull(event.getType());
        assertNull(event.getTitle());
        assertNull(event.getMessage());
        assertNull(event.getSeverity());
        assertNull(event.getRelatedEntityId());
    }

    @Test
    @DisplayName("Should create notification with all-args constructor")
    void shouldCreateNotificationWithAllArgsConstructor() {
        NotificationEvent event = new NotificationEvent(
                "student-456",
                NotificationEventType.LOW_PERFORMANCE_ALERT,
                "Titulo",
                "Detalle",
                NotificationSeverity.CRITICAL,
                99L
        );

        assertEquals("student-456", event.getUserId());
        assertEquals(NotificationEventType.LOW_PERFORMANCE_ALERT, event.getType());
        assertEquals("Titulo", event.getTitle());
        assertEquals("Detalle", event.getMessage());
        assertEquals(NotificationSeverity.CRITICAL, event.getSeverity());
        assertEquals(99L, event.getRelatedEntityId());
    }
}
