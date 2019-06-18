package com.siemens.krawal.krawalcloudmanager.dao;

import java.util.Map;
import java.util.Set;

import org.eclipse.rdf4j.model.Value;

import com.siemens.krawal.krawalcloudmanager.db.model.CheckoutRevisionResponse;

public interface RevisionDAO {

	public CheckoutRevisionResponse checkoutRevisionExists(String projectName, String userId, String crId);

	public void createCheckoutRevision(String projectName, String userId, String checkoutRevisionId);

	public CheckoutRevisionResponse verifyUserAccessCheckoutRevision(String projectName, String userId, String crId);

	public void deleteCheckoutRevision(String projectName, String crId, String userId);

	public void synchronizedCommit(int revNumber, String projectName, Value checkoutRevision, String crId);

	public void updateCommittedRevision(String projectName, Value checkoutRevision, String comment, String userId);

	public Map<String, Value> fetchUsers(Set<String> users);

	public void deleteTuples(String projectName, String crId);

}
