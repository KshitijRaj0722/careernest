package com.careernest.backend.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {
    // Twilio.init(...) is handled in SmsServiceImpl via @PostConstruct
}
