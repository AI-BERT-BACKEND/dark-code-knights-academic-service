package com.aibert.dosw.application.dto.event;

import com.aibert.dosw.domain.model.NotificationEventType;
import com.aibert.dosw.domain.model.NotificationSeverity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private String userId;
    private NotificationEventType type;
    private String title;
    private String message;
    private NotificationSeverity severity;
    private Long relatedEntityId;
}
