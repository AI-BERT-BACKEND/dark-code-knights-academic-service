package com.aibert.dosw.domain.ports.out;

import com.aibert.dosw.application.dto.event.NotificationEvent;

public interface NotificationProducerPort {
    void publish(NotificationEvent event);
}
