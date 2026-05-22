package com.aibert.dosw.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Notification Enums Tests")
class NotificationEnumsTest {

    @Test
    @DisplayName("Should expose notification severity values in expected order")
    void shouldExposeNotificationSeverityValuesInExpectedOrder() {
        NotificationSeverity[] severities = NotificationSeverity.values();

        assertEquals(4, severities.length);
        assertArrayEquals(
                new NotificationSeverity[]{
                        NotificationSeverity.LOW,
                        NotificationSeverity.MEDIUM,
                        NotificationSeverity.HIGH,
                        NotificationSeverity.CRITICAL
                },
                severities
        );
        assertEquals(NotificationSeverity.HIGH, NotificationSeverity.valueOf("HIGH"));
    }

    @Test
    @DisplayName("Should expose notification event type values")
    void shouldExposeNotificationEventTypeValues() {
        NotificationEventType[] eventTypes = NotificationEventType.values();

        assertEquals(1, eventTypes.length);
        assertEquals(NotificationEventType.LOW_PERFORMANCE_ALERT, eventTypes[0]);
        assertEquals(
                NotificationEventType.LOW_PERFORMANCE_ALERT,
                NotificationEventType.valueOf("LOW_PERFORMANCE_ALERT")
        );
    }
}
