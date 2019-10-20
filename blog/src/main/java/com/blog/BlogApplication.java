package com.blog;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import com.blog.config.ApplicationProperties;
import com.blog.config.DefaultProfileUtil;
import com.blog.constants.Constants;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class,LiquibaseProperties.class})
public class BlogApplication implements InitializingBean{

	private static final Logger log = LoggerFactory.getLogger(BlogApplication.class);
	
	@Autowired
	Environment env;
	
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(BlogApplication.class);
		DefaultProfileUtil.addDefaultProfile(app);
		Environment env = app.run(args).getEnvironment();
		logApplicationStartup(env);
	}

	
	/**
	 * Initializes the application
	 * <p>
	 * Spring Profiles can be configured with a program argument --spring.profiles.active=your-profile-name
	 * </p>
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
		if(activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(Constants.SPRING_PROFILE_PRODUCTION)) {
			log.error("You have misconfigured your application! It should not user both 'dev' and 'prod' profiles at the same time");
		}
	}

	  private static void logApplicationStartup(Environment env) {
	        String protocol = "http";
	        if (env.getProperty("server.ssl.key-store") != null) {
	            protocol = "https";
	        }
	        String serverPort = env.getProperty("server.port");
	        String contextPath = env.getProperty("server.servlet.context-path");
	        if (StringUtils.isBlank(contextPath)) {
	            contextPath = "/";
	        }
	        String hostAddress = "localhost";
	        try {
	            hostAddress = InetAddress.getLocalHost().getHostAddress();
	        } catch (UnknownHostException e) {
	            log.warn("The host name could not be determined, using `localhost` as fallback");
	        }
	        log.info("\n----------------------------------------------------------\n\t" +
	                "Application '{}' is running! Access URLs:\n\t" +
	                "Local: \t\t{}://localhost:{}{}\n\t" +
	                "External: \t{}://{}:{}{}\n\t" +
	                "Profile(s): \t{}\n----------------------------------------------------------",
	            env.getProperty("spring.application.name"),
	            protocol,
	            serverPort,
	            contextPath,
	            protocol,
	            hostAddress,
	            serverPort,
	            contextPath,
	            env.getActiveProfiles());
	    }

}
