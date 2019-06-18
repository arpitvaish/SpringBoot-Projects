package com.siemens.krawal.krawalcloudmanager.aspect;

public final class AspectConstants {

	private AspectConstants() {

	}
	
	public static final String USER_LOG = "API Invoked -> {} ----- User -> {} ";
	public static final String ENTRY_METHOD = "entry -> method -> {} ";
	public static final String METHOD_EXECUTION_TIME = "Method execution time in ms-> {} ";
	public static final String EXIT_METHOD = "exit -> method -> {} ";
	public static final String EXCEPTION_CAUGHT = "Exception caught in method!! {}";
	public static final String METHOD_ARGUMENTS = " with arguments {} ";
	public static final String EXCEPTION_MESSAGE = "and the full toString: {} ";
	public static final String EXCEPTION = "the exception is: {} ";
}
