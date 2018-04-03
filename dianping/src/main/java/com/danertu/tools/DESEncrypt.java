package com.danertu.tools;

import java.nio.charset.Charset;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

/**
 * 使用DES加密与解密,可对byte[],String类型进行加密与解密
 * 密文可使用String,byte[]存储.
 *
 * @see String getKey(String   strKey) 从strKey的字条生成一个Key
 * @see String getEncString(String strMing) 对strMing进行加密,返回String密文
 * @see String getDesString(String strMi) 对strMin进行解密,返回String明文
 * @see byte[] getEncCode(byte[] byteS) byte[]型的加密
 * @see byte[] getDesCode(byte[] byteD) byte[]型的解密
 */
public class DESEncrypt {
    private Key key;
    private byte[] byteMi = null;
    private byte[] byteMing = null;
    private String strMi = "";
    private String strM = "";
    private static DESEncrypt desEncrypt;

    private DESEncrypt(String strKey) {
        try {
            KeyGenerator _generator = KeyGenerator.getInstance("DES");
            _generator.init(new SecureRandom(strKey.getBytes(Charset.forName("UTF-8"))));
            this.key = _generator.generateKey();
            _generator = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DESEncrypt getInstance(String strKey) {
        if (desEncrypt == null)
            desEncrypt = new DESEncrypt(strKey);
        return desEncrypt;
    }

    /**
     * 加密String明文输入,String密文输出
     */
    public void setEncString(String strMing) {
//		BASE64Encoder base64en = new BASE64Encoder();
//		try {
//			this.byteMing = strMing.getBytes("UTF8");
//			this.byteMi = this.getEncCode(this.byteMing);
//			this.strMi = base64en.encode(this.byteMi);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//
//			this.byteMing = null;
//			this.byteMi = null;
//		}
    }

    // 加密以byte[]明文输入,byte[]密文输出
    private byte[] getEncCode(byte[] byteS) {
        byte[] byteFina = null;
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byteFina = cipher.doFinal(byteS);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cipher = null;
        }

        return byteFina;
    }

    // 解密:以String密文输入,String明文输出
    public void setDesString(String strMi) {
//		BASE64Decoder base64De = new BASE64Decoder();
//		try {
//			this.byteMi = base64De.decodeBuffer(strMi);
//			this.byteMing = this.getDesCode(byteMi);
//			this.strM = new String(byteMing, "UTF8");
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			base64De = null;
//			byteMing = null;
//			byteMi = null;
//		}

    }

    // 解密以byte[]密文输入,以byte[]明文输出
    private byte[] getDesCode(byte[] byteD) {
        Cipher cipher;
        byte[] byteFina = null;
        try {
            cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byteFina = cipher.doFinal(byteD);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cipher = null;
        }
        return byteFina;
    }

    // 返回加密后的密文strMi
    public String getStrMi() {
        return strMi;
    }

    // 返回解密后的明文
    public String getStrM() {
        return strM;
    }
}