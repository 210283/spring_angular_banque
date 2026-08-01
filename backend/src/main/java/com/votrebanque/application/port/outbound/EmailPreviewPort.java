package com.votrebanque.application.port.outbound;

import java.util.Optional;

public interface EmailPreviewPort {
    Optional<EmailPreview> findLatestEmailTo(String recipientEmail);

    record EmailPreview(String subject, String text, String html) {}
}
