package com.siemens.krawal.krawalcloudmanager.model;

import java.util.Map;

public class DataDTO {

	private int krawalId;
	private Map<String,String> data;
	
	public int getKrawalId() {
		return krawalId;
	}
	public void setKrawalId(int krawalId) {
		this.krawalId = krawalId;
	}
	public Map<String, String> getData() {
		return data;
	}
	public void setData(Map<String, String> data) {
		this.data = data;
	}
}
