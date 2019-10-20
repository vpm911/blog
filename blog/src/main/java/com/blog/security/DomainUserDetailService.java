package com.blog.security;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.blog.model.User;
import com.blog.repository.UserRepository;

/**
 * Authenticate a user from the database
 * @author vishal.maradkar
 *
 */
@Component("userDetailsService")
public class DomainUserDetailService implements UserDetailsService{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private final UserRepository userRepository;
	
	@Autowired
	public DomainUserDetailService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		log.debug("Authenticating {}",login);
		
		if(new EmailValidator().isValid(login, null)) {
			return userRepository.findOneWithAuthoritiesByEmail(login)
					.map(user -> createSpringUser(login, user))
					.orElseThrow(() -> new UsernameNotFoundException("User with email " + login + "was not found in database"));
		}
		
		String lowercaselogin = login.toLowerCase();
		return userRepository.findOneWithAuthoritiesByLogin(lowercaselogin)
				.map(user -> createSpringUser(lowercaselogin, user))
				.orElseThrow(()-> new UsernameNotFoundException("User " + lowercaselogin + "was not found in database"));
	}

	private org.springframework.security.core.userdetails.User createSpringUser(String lowercaseLogin, User user){
		List<GrantedAuthority> authorities = user.getAuthorities().stream()
				.map(authority -> new SimpleGrantedAuthority(authority.getName()))
				.collect(Collectors.toList());
		
		return new org.springframework.security.core.userdetails.User(user.getLogin(),
	            user.getPassword(),authorities);
	}
	
}
