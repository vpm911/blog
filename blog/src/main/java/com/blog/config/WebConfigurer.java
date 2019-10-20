package com.blog.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.blog.config.helper.H2ConfigurationHelper;
import com.blog.constants.Constants;

/**
 * Configuration for web app using Servlet 3.0 API
 * @author vishal.maradkar
 *
 */
@Configuration
public class WebConfigurer implements ServletContextInitializer, WebServerFactoryCustomizer<WebServerFactory>{

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Environment env;
	
	private final ApplicationProperties applicationProperties;

	@Autowired
	public WebConfigurer(Environment env, ApplicationProperties applicationProperties) {
		this.env = env;
		this.applicationProperties=applicationProperties;
	}


	@Override
	public void customize(WebServerFactory factory) {
		
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		if(env.getActiveProfiles().length!=0) {
			log.info("Web application configuration, using profiles: {}", (Object[]) env.getActiveProfiles());
		}
		if (env.acceptsProfiles(Profiles.of(Constants.SPRING_PROFILE_DEVELOPMENT))) {
            initH2Console(servletContext);
        }
        log.info("Web application fully configured");
	}
	
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = applicationProperties.getCors();
        if (config.getAllowedOrigins() != null && !config.getAllowedOrigins().isEmpty()) {
            log.debug("Registering CORS filter");
            source.registerCorsConfiguration("/api/**", config);
            source.registerCorsConfiguration("/management/**", config);
            source.registerCorsConfiguration("/v2/api-docs", config);
        }
        return new CorsFilter(source);
    }
	/**
     * Initializes H2 console.
     */
    private void initH2Console(ServletContext servletContext) {
        log.debug("Initialize H2 console");
        H2ConfigurationHelper.initH2Console(servletContext);
    }
}
