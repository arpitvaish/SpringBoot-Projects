package com.siemens.krawal.krawalcloudmanager.manager;

import static com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants.UNDERSCORE_STRING;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.dao.RevisionDAO;
import com.siemens.krawal.krawalcloudmanager.db.model.CheckoutRevisionResponse;
import com.siemens.krawal.krawalcloudmanager.exception.DBException;
import com.siemens.krawal.krawalcloudmanager.model.CommitRevisionRequest;

@Component
public class RevisionManager {

	@Autowired
	private RevisionDAO revisionDAO;

	/**
	 * Create checkout revision, add revision to project, associate user with
	 * revision
	 * 
	 * @param projectName
	 * @param revisionNumber
	 * @param userId
	 * @return
	 */
	public String checkoutRevision(String projectName, int revisionNumber, String userId) {

		String checkoutRevisionId = userId + UNDERSCORE_STRING + revisionNumber;
		CheckoutRevisionResponse response = revisionDAO.checkoutRevisionExists(projectName, userId, checkoutRevisionId);
		if (null != response.getLatestRevisionNumber()) {
			if (null != response.getCheckoutRevision()) {
				throw new DBException("Checkout revision already exists!!");
			} else {
				int latestRevisionNumber = Integer.parseInt(response.getLatestRevisionNumber().stringValue());
				if (revisionNumber != latestRevisionNumber) {
					throw new DBException(
							"Checkout is supported only from the latest revision:" + latestRevisionNumber);
				} else {
					revisionDAO.createCheckoutRevision(projectName, userId, checkoutRevisionId);
				}
			}
		} else {
			throw new DBException("Project is not found or permission is denied for user!");
		}
		return checkoutRevisionId;
	}

	/**
	 * verify whether user is allowed to delete the checkout revision & delete
	 * 
	 * @param projectName
	 * @param crId
	 * @param userId
	 */
	public void deleteCheckoutRevision(String projectName, String crId, String userId) {

		CheckoutRevisionResponse response = revisionDAO.verifyUserAccessCheckoutRevision(projectName, userId, crId);
		if (null != response.getUser()) {
			revisionDAO.deleteCheckoutRevision(projectName, crId, userId);
		} else {
			throw new DBException("User is not permitted to delete the revision!");
		}
	}

	/**
	 * 
	 * @param projectName
	 * @param crId
	 * @param userId
	 * @param request
	 * @return
	 */
	public int commitRevision(String projectName, String crId, String userId, CommitRevisionRequest request) {
		CheckoutRevisionResponse response = revisionDAO.verifyUserAccessCheckoutRevision(projectName, userId, crId);
		int committedRevisionNo = 0;
		if (null != response.getUser()) {
			int revNumber = Integer.parseInt(crId.split("_")[1]);
			committedRevisionNo = revNumber + 1;
			revisionDAO.deleteTuples(projectName, crId);
			revisionDAO.synchronizedCommit(revNumber, projectName, response.getCheckoutRevision(), crId);
			revisionDAO.updateCommittedRevision(projectName, response.getCheckoutRevision(), request.getComment(),
					userId);
		} else {
			throw new DBException("User is not permitted to commit the revision!");
		}
		return committedRevisionNo;
	}
}
