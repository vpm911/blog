package com.blog.constants;

/**
 * Constants for the application
 * 
 * @author vishal.maradkar
 *
 */
public interface Constants {

	// Spring profiles for development, test and production
	/** Constant <code>SPRING_PROFILE_DEVELOPMENT="dev"</code> */
	String SPRING_PROFILE_DEVELOPMENT = "dev";
	/** Constant <code>SPRING_PROFILE_TEST="test"</code> */
	String SPRING_PROFILE_TEST = "test";
	/** Constant <code>SPRING_PROFILE_PRODUCTION="prod"</code> */
	String SPRING_PROFILE_PRODUCTION = "prod";

	/**
	 * Spring profile used to disable running liquibase Constant
	 * <code>SPRING_PROFILE_NO_LIQUIBASE="no-liquibase"</code>
	 */
	String SPRING_PROFILE_NO_LIQUIBASE = "no-liquibase";
}
