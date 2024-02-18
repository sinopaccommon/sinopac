package com.api.util;

import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.util.StringUtils;

@Service
public class ToolUtil {
	@Value("${pd.sk}")
	private String sourceKey;

	@Value("${pd.iv}")
	private String iv;

	@Value("${api.domain}")
	private String domain;
	
	@Value("${api.customer}")
	private String customerIp;

	public String decrypt(String ocard) {
		return decrypt(sourceKey, iv, ocard);
	}

	public String encrypt(String ocard) {
		return encrypt(sourceKey, iv, ocard);
	}

	private String decrypt(String key, String initVector, String encryptedData) {
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

	public String encrypt(String key, String initVector, String data) {
		try {
			byte[] keyBytes = base64Decode(key).getBytes("UTF-8");
			byte[] ivBytes = base64Decode(initVector).getBytes("UTF-8");
			keyBytes = MessageDigest.getInstance("SHA-256").digest(keyBytes);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
			IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

			byte[] encryptedBytes = cipher.doFinal(data.getBytes());
			return Base64.getEncoder().encodeToString(encryptedBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getDomain() {
		return domain;
	}
	
	public String getCustomerIp() {
		return customerIp;
	}

	private String base64Decode(String encodedString) {
		byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
		return new String(decodedBytes);
	}

	public String maskSubstring(String input, int startIndex, int endIndex) {
		if (StringUtils.isBlank(input)) {
			return "";
		}

		// 確保startIndex和endIndex在有效範圍內
		if (startIndex < 0) {
			startIndex = 0;
		}
		if (endIndex > input.length()) {
			endIndex = input.length();
		}

		// 使用StringBuilder替換需要解碼的部分
		StringBuilder unmaskedStringBuilder = new StringBuilder(input);
		unmaskedStringBuilder.replace(startIndex, endIndex, generateStars(endIndex - startIndex));

		return unmaskedStringBuilder.toString();
	}

	private String generateStars(int count) {
		StringBuilder stars = new StringBuilder();
		for (int i = 0; i < count; i++) {
			stars.append('*');
		}
		return stars.toString();
	}
}
