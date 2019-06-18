package com.siemens.krawal.krawalcloudmanager.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.authorization.Authorizer;
import com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants;
import com.siemens.krawal.krawalcloudmanager.exception.RequestModificationException;

@Component
public class AuthorizationUtil {

	@Autowired
	private Authorizer authorizer;

	//assumption is that the userId will never be null.
	public String getUserID(String authToken) {
		if (authToken.matches("^" + DBConstants.AUTH_TOKEN_PREFIX + "\\s.+"))
			authToken = authToken.split(" ")[1];
		String userId = authorizer.getUserId(authToken);
		return userId.toUpperCase();

	}

	public boolean validateCheckoutRevision(String userId, String revisionNo, boolean isGetOperation) {

		if (userId.length() > revisionNo.length() && !isGetOperation) {

			throw new RequestModificationException("Not a valid checkout revision!");
		}
		else if (revisionNo.contains("_")) {
			String user = revisionNo.split("_")[0];
			if (!user.equalsIgnoreCase(userId)) {
				throw new RequestModificationException(
						"Only user with GID: " + userId + " can modify/view the chekout revision!");
			}
		}
		return true;
	}
}
