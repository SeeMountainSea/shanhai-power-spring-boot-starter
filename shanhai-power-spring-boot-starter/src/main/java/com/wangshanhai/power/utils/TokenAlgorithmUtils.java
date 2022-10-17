package com.wangshanhai.power.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 消息摘要算法
 * @author Shmily
 */
public class TokenAlgorithmUtils {
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
    public static String SHA512(String data) {
        try {
            //创建SHA512类型的加密对象
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(data.getBytes());
            byte[] bytes = messageDigest.digest();
            StringBuffer strHexString = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xff & bytes[i]);
                if (hex.length() == 1) {
                    strHexString.append('0');
                }
                strHexString.append(hex);
            }
            String result = strHexString.toString();
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
