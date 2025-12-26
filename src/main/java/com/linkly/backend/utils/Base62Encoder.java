package com.linkly.backend.utils;

public class Base62Encoder {

    private static final String BASE62_CHARS =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String encode(long number) {
        if (number == 0) {
            return "0";
        }

        StringBuilder result = new StringBuilder();

        while (number > 0) {
            int remainder = (int) (number % 62);
            result.insert(0, BASE62_CHARS.charAt(remainder));
            number = number / 62;
        }

        return result.toString();
    }

    // Optional: Decode function
    public static long decode(String encoded) {
        long result = 0;

        for (int i = 0; i < encoded.length(); i++) {
            char c = encoded.charAt(i);
            int index = BASE62_CHARS.indexOf(c);
            result = result * 62 + index;
        }

        return result;
    }
}
