package com.siemens.krawal.krawalcloudmanager.model;

import java.util.List;

public class CycleSegment {

	private int krawalId;

	private List<Integer> ids;

	public List<Integer> getIds() {
		return ids;
	}

	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}

	public int getKrawalId() {
		return krawalId;
	}

	public void setKrawalId(int krawalId) {
		this.krawalId = krawalId;
	}
	
}
