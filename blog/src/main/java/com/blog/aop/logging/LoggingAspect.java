package com.blog.aop.logging;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import com.blog.constants.Constants;

/**
 * Aspect for logging execution of service and repository components.
 * 
 * @author vishal.maradkar
 */
@Aspect
public class LoggingAspect {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	Environment env;
	
	@Autowired
	public LoggingAspect(Environment env) {
		this.env = env;
	}
	
	/**
	 * Pointcut that matches all repositories, services and REST enpoints.
	 */
	@Pointcut("within(@org.springframework.stereotype.Repository *)"
		+ " || within(@org.springframework.stereotype.Servce *)"
		+ " || within(@org.springframework.web.bind.annotation.RestController *)")
	public void springBeanPointcut() {
		// Empty method as this is just a pointcut, implementations are in the advices.
	}
	
	@Pointcut("within(com.blog.repository..*) || within(com.blog.service..*) || within(com.blog.rest..*)")
	public void applicationPackagePointcut() {
		// Empty method as this is just a pointcut, implementaions are in the advices.
	}
	
	/**
	 * Advice that logs methods throwing exception
	 * 
	 * @param joinPoint -join point for advice.
	 * @param e -exception
	 */
	@AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
	public void logAfterThrowingException(JoinPoint joinPoint, Throwable e) {
		// Checking profile, not logging stack trace for profiles other than development
		if(env.acceptsProfiles(Profiles.of(Constants.SPRING_PROFILE_DEVELOPMENT))) {
			log.error("Exception in {}.{}() with cause =\'{}\' and exception \'{}\'", joinPoint.getSignature().getDeclaringTypeName()
					,joinPoint.getSignature().getName(), e.getCause()!=null ? e.getCause() : "NULL" ,e.getMessage(),e);
		}else {
			log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
	                joinPoint.getSignature().getName(), e.getCause() != null? e.getCause() : "NULL");
		}
	}
	
	/**
	 * Advice that logs when a method is entered and exited.
	 * 
	 * @param joinPoint join point for advice.
	 * @return result.
	 * @throws Throwable throws {@link IllegalArgumentException}.
	 */
	@Around("applicationPackagePointcut() && springBeanPointcut()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		if(log.isDebugEnabled()) {
			log.debug("Enter: {}.{} with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(), 
					joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
		}
		try {
			Object result = joinPoint.proceed();
			if(log.isDebugEnabled()) {
				log.debug("Exit: {}.{}() with result = {} ", joinPoint.getSignature().getDeclaringTypeName(),
						joinPoint.getSignature().getName(),result);
			}
			return result;
		}catch(IllegalArgumentException e){
			log.error("Illegal Argument: {} in {}.{}() ", Arrays.toString(joinPoint.getArgs()),
					joinPoint.getSignature().getDeclaringTypeName(),joinPoint.getSignature().getName());
			throw e;
		}
	}
	
}
