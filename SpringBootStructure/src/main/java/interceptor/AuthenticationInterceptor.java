package com.siemens.krawal.krawalcloudmanager.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.siemens.authorization.Authorizer;
import com.siemens.krawal.krawalcloudmanager.exception.ValidationException;

@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private Authorizer authorizer;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		if (!request.getRequestURL().toString().contains("swagger") && !request.getMethod().equalsIgnoreCase("OPTIONS")) {
			String authToken = request.getHeader("Authorization");
			if (null != authToken) {
				if (authToken.matches("^Bearer\\s.+")) {
					authToken = authToken.split(" ")[1];
				}
				authorizer.validateToken(authToken);

			} else {
				throw new ValidationException("Header \"Authorization\" is missing in the request!!");
			}
		}
		return true;
	}
}
