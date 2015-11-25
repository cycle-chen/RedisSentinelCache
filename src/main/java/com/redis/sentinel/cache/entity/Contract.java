package com.redis.sentinel.cache.entity;

import java.io.Serializable;

public class Contract implements Serializable {
	private static final long serialVersionUID = 1L;
	private String contractCode;

	public String getContractCode() {
		return contractCode;
	}

	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}
}
