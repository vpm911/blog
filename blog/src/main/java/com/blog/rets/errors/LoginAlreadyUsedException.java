package com.blog.rets.errors;

import com.blog.rest.errors.BadRequestAlertException;
import com.blog.rest.errors.ErrorConstants;

public class LoginAlreadyUsedException extends BadRequestAlertException{

	private static final long serialVersionUID = 1L;

	public LoginAlreadyUsedException() {
		super(ErrorConstants.LOGIN_ALREADY_USED_TYPE, "Login name already used", "usermanagement","userexists");
	}

	
}
