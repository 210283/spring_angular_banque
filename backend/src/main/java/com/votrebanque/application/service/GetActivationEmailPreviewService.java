package com.votrebanque.application.service;

import com.votrebanque.application.port.inbound.GetActivationEmailPreviewUseCase;
import com.votrebanque.application.port.outbound.EmailPreviewPort;
import com.votrebanque.domain.exception.EmailNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GetActivationEmailPreviewService implements GetActivationEmailPreviewUseCase {

    private static final String EMAIL_DOMAIN = "@votrebanque.com";

    private final EmailPreviewPort emailPreviewPort;

    public GetActivationEmailPreviewService(EmailPreviewPort emailPreviewPort) {
        this.emailPreviewPort = emailPreviewPort;
    }

    @Override
    public EmailPreviewResult getActivationEmail(String username) {
        String recipientEmail = username + EMAIL_DOMAIN;

        return emailPreviewPort.findLatestEmailTo(recipientEmail)
            .map(email -> new EmailPreviewResult(email.subject(), email.text(), email.html()))
            .orElseThrow(() -> new EmailNotFoundException("No activation email found yet for this account"));
    }
}
