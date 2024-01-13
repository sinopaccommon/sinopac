package com.api.service;

import org.springframework.stereotype.Service;

import com.dxc.epos.api.ApiClient;

@Service
public class EposService {

	public int auth() {
		int rtnCode = 0;
		ApiClient apiClient = new ApiClient();
		apiClient.clear();
		apiClient.setMid("660080002010001");
		apiClient.setTid("13999034");
		apiClient.setOid("1234567");
		apiClient.setTransCode("00");
		apiClient.setExpireDate("1912");
		apiClient.setPan("4058650000000013");
		apiClient.setMemberId("543045");
		apiClient.setExpireDate("");
		apiClient.setCvv2("");
		apiClient.setTransMode("0");
		apiClient.setTransAmt("1");
		apiClient.setCustomerIp("127.0.0.1");
		apiClient.setDoname("sinotwpay.ocard.co");
		apiClient.setSecurityId("123456");
		try {
			rtnCode = apiClient.post();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return rtnCode;
	}

}
