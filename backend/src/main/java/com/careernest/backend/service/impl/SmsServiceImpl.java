package com.careernest.backend.service.impl;

import com.careernest.backend.service.SmsService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.auth-token:}")
    private String authToken;

    @Value("${twilio.phone-number:}")
    private String fromPhoneNumber;

    private boolean configured;

    @PostConstruct
    public void init() {
        configured = StringUtils.hasText(accountSid)
                && StringUtils.hasText(authToken)
                && StringUtils.hasText(fromPhoneNumber);

        if (configured) {
            Twilio.init(accountSid, authToken);
            log.info("Twilio SMS enabled (from {})", fromPhoneNumber);
        } else {
            log.info("Twilio not configured — SMS notifications will be logged instead of sent. "
                    + "Set twilio.account-sid / auth-token / phone-number to enable.");
        }
    }

    /**
     * Notifications are best-effort: a failure to send must never roll back or fail the
     * business operation that triggered it (applying to a job, changing a status).
     */
    @Override
    public void sendSms(String toPhoneNumber, String message) {
        if (!StringUtils.hasText(toPhoneNumber)) {
            log.debug("Skipping SMS — recipient has no phone number on file");
            return;
        }
        if (!configured) {
            log.info("[SMS not sent — Twilio disabled] to={} message={}", toPhoneNumber, message);
            return;
        }

        try {
            Message.creator(new PhoneNumber(toPhoneNumber), new PhoneNumber(fromPhoneNumber), message).create();
            log.info("SMS sent to {}", toPhoneNumber);
        } catch (Exception ex) {
            log.warn("Failed to send SMS to {}: {}", toPhoneNumber, ex.getMessage());
        }
    }
}
