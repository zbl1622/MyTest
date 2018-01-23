package com.example.zbl.mytest.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5Util {
    public static String encrypt(String strIN) {
        MessageDigest alg;
        try {
            alg = MessageDigest.getInstance("MD5");
            alg.update(strIN.getBytes());
            byte[] bytes = alg.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String temp = Integer.toHexString(0xFF & bytes[i]);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                hexString.append(temp);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return strIN;
    }
}