package com.api.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.util.ToolUtil;
import com.dxc.epos.api.ApiClient;

@Service
public class EposService {

	private static final Logger log = LoggerFactory.getLogger(EposService.class);

	private static final String KEY_OID = "MerchantOrderNo";

	private static final String KEY_MEMBER_ID = "MerchantID";

	private static final String KEY_CARD = "card";

	private static final String KEY_PAN = "card_no";

	private static final String KEY_EXPIRE_DATE = "expired_date";

	private static final String KEY_CVV2 = "cvc";

	private static final String KEY_TRANSAMT = "Amt";

	private static final String KEY_FRONTEND_URL = "ReturnURL";

	private static final String MID = "807426550588001";

	private static final String TID = "80019423";

	private static final String TRANS_CODE_AUTH = "00";

	private static final String TRANS_CODE_CANCEL = "01";

	private static final String PAYMENT_IN_FULL = "0";

	private static final String SECURITY_ID = "cd933a4c414d44de970a0c936199f479";

	private static final String CUSTOMER_IP = "https://dev-sinotwpay.ocard.co/";

	private static final String DOMAIN = "eposuat.sinopac.com";

	@Autowired
	private ToolUtil toolUtil;

	public String auth(String ocard) {
		log.info("auth start...");

		JSONObject obj = parseOcard(ocard);
		JSONObject result = new JSONObject();
		result.put("Oid", obj.optString(KEY_OID));

		int rtnCode = 0;
		try {
			JSONObject cardObj = obj.optJSONObject(KEY_CARD);
			if (cardObj == null) {
				throw new Exception("auth can not find card");
			}

			log.info("auth [MerchantOrderNo]: " + obj.optString(KEY_OID) + ", [CardNo]: "
					+ toolUtil.maskSubstring(cardObj.optString(KEY_PAN), 9, 15) + ", [Amt]: "
					+ obj.optString(KEY_TRANSAMT));

			ApiClient apiClient = new ApiClient();
			apiClient.clear();
			apiClient.setMid(MID);
			apiClient.setTid(TID);
			apiClient.setOid(obj.optString(KEY_OID));
			apiClient.setTransCode(TRANS_CODE_AUTH);
			apiClient.setMemberId(obj.optString(KEY_MEMBER_ID));
			apiClient.setPan(cardObj.optString(KEY_PAN)); // 卡號
			apiClient.setExpireDate(cardObj.optString(KEY_EXPIRE_DATE)); // 到期日
			apiClient.setCvv2(cardObj.optString(KEY_CVV2)); // 後三碼
			apiClient.setTransMode(PAYMENT_IN_FULL);
			apiClient.setTransAmt(obj.optString(KEY_TRANSAMT));
			apiClient.setCustomerIp(CUSTOMER_IP);
			apiClient.setDoname(DOMAIN);
			apiClient.setSecurityId(SECURITY_ID);
			apiClient.setFrontendUrl(obj.optString(KEY_FRONTEND_URL));

			rtnCode = apiClient.post();
			log.info("auth rtnCode: " + rtnCode);
			if (rtnCode > 0) {
				if (rtnCode == 1) {
					result.put("html", apiClient.getHtml());
				} else if (rtnCode == 2) {
					result.put("TransDate", apiClient.getTransDate());
					result.put("TransTime", apiClient.getTransTime());
					result.put("TransCode", apiClient.getTransCode());
					result.put("TransMode", apiClient.getTransMode());
					result.put("TransAmt", apiClient.getTransAmt());
					result.put("ApproveCode", apiClient.getApproveCode());
					result.put("ResponseCode", apiClient.getResponseCode());
					result.put("ResponseMsg", apiClient.getResponseMsg());
					result.put("RequestDate", apiClient.getRequestDate());
					result.put("RequestAmt", apiClient.getRequestAmt());
					result.put("Execute", apiClient.getExecute());
					result.put("InstallmentType", apiClient.getInstallmentType());
					result.put("FirstAmt", apiClient.getFirstAmt());
					result.put("EachAmt", apiClient.getEachAmt());
					result.put("Fee", apiClient.getFee());
					result.put("RedeemType", apiClient.getRedeemType());
					result.put("RedeemUsed", apiClient.getRedeemUsed());
					result.put("RedeemBalance", apiClient.getRedeemBalance());
					result.put("CreditAmt", apiClient.getCreditAmt());
					result.put("OnusFlag", apiClient.getOnusFlag());
					result.put("SecureStatus", apiClient.getSecureStatus());
				}
			} else {
				result.put("errMsg", "Auth Fail");
			}

		} catch (Exception ex) {
			log.error("auth error: " + ex.getMessage(), ex);
			result.put("errMsg", ex.getMessage());
		}

		log.info("auth end...");

		result.put("code", rtnCode);
		return toolUtil.encrypt(result.toString());
	}

	public String cancel(String ocard) {
		log.info("cancel start...");

		JSONObject obj = parseOcard(ocard);
		JSONObject result = new JSONObject();
		result.put("Oid", obj.optString(KEY_OID));
		log.info("cancel [MerchantOrderNo]: " + obj.optString(KEY_OID));

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
					result.put("html", apiClient.getHtml());
				} else if (rtnCode == 2) {
					result.put("TransDate", apiClient.getTransDate());
					result.put("TransTime", apiClient.getTransTime());
					result.put("TransCode", apiClient.getTransCode());
					result.put("TransMode", apiClient.getTransMode());
					result.put("TransAmt", apiClient.getTransAmt());
					result.put("ApproveCode", apiClient.getApproveCode());
					result.put("ResponseCode", apiClient.getResponseCode());
					result.put("ResponseMsg", apiClient.getResponseMsg());
					result.put("RequestDate", apiClient.getRequestDate());
					result.put("RequestAmt", apiClient.getRequestAmt());
					result.put("Execute", apiClient.getExecute());
					result.put("InstallmentType", apiClient.getInstallmentType());
					result.put("FirstAmt", apiClient.getFirstAmt());
					result.put("EachAmt", apiClient.getEachAmt());
					result.put("Fee", apiClient.getFee());
					result.put("RedeemType", apiClient.getRedeemType());
					result.put("RedeemUsed", apiClient.getRedeemUsed());
					result.put("RedeemBalance", apiClient.getRedeemBalance());
					result.put("CreditAmt", apiClient.getCreditAmt());
					result.put("OnusFlag", apiClient.getOnusFlag());
					result.put("SecureStatus", apiClient.getSecureStatus());
				}
			} else {
				result.put("errMsg", "Cancel Fail");
			}

		} catch (Exception ex) {
			log.error("cancel error: " + ex.getMessage(), ex);
			result.put("errMsg", ex.getMessage());
		}

		result.put("code", rtnCode);

		log.info("cancel end...");
		return toolUtil.encrypt(result.toString());
	}

	public String query(String ocard) {
		log.info("query start...");

		JSONObject obj = parseOcard(ocard);
		JSONObject result = new JSONObject();
		result.put("Oid", obj.optString(KEY_OID));
		log.info("query [MerchantOrderNo]: " + obj.optString(KEY_OID));

		int rtnCode = 0;
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
					result.put("html", apiClient.getHtml());
				} else if (rtnCode == 2) {
					result.put("TransDate", apiClient.getTransDate());
					result.put("TransTime", apiClient.getTransTime());
					result.put("TransCode", apiClient.getTransCode());
					result.put("TransMode", apiClient.getTransMode());
					result.put("TransAmt", apiClient.getTransAmt());
					result.put("ApproveCode", apiClient.getApproveCode());
					result.put("ResponseCode", apiClient.getResponseCode());
					result.put("ResponseMsg", apiClient.getResponseMsg());
					result.put("RequestDate", apiClient.getRequestDate());
					result.put("RequestAmt", apiClient.getRequestAmt());
					result.put("Execute", apiClient.getExecute());
					result.put("InstallmentType", apiClient.getInstallmentType());
					result.put("FirstAmt", apiClient.getFirstAmt());
					result.put("EachAmt", apiClient.getEachAmt());
					result.put("Fee", apiClient.getFee());
					result.put("RedeemType", apiClient.getRedeemType());
					result.put("RedeemUsed", apiClient.getRedeemUsed());
					result.put("RedeemBalance", apiClient.getRedeemBalance());
					result.put("CreditAmt", apiClient.getCreditAmt());
					result.put("OnusFlag", apiClient.getOnusFlag());
					result.put("SecureStatus", apiClient.getSecureStatus());
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
		log.info("query end...");
		return toolUtil.encrypt(result.toString());
	}

	private JSONObject parseOcard(String ocard) {
		String decryptedData = toolUtil.decrypt(ocard);
		return new JSONObject(decryptedData);
	}

}
