package com.api.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;

public class TestService {

	public static String encrypt(byte[] sha256Hash, byte[] md5Hash, byte[] byteInput) {
		try {
			SecretKey secretKey = new SecretKeySpec(sha256Hash, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(md5Hash));

			byte[] encryptedBytes = cipher.doFinal(byteInput);
			return Base64.getEncoder().encodeToString(encryptedBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String decrypt(byte[] sha256Hash, byte[] md5Hash, byte[] byteInput) {
		try {
			// 確保 key 長度是 32 字節 (256 位)
			sha256Hash = Arrays.copyOf(sha256Hash, 32);

			SecretKey secretKey = new SecretKeySpec(sha256Hash, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(md5Hash));

			// 將 Base64 編碼的數據解碼並解密
			byte[] decryptedBytes = cipher.doFinal(byteInput);

			// 將解密後的數據轉換為字符串
			return new String(decryptedBytes, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String calculateSHA256(byte[] key) {
		try {
			MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
			byte[] shaByteArr = mDigest.digest(key);

			StringBuilder hexStrBuilder = new StringBuilder();
			for (byte b : shaByteArr) {
				hexStrBuilder.append(b);
			}
			return hexStrBuilder.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String calculateMD5(byte[] key) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(key);

			StringBuilder hexStrBuilder = new StringBuilder();
			for (byte b : messageDigest) {
				hexStrBuilder.append(b);
			}
			return hexStrBuilder.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] encodeToBytes(String input) {
		return Base64.getEncoder().encode(input.getBytes(StandardCharsets.UTF_8));
	}

	public static String decodeToString(byte[] encodedBytes) {
		byte[] decodedBytes = Base64.getDecoder().decode(encodedBytes);
		return new String(decodedBytes, StandardCharsets.UTF_8);
	}

	public static void main(String[] args) {
		String key = "Ocard_Sinopac";
		String iv = "QWq2vaaB@juvTYNh";
		String inPut = "MSvt/w32Ys2d0DH+WUuRry44KTLuZdoxSED8Pyjy1oJHr/c4Qj/I8S8eSyALMnH1T5BLOl40cH+FiCuDwom2S6oJAagYrvdXEDLPr1B7esCkWsMDySLVjRpkm4i/rNs+pJuWptWR+86OmUcPmFseFKrEqrpXT6va1NDU17lfNwsR3EUTDsYAASIiEWsNLPUgAaXQA8JxFzMvL4BUJV6+D3N/3+3O902j3+++4KMtwv9HcMh4e2bbOAc1xtZIa08E9SbfcCn5rBoP/wgl/SDoIqeS1OPHeGk+0PTTByNpAe2s7J6mzGHreAMNcPYOniDzxOGUHM1yXGFVB96HgMfG9y098MVt1TLKjSi4ZY6hX/Y4TdBmoecdwOsAZ4+1pVvrod0poRNRUtaDchsiaNZkGBLUN+537NMfzfj48cKbxoTod7ON7DK5w/ubRBR9l4QBtFE4+v9P+OmYsIcza0p8D1vVGcX8tRA/B2kjHl0u98evp92gQz/uGmQDdjNZqiC6cLUilLduDsCCP8qK4w66Og==";

		try {
			// step1
			byte[] byteKey = key.getBytes(StandardCharsets.UTF_8);
			byte[] byteIv = iv.getBytes(StandardCharsets.UTF_8);
			// step2
			MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
			byte[] sha256Hash = mDigest.digest(byteKey);
			// step3
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] md5Hash = md.digest(byteIv);
			// step4
			byte[] byteInput = Base64.getDecoder().decode(inPut);
			// step5
			String decryptedData = decrypt(sha256Hash, md5Hash, byteInput);
			System.out.println("解密後的JSON數據：" + decryptedData);

			// 要加密的JSON數據
			JSONObject obj = new JSONObject();
			obj.put("MerchantID", "ocardtest123");
			obj.put("RespondType", "JSON");
			obj.put("TimeStamp", 1706164644);
			obj.put("Version", "1.5");
			obj.put("LangType", "zh-tw");
			obj.put("MerchantOrderNo", "0YXOJ3G");
			obj.put("Amt", 998);
			obj.put("ItemDesc", "\\u4e00\\u676f\\u7d05\\u8336");
			obj.put("Email", "victor@ocard.co");
			obj.put("LoginType", 0);
			obj.put("ReturnURL", "https://api-order-test.ocard.co/Newebpay/redirect?token=0YXOJ3G");
			obj.put("NotifyURL", "https://api-order-test.ocard.co/Newebpay/notify?token=0YXOJ3G");

			byte[] byteJson = Base64.getEncoder().encode(obj.toString().getBytes(StandardCharsets.UTF_8));
			// 加密JSON數據
	        String encryptedData = encrypt(sha256Hash, md5Hash, byteJson);
	        System.out.println("加密後的數據：" + encryptedData);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		

	}
}
