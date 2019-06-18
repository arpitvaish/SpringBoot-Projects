package com.siemens.krawal.krawalcloudmanager.model.response;

import java.util.List;

import com.siemens.krawal.krawalcloudmanager.model.Loadpoint;

public class FetchAllLoadpointsResponse {

	private List<Loadpoint> loadpoints;

	public List<Loadpoint> getLoadpoints() {
		return loadpoints;
	}

	public void setLoadpoints(List<Loadpoint> loadpoints) {
		this.loadpoints = loadpoints;
	}
}
