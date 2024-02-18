package com.api.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.data.JsonKey;
import com.api.data.TransCode;
import com.api.util.ToolUtil;
import com.dxc.epos.api.ApiClient;

import io.micrometer.core.instrument.util.StringUtils;

@Service
public class EposService {

	private static final Logger log = LoggerFactory.getLogger(EposService.class);

	private static final String PAYMENT_IN_FULL = "0";

	@Autowired
	private ToolUtil toolUtil;

	public String auth(String ocard) {
		log.info("auth start...");

		JSONObject obj = parseOcard(ocard);
		JSONObject result = new JSONObject();
		result.put("Oid", obj.optString(JsonKey.OID));

		int rtnCode = 0;
		try {
			if (StringUtils.isBlank(obj.optString(JsonKey.PAN))) {
				throw new Exception("auth can not find card");
			}

			log.info("auth [Oid]: " + obj.optString(JsonKey.OID) + ", [Pan]: "
					+ toolUtil.maskSubstring(obj.optString(JsonKey.PAN), 9, 15) + ", [TransAmt]: "
					+ obj.optString(JsonKey.TRANS_AMT));

			ApiClient apiClient = new ApiClient();
			apiClient.clear();
			apiClient.setMid(obj.optString(JsonKey.MID));
			apiClient.setTid(obj.optString(JsonKey.TID));
			apiClient.setOid(obj.optString(JsonKey.OID));
			apiClient.setTransCode(TransCode.AUTH);
			apiClient.setMemberId(obj.optString(JsonKey.MEMBER_ID));
			apiClient.setPan(obj.optString(JsonKey.PAN)); // 卡號
			apiClient.setExpireDate(obj.optString(JsonKey.EXPIRE_DATE)); // 到期日
			apiClient.setCvv2(obj.optString(JsonKey.CVV2)); // 後三碼
			apiClient.setTransMode(PAYMENT_IN_FULL);
			apiClient.setTransAmt(obj.optString(JsonKey.TRANS_AMT));
			apiClient.setCustomerIp(toolUtil.getCustomerIp());
			apiClient.setDoname(toolUtil.getDomain());
			apiClient.setSecurityId(obj.optString(JsonKey.SECURITY_ID));
			apiClient.setFrontendUrl(obj.optString(JsonKey.FRONTEND_URL));

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
		result.put("Oid", obj.optString(JsonKey.OID));
		log.info("cancel [Oid]: " + obj.optString(JsonKey.OID));

		int rtnCode = 0;
		ApiClient apiClient = new ApiClient();
		apiClient.clear();
		apiClient.setMid(obj.optString(JsonKey.MID));
		apiClient.setTid(obj.optString(JsonKey.TID));
		apiClient.setOid(obj.optString(JsonKey.OID));
		apiClient.setTransCode(TransCode.CANCEL);
		apiClient.setMemberId(obj.optString(JsonKey.MEMBER_ID));
		apiClient.setCustomerIp(toolUtil.getCustomerIp());
		apiClient.setDoname(toolUtil.getDomain());
		apiClient.setSecurityId(obj.optString(JsonKey.SECURITY_ID));
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
		result.put("Oid", obj.optString(JsonKey.OID));
		log.info("query [Oid]: " + obj.optString(JsonKey.OID));

		int rtnCode = 0;
		ApiClient apiClient = new ApiClient();
		apiClient.clear();
		apiClient.setMid(obj.optString(JsonKey.MID));
		apiClient.setOid(obj.optString(JsonKey.OID));
		apiClient.setCustomerIp(toolUtil.getCustomerIp());
		apiClient.setDoname(toolUtil.getDomain());
		apiClient.setSecurityId(obj.optString(JsonKey.SECURITY_ID));
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

	public String capture(String ocard) {
		log.info("capture start...");

		JSONObject obj = parseOcard(ocard);
		JSONObject result = new JSONObject();
		result.put("Oid", obj.optString(JsonKey.OID));

		int rtnCode = 0;
		try {
			log.info("capture [Oid]: " + obj.optString(JsonKey.OID));

			ApiClient apiClient = new ApiClient();
			apiClient.clear();
			apiClient.setMid(obj.optString(JsonKey.MID));
			apiClient.setTid(obj.optString(JsonKey.TID));
			apiClient.setOid(obj.optString(JsonKey.OID));
			apiClient.setTransCode(TransCode.CAPTURE);
			apiClient.setMemberId(obj.optString(JsonKey.MEMBER_ID));
			apiClient.setTransAmt(obj.optString(JsonKey.TRANS_AMT));
			apiClient.setApproveCode(obj.optString(JsonKey.APPROVE_CODE));
			apiClient.setSecurityId(obj.optString(JsonKey.SECURITY_ID));
			apiClient.setCustomerIp(toolUtil.getCustomerIp());
			apiClient.setDoname(toolUtil.getDomain());

			rtnCode = apiClient.post();
			log.info("capture rtnCode: " + rtnCode);
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
				result.put("errMsg", "capture Fail");
			}

		} catch (Exception ex) {
			log.error("capture error: " + ex.getMessage(), ex);
			result.put("errMsg", ex.getMessage());
		}

		log.info("capture end...");

		result.put("code", rtnCode);
		return toolUtil.encrypt(result.toString());
	}

	public String refund(String ocard) {
		log.info("refund start...");

		JSONObject obj = parseOcard(ocard);
		JSONObject result = new JSONObject();
		result.put("Oid", obj.optString(JsonKey.OID));

		int rtnCode = 0;
		try {
			if (StringUtils.isBlank(obj.optString(JsonKey.PAN))) {
				throw new Exception("refund can not find card");
			}

			log.info("refund [Oid]: " + obj.optString(JsonKey.OID) + ", [Pan]: "
					+ toolUtil.maskSubstring(obj.optString(JsonKey.PAN), 9, 15) + ", [TransAmt]: "
					+ obj.optString(JsonKey.TRANS_AMT));

			ApiClient apiClient = new ApiClient();
			apiClient.clear();
			apiClient.setMid(obj.optString(JsonKey.MID));
			apiClient.setTid(obj.optString(JsonKey.TID));
			apiClient.setOid(obj.optString(JsonKey.OID));
			apiClient.setTransCode(TransCode.REFUND);
			apiClient.setMemberId(obj.optString(JsonKey.MEMBER_ID));
			apiClient.setTransAmt(obj.optString(JsonKey.TRANS_AMT));
			apiClient.setApproveCode(obj.optString(JsonKey.APPROVE_CODE));
			apiClient.setSecurityId(obj.optString(JsonKey.SECURITY_ID));
			apiClient.setCustomerIp(toolUtil.getCustomerIp());
			apiClient.setDoname(toolUtil.getDomain());

			rtnCode = apiClient.post();
			log.info("refund rtnCode: " + rtnCode);
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
				result.put("errMsg", "refund Fail");
			}

		} catch (Exception ex) {
			log.error("refund error: " + ex.getMessage(), ex);
			result.put("errMsg", ex.getMessage());
		}

		log.info("refund end...");

		result.put("code", rtnCode);
		return toolUtil.encrypt(result.toString());
	}

	private JSONObject parseOcard(String ocard) {
		String decryptedData = toolUtil.decrypt(ocard);
		return new JSONObject(decryptedData);
	}

}
