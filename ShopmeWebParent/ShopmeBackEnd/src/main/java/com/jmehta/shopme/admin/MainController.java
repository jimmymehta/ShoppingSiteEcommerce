package com.jmehta.shopme.admin;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	
	@GetMapping("")
	public String viewHomePage() {
		return "index";
	}
	

	@GetMapping("/login")
	public String viewLoginPage() {
		
		Authentication authentification = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentification == null || authentification instanceof AnonymousAuthenticationToken)
			return "login";
		
		return "redirect:/";
		
	}

}