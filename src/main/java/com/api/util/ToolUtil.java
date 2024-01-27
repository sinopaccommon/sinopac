package com.api.util;

import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;

public class ToolUtil {
	@Value("${pd.sk}")
	private static String sourceKey;

	@Value("${pd.iv}")
	private static String iv;

	public static String decrypt(String ocard) {
		return decrypt(sourceKey, iv, ocard);
	}

	private static String decrypt(String key, String initVector, String encryptedData) {
		try {
			byte[] keyBytes = base64Decode(key).getBytes("UTF-8");
			byte[] ivBytes = base64Decode(initVector).getBytes("UTF-8");

			// 確保 key 長度是 32 字節 (256 位)
			keyBytes = MessageDigest.getInstance("SHA-256").digest(keyBytes);

			SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivBytes));

			// 將 Base64 編碼的數據解碼並進行解密
			byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
			byte[] decryptedBytes = cipher.doFinal(decodedBytes);

			return new String(decryptedBytes, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String base64Decode(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        return new String(decodedBytes);
    }
}
