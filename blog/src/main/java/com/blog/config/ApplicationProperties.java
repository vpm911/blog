package com.blog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Properties specific to Blog App.
 * 
 * Properties are configured in the {@application.properties} file.
 * 
 * @author vishal.maradkar
 *
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

	private final CorsConfiguration cors = new CorsConfiguration();

	public CorsConfiguration getCors() {
		return cors;
	}


}
