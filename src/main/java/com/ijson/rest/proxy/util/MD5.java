package com.ijson.rest.proxy.util;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;

@Slf4j
public class MD5 {

    private static final String[] HEX_DIGITS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte aB : b) {
            resultSb.append(byteToHexString(aB));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }

    public static String mD5Encode(String origin) {
        String result = null;
        try {
            result = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            result = byteArrayToHexString(md.digest(result.getBytes("UTF-8")));
        } catch (Exception ignored) {
            log.error("MD5 ERROR :", ignored);
        }
        return result;
    }

}
