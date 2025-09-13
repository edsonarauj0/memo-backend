package com.nosde.memo.infrastructure.helper;

import org.apache.commons.validator.routines.EmailValidator;

public class UtHelper {

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }
    
}
