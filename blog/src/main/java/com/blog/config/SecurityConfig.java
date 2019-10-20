package com.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import com.blog.security.AuthoritiesConstants;
import com.blog.security.jwt.JWTConfigurer;
import com.blog.security.jwt.TokenProvider;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true , securedEnabled = true)
@Import(SecurityProblemSupport.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final TokenProvider tokenProvider;
	private final CorsFilter corsFilter;
    private final SecurityProblemSupport problemSupport;

    @Autowired
	public SecurityConfig(TokenProvider tokenProvider, CorsFilter corsFilter, SecurityProblemSupport problemSupport) {
		this.tokenProvider = tokenProvider;
		this.corsFilter = corsFilter;
		this.problemSupport = problemSupport;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.antMatchers(HttpMethod.OPTIONS,"/**")
			.antMatchers("/app/**/*.js")
			 .antMatchers("/i18n/**")
	         .antMatchers("/content/**")
	         .antMatchers("/h2-console/**")
	         .antMatchers("/swagger-ui/index.html")
	         .antMatchers("/test/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.
			csrf().disable()
			.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling()
			.authenticationEntryPoint(problemSupport)
			.accessDeniedHandler(problemSupport)
		.and()
			.headers()
			.contentSecurityPolicy("default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data:")
		.and()
	     	.referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
	        .and()
	            .featurePolicy("geolocation 'none'; midi 'none'; sync-xhr 'none'; microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; speaker 'none'; fullscreen 'self'; payment 'none'")
	        .and()
	            .frameOptions()
	            .deny()
	        .and()
	            .sessionManagement()
	            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        .and()
	            .authorizeRequests()
	            .antMatchers("/api/authenticate").permitAll()
	            .antMatchers("/api/register").permitAll()
	            .antMatchers("/api/activate").permitAll()
	            .antMatchers("/api/account/reset-password/init").permitAll()
	            .antMatchers("/api/account/reset-password/finish").permitAll()
	            .antMatchers("/api/**").authenticated()
	            .antMatchers("/management/health").permitAll()
	            .antMatchers("/management/info").permitAll()
	            .antMatchers("/management/prometheus").permitAll()
	            .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
	        .and()
	            .httpBasic()
	        .and()
	            .apply(securityConfigurerAdapter());
		// @formatter:on
	}
	
	private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }
	
}
