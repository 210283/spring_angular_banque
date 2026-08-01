package com.votrebanque.application.port.inbound;

public interface GetActivationEmailPreviewUseCase {
    EmailPreviewResult getActivationEmailPreview(String username);

    record EmailPreviewResult(String subject, String text, String html) {}
}
