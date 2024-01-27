package com.api.service;

import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dxc.epos.api.ApiClient;

@Service
public class EposService {

	private static final Logger log = LoggerFactory.getLogger(EposService.class);
	
	private static final String MID = "807426550588001";

	private static final String TID = "80019423";

	private static final String TRANS_CODE_AUTH = "00";

	private static final String TRANS_CODE_CANCEL = "01";

	private static final String PAYMENT_IN_FULL = "0";

	private static final String SECURITY_ID = "cd933a4c414d44de970a0c936199f479";
	
	private static final String DOMAIN = "54.95.68.119";

	public int auth(String ocard) {
		int rtnCode = 0;

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
			log.info("auth apiClient: " + apiClient.toString());
			rtnCode = apiClient.post();
			
			log.info("auth result obj: " + apiClient.toString());
		} catch (Exception ex) {
			log.error("auth error: " + ex.getMessage(), ex);
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
			
			log.info("cancel result obj: " + apiClient.toString());
		} catch (Exception ex) {
			log.error("cancel error: " + ex.getMessage(), ex);
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
			rtnCode = apiClient.query();
			
			log.info("query result obj: " + apiClient.toString());
		} catch (Exception ex) {
			log.error("query error: " + ex.getMessage(), ex);
		}

		return rtnCode;
	}

	private JSONObject decryptedOcard(String ocard) {
		String sourceKey = "Ocard_Sinopac";
		String iv = "QWq2vaaB@juvTYNh";
		String decryptedData = decrypt(sourceKey, iv, ocard);
		return new JSONObject(decryptedData);
	}

	private String decrypt(String key, String initVector, String encryptedData) {
        try {
            byte[] keyBytes = key.getBytes("UTF-8");
            byte[] ivBytes = initVector.getBytes("UTF-8");

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
}
