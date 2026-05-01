package com.agriconnect.commons.util;

public final class PhoneUtils {

    private PhoneUtils() {}

    /**
     * Normalise un numéro de téléphone camerounais en format international +237XXXXXXXXX
     */
    public static String normalize(String phone) {
        if (phone == null) return null;
        String cleaned = phone.replaceAll("[\\s\\-().]+", "");
        if (cleaned.startsWith("+237")) return cleaned;
        if (cleaned.startsWith("237"))  return "+" + cleaned;
        if (cleaned.startsWith("6") || cleaned.startsWith("2")) return "+237" + cleaned;
        return cleaned;
    }

    public static String mask(String phone) {
        if (phone == null || phone.length() < 6) return phone;
        return phone.substring(0, 5) + "***" + phone.substring(phone.length() - 3);
    }

    public static boolean isValidCameroonPhone(String phone) {
        if (phone == null) return false;
        String n = normalize(phone);
        return n.matches("\\+237[6-9]\\d{8}");
    }
}
