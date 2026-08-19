package com.careernest.backend.service;

public interface SmsService {
    void sendSms(String toPhoneNumber, String message);
}
