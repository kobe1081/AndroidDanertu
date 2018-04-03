package com.danertu.tools;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

/**
 * @author vipin.cb , vipin.cb@experionglobal.com <br>
 *         Sep 27, 2013, 5:18:34 PM <br>
 *         Package:- <b>com.veebow.util</b> <br>
 *         Project:- <b>Veebow</b>
 *         <p>
 */
public class AESEncrypt {
    private final Cipher cipher;
    private final SecretKeySpec key;
    private AlgorithmParameterSpec spec;
//    public static final String SEED_16_CHARACTER = "U1MjU1M0FDOUZ.Qz";


    private final String keyStr = "abcdef1234567890";

    public AESEncrypt() throws Exception {
        // hash password with SHA-256 and crop the output to 128-bit for key
        // 先用sha-256算法加密key为128位的key
//        MessageDigest digest = MessageDigest.getInstance("SHA-256");
//        digest.update(keyStr.getBytes("UTF-8"));
//        byte[] keyBytes = new byte[32];
//        byte[] digs = digest.digest();
//        System.arraycopy(digs, 0, keyBytes, 0, keyBytes.length);
//        Log.e("密钥", new String(digs, "UTF-8"));

        cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        key = new SecretKeySpec(keyStr.getBytes(), "AES");
        spec = getIV();
    }

    byte[] iv = null;

    //专用算法参数
    public AlgorithmParameterSpec getIV() {
        if (iv == null)
            iv = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//    	byte[] iv = { 0x12, 0x34, 0x56, 0x78, 0x90, 0xAB, 0xCD, 0xEF, 0x12, 0x34, 0x56, 0x78, 0x90, 0xAB, 0xCD, 0xEF };

        IvParameterSpec ivParameterSpec;
        ivParameterSpec = new IvParameterSpec(iv);
        return ivParameterSpec;
    }

    public void setAlgorithKey() {
        iv = new byte[]{0, 2, 5, 1, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 1};
        spec = getIV();
    }

    public String encrypt(String plainText) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
        return new String(Base64.encode(encrypted, Base64.DEFAULT), "UTF-8");
    }

    public String decrypt(String cryptedText) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] bytes = Base64.decode(cryptedText, Base64.DEFAULT);
        byte[] decrypted = cipher.doFinal(bytes);
        return new String(decrypted, "UTF-8");
    }
}