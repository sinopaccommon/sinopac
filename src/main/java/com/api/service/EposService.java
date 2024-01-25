package com.api.service;

import java.math.BigInteger;
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
import org.springframework.stereotype.Service;

import com.dxc.epos.api.ApiClient;

@Service
public class EposService {

	private static final String MID = "807426550588001";

	private static final String TID = "80019423";

	private static final String TRANS_CODE_AUTH = "00";

	private static final String TRANS_CODE_CANCEL = "01";

	private static final String PAYMENT_IN_FULL = "0";

	private static final String SECURITY_ID = "cd933a4c414d44de970a0c936199f479";
	
	private static final String DOMAIN = "54.95.68.119";

	public int auth(String ocard) {
		int rtnCode = 666;

		JSONObject obj = decryptedOcard(ocard);
		ApiClient apiClient = new ApiClient();
		apiClient.clear();
		apiClient.setMid(MID);
		apiClient.setTid(TID);
		apiClient.setOid(obj.optString("MerchantOrderNo"));
		apiClient.setTransCode(TRANS_CODE_AUTH);
		apiClient.setMemberId(obj.optString("MerchantID"));
		apiClient.setPan("4058650000000013"); // TODO 卡號
		apiClient.setExpireDate("1912"); // TODO 到期日
		apiClient.setCvv2("000"); // 後三碼
		apiClient.setTransMode(PAYMENT_IN_FULL);
		apiClient.setTransAmt(obj.optString("Amt"));
		apiClient.setCustomerIp("54.168.161.147");
		apiClient.setDoname(DOMAIN);
		apiClient.setSecurityId(SECURITY_ID);
		apiClient.setFrontendUrl(obj.optString("ReturnURL"));
		try {
			rtnCode = apiClient.post();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return rtnCode;
	}

	public int cancel(String ocard) {
		int rtnCode = 0;

		JSONObject obj = decryptedOcard(ocard);
		ApiClient apiClient = new ApiClient();
		apiClient.clear();
		apiClient.setMid(MID);
		apiClient.setTid(TID);
		apiClient.setOid(obj.optString("MerchantOrderNo"));
		apiClient.setTransCode(TRANS_CODE_CANCEL);
		apiClient.setMemberId(obj.optString("MerchantID"));
		apiClient.setCustomerIp("54.168.161.147");
		apiClient.setDoname(DOMAIN);
		apiClient.setSecurityId(SECURITY_ID);
		try {
			rtnCode = apiClient.post();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return rtnCode;
	}

	public int query(String ocard) {
		int rtnCode = 0;

		JSONObject obj = decryptedOcard(ocard);
		ApiClient apiClient = new ApiClient();
		apiClient.clear();
		apiClient.setMid(MID);
		apiClient.setOid(obj.optString("MerchantOrderNo"));
		apiClient.setCustomerIp("54.168.161.147");
		apiClient.setDoname(DOMAIN);
		apiClient.setSecurityId(SECURITY_ID);
		try {
			rtnCode = apiClient.post();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return rtnCode;
	}

	private JSONObject decryptedOcard(String ocard) {
		String sourceKey = "Ocard_Sinopac";
		String iv = "QWq2vaaB@juvTYNh";
		String sha256Hash = calculateSHA256(sourceKey);
		String md5Hash = calculateMD5(iv);
		String decryptedData = decrypt(sha256Hash, md5Hash.substring(0,16), ocard);
		return new JSONObject(decryptedData);
	}

	private String calculateSHA256(String input) {
		try {
			MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
			byte[] shaByteArr = mDigest.digest(input.getBytes(StandardCharsets.UTF_8));

			// 將字節轉換為十六進制字符串
			StringBuilder hexStrBuilder = new StringBuilder();
			for (byte b : shaByteArr) {
				hexStrBuilder.append(String.format("%02X", b));
	        }
			return hexStrBuilder.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String calculateMD5(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());

			// 轉換為16進制字符串
			BigInteger no = new BigInteger(1, messageDigest);
			StringBuilder hashText = new StringBuilder(no.toString(16));
			for (int i = 0; i < messageDigest.length; i++) {
				hashText.append(Integer.toHexString(0xFF & messageDigest[i]));
			}
			return hashText.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String decrypt(String key, String initVector, String encryptedData) {
		try {
			byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
			byte[] ivBytes = initVector.getBytes(StandardCharsets.UTF_8);

			// 確保 key 長度是 32 字節 (256 位)
			keyBytes = Arrays.copyOf(keyBytes, 32);

			SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivBytes));

			// 將 Base64 編碼的數據解碼並解密
			byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
			byte[] decryptedBytes = cipher.doFinal(decodedBytes);

			// 將解密後的數據轉換為字符串
			return new String(decryptedBytes, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
