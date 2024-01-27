package com.api.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.api.util.ToolUtil;
import com.dxc.epos.api.ApiClient;

@Service
public class EposService {

	private static final Logger log = LoggerFactory.getLogger(EposService.class);

	private static final String KEY_OID = "MerchantOrderNo";

	private static final String KEY_MEMBER_ID = "MerchantID";

	private static final String KEY_TRANSAMT = "Amt";

	private static final String KEY_FRONTEND_URL = "ReturnURL";

	private static final String MID = "807426550588001";

	private static final String TID = "80019423";

	private static final String TRANS_CODE_AUTH = "00";

	private static final String TRANS_CODE_CANCEL = "01";

	private static final String PAYMENT_IN_FULL = "0";

	private static final String SECURITY_ID = "cd933a4c414d44de970a0c936199f479";

	private static final String CUSTOMER_IP = "https://dev-sinotwpay.ocard.co/";

	private static final String DOMAIN = "54.95.68.119";

	public JSONObject auth(String ocard) {
		JSONObject result = new JSONObject();
		int rtnCode = 0;
		JSONObject obj = decryptedOcard(ocard);
		ApiClient apiClient = new ApiClient();
		apiClient.clear();
		apiClient.setMid(MID);
		apiClient.setTid(TID);
		apiClient.setOid(obj.optString(KEY_OID));
		apiClient.setTransCode(TRANS_CODE_AUTH);
		apiClient.setMemberId(obj.optString(KEY_MEMBER_ID));
		apiClient.setPan("4058650000000013"); // TODO 卡號
		apiClient.setExpireDate("1912"); // TODO 到期日
		apiClient.setCvv2("000"); // 後三碼
		apiClient.setTransMode(PAYMENT_IN_FULL);
		apiClient.setTransAmt(obj.optString(KEY_TRANSAMT));
		apiClient.setCustomerIp(CUSTOMER_IP);
		apiClient.setDoname(DOMAIN);
		apiClient.setSecurityId(SECURITY_ID);
		apiClient.setFrontendUrl(obj.optString(KEY_FRONTEND_URL));
		try {
			rtnCode = apiClient.post();
			log.info("auth rtnCode: " + rtnCode);
			if (rtnCode > 0) {
				if (rtnCode == 1) {
				} else if (rtnCode == 2) {

				}
			} else {
				result.put("errMsg", "Auth Fail");
			}

		} catch (Exception ex) {
			log.error("auth error: " + ex.getMessage(), ex);
			result.put("errMsg", ex.getMessage());
		}

		result.put("code", rtnCode);
		return result;
	}

	public JSONObject cancel(String ocard) {
		JSONObject result = new JSONObject();
		JSONObject obj = decryptedOcard(ocard);
		int rtnCode = 0;
		ApiClient apiClient = new ApiClient();
		apiClient.clear();
		apiClient.setMid(MID);
		apiClient.setTid(TID);
		apiClient.setOid(obj.optString(KEY_OID));
		apiClient.setTransCode(TRANS_CODE_CANCEL);
		apiClient.setMemberId(obj.optString(KEY_MEMBER_ID));
		apiClient.setCustomerIp(CUSTOMER_IP);
		apiClient.setDoname(DOMAIN);
		apiClient.setSecurityId(SECURITY_ID);
		try {
			rtnCode = apiClient.post();
			if (rtnCode > 0) {
				if (rtnCode == 1) {
				} else if (rtnCode == 2) {

				}
			} else {
				result.put("errMsg", "Cancel Fail");
			}

		} catch (Exception ex) {
			log.error("cancel error: " + ex.getMessage(), ex);
			result.put("errMsg", ex.getMessage());
		}

		result.put("code", rtnCode);
		return result;
	}

	public JSONObject query(String ocard) {
		JSONObject result = new JSONObject();
		int rtnCode = 0;
		JSONObject obj = decryptedOcard(ocard);
		ApiClient apiClient = new ApiClient();
		apiClient.clear();
		apiClient.setMid(MID);
		apiClient.setOid(obj.optString(KEY_OID));
		apiClient.setCustomerIp(CUSTOMER_IP);
		apiClient.setDoname(DOMAIN);
		apiClient.setSecurityId(SECURITY_ID);
		try {
			rtnCode = apiClient.query();
			if (rtnCode > 0) {
				if (rtnCode == 1) {
				} else if (rtnCode == 2) {

				}
			} else {
				result.put("errMsg", "Query Fail");
			}
			log.info("query result obj: " + apiClient.toString());
		} catch (Exception ex) {
			log.error("query error: " + ex.getMessage(), ex);
			result.put("errMsg", ex.getMessage());
		}

		result.put("code", rtnCode);
		return result;
	}

	private JSONObject decryptedOcard(String ocard) {
		String decryptedData = ToolUtil.decrypt(ocard);
		return new JSONObject(decryptedData);
	}
	
}
