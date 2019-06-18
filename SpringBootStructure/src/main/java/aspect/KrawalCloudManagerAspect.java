package com.siemens.krawal.krawalcloudmanager.aspect;

import static com.siemens.krawal.krawalcloudmanager.aspect.AspectConstants.ENTRY_METHOD;
import static com.siemens.krawal.krawalcloudmanager.aspect.AspectConstants.EXCEPTION;
import static com.siemens.krawal.krawalcloudmanager.aspect.AspectConstants.EXCEPTION_CAUGHT;
import static com.siemens.krawal.krawalcloudmanager.aspect.AspectConstants.EXCEPTION_MESSAGE;
import static com.siemens.krawal.krawalcloudmanager.aspect.AspectConstants.EXIT_METHOD;
import static com.siemens.krawal.krawalcloudmanager.aspect.AspectConstants.METHOD_ARGUMENTS;
import static com.siemens.krawal.krawalcloudmanager.aspect.AspectConstants.METHOD_EXECUTION_TIME;
import static com.siemens.krawal.krawalcloudmanager.aspect.AspectConstants.USER_LOG;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.siemens.krawal.krawalcloudmanager.util.AuthorizationUtil;

@Aspect
@Component
public class KrawalCloudManagerAspect {

	@Autowired
	private AuthorizationUtil authorizationUtil;

	private static final Logger LOGGER = LoggerFactory.getLogger(KrawalCloudManagerAspect.class);

	@Pointcut("execution(* com.siemens.krawal.krawalcloudmanager.controller.*.*(..))")
	public void pointCutMethods() {
		// no body required as the method is to define the pointcuts in
		// annotation
	}

	@Around("pointCutMethods()")
	public Object profileMethods(ProceedingJoinPoint pjp) throws Throwable {

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		String authToken = request.getHeader("Authorization");
		String userId = authorizationUtil.getUserID(authToken);

		long start = System.currentTimeMillis();
		Object output = null;

		LOGGER.info("{}", pjp.getTarget().getClass());
		LOGGER.info(USER_LOG, pjp.getSignature().getName(), userId);
		LOGGER.info(ENTRY_METHOD, pjp.getSignature().getName());

		try {

			output = pjp.proceed();
			long elapsedTime = System.currentTimeMillis() - start;
			LOGGER.info(METHOD_EXECUTION_TIME, elapsedTime);
			LOGGER.info(EXIT_METHOD, pjp.getSignature().getName());

		} catch (Throwable t) {
			LOGGER.error(EXCEPTION_CAUGHT);
			throw t;
		}
		return output;
	}

	@AfterThrowing(pointcut = "execution(* com.siemens.krawal.krawalcloudmanager.controller.*.*(..))", throwing = "ex")
	public void doRecoveryActions(JoinPoint joinPoint, Throwable ex) {

		Signature signature = joinPoint.getSignature();
		String methodName = signature.getName();
		String stuff = signature.toString();
		String arguments = Arrays.toString(joinPoint.getArgs());
		LOGGER.error(EXCEPTION_CAUGHT, methodName);
		LOGGER.error(METHOD_ARGUMENTS, arguments);
		LOGGER.error(EXCEPTION_MESSAGE, stuff);
		LOGGER.error(EXCEPTION, ex.getMessage());

	}
}
