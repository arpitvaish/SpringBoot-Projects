package com.siemens.krawal.krawalcloudmanager.db.model;

import org.eclipse.rdf4j.model.Value;

public class CheckoutRevisionResponse {

	private Value latestRevisionNumber;
	private Value checkoutRevision;
	private Value user;
	
	public Value getLatestRevisionNumber() {
		return latestRevisionNumber;
	}
	public void setLatestRevisionNumber(Value latestRevisionNumber) {
		this.latestRevisionNumber = latestRevisionNumber;
	}
	public Value getCheckoutRevision() {
		return checkoutRevision;
	}
	public void setCheckoutRevision(Value checkoutRevision) {
		this.checkoutRevision = checkoutRevision;
	}
	public Value getUser() {
		return user;
	}
	public void setUser(Value user) {
		this.user = user;
	}
}
