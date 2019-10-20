package com.blog.rest;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.rest.vm.LoginVM;
import com.blog.security.jwt.JWTFilter;
import com.blog.security.jwt.TokenProvider;
import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
@RequestMapping("/api")
public class UserJWTController {

	private final TokenProvider tokenProvider;

	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	public UserJWTController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
		this.authenticationManagerBuilder = authenticationManagerBuilder;
		this.tokenProvider = tokenProvider;
	}

	@PostMapping("/authenticate")
	public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginVM.getUsername(),
				loginVM.getPassword());

		Authentication auth = authenticationManagerBuilder.getObject().authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(auth);
		boolean rememberMe = (loginVM.isRememberMe() == null ? false : loginVM.isRememberMe());
		String jwt = tokenProvider.createToken(auth, rememberMe);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(JWTFilter.AUTH_HEADER, "Bearer "+ jwt);
		return new ResponseEntity<>(new JWTToken(jwt),httpHeaders,HttpStatus.OK);
	}
	

	/**
	 * Object to return as body in JWT Authentication.
	 */
	static class JWTToken {

		private String idToken;

		JWTToken(String idToken) {
			this.idToken = idToken;
		}

		@JsonProperty("id_token")
		String getIdToken() {
			return idToken;
		}

		void setIdToken(String idToken) {
			this.idToken = idToken;
		}
	}
}
