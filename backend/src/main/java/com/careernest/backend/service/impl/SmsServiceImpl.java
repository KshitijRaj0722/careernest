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

    private static final String WHATSAPP_CHANNEL = "whatsapp";
    private static final String WHATSAPP_PREFIX = "whatsapp:";

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.auth-token:}")
    private String authToken;

    @Value("${twilio.phone-number:}")
    private String fromPhoneNumber;

    /**
     * "sms" or "whatsapp". WhatsApp is useful on a Twilio trial for destinations where
     * carrier SMS requires registered templates (e.g. India's DLT rules), since sandbox
     * WhatsApp accepts free-form text.
     */
    @Value("${twilio.channel:sms}")
    private String channel;

    /** Twilio's shared WhatsApp sandbox sender; recipients must join the sandbox first. */
    @Value("${twilio.whatsapp-from:+14155238886}")
    private String whatsappFrom;

    private boolean configured;

    private boolean isWhatsApp() {
        return WHATSAPP_CHANNEL.equalsIgnoreCase(channel);
    }

    private String sender() {
        return isWhatsApp() ? whatsappFrom : fromPhoneNumber;
    }

    @PostConstruct
    public void init() {
        configured = StringUtils.hasText(accountSid)
                && StringUtils.hasText(authToken)
                && StringUtils.hasText(sender());

        if (configured) {
            Twilio.init(accountSid, authToken);
            log.info("Twilio enabled — channel={} from={}", isWhatsApp() ? "whatsapp" : "sms", sender());
        } else {
            log.info("Twilio not configured — notifications will be logged instead of sent. "
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
            log.debug("Skipping notification — recipient has no phone number on file");
            return;
        }
        if (!configured) {
            log.info("[not sent — Twilio disabled] to={} message={}", toPhoneNumber, message);
            return;
        }

        String to = isWhatsApp() ? WHATSAPP_PREFIX + toPhoneNumber : toPhoneNumber;
        String from = isWhatsApp() ? WHATSAPP_PREFIX + sender() : sender();

        try {
            Message.creator(new PhoneNumber(to), new PhoneNumber(from), message).create();
            log.info("Notification sent to {} via {}", toPhoneNumber, isWhatsApp() ? "whatsapp" : "sms");
        } catch (Exception ex) {
            log.warn("Failed to send notification to {}: {}", toPhoneNumber, ex.getMessage());
        }
    }
}
