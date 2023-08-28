package com.jmehta.shopme.customer;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jmehta.shopme.common.entity.Customer;
import com.jmehta.shopme.common.exception.CustomerNotFoundException;
import com.jmehta.shopme.common.exception.ResetPasswordTokenExpirationException;

@Controller
public class ForgotPasswordController {
	
	@Autowired
	private CustomerService customerService;
	
	
	@GetMapping("/forgot_password")
	public String showRequestForm() {
		
		return "customer/forgot_password_form";
		
	}
	
	@PostMapping("/forgot_password")
	public String processRequestForm(HttpServletRequest request, Model model) {
		
		String email = request.getParameter("email");
		
		try {
			
			String token = customerService.updateResetPasswordToken(email);
			
			System.out.println("Email::: " + email);
			System.out.println("Token::: " + token);
			
			customerService.sendResetPasswordEmail(request, email, token);
			model.addAttribute("message", "We have sent a reset password link to your email. Please check");
			
		} catch (CustomerNotFoundException e) {
			model.addAttribute("error", e.getMessage());
		} catch (UnsupportedEncodingException | MessagingException e) {
			model.addAttribute("error", "Could not send email");
		}
		
		return "customer/forgot_password_form";
		
	}
	
	@GetMapping("/reset_password")
	public String showResetForm(@RequestParam("token") String token, Model model) {
		
		Customer customer = customerService.getByResetPasswordToken(token);
		
		if(customer != null && customer.isTokenExpired()) {
			model.addAttribute("pageTitle", "Invalid token");
			model.addAttribute("message", "Token has expired");
			return "message";
		}
		
		if(customer != null) {
			model.addAttribute("token", token);
		}else {
			model.addAttribute("pageTitle", "Invalid token");
			model.addAttribute("message", "Invalid Token");
			return "message";
		}
		
		return "customer/reset_password_form";
	}
	
	@PostMapping("/reset_password")
	public String processResetForm(HttpServletRequest request, Model model) {
		
		String token = request.getParameter("token");
		String password = request.getParameter("password");
		
		try {
			customerService.updatePassword(token, password);
			model.addAttribute("title","Reset Your Password");
			model.addAttribute("message","You have successfully changed password");
			return "message";
		} catch (CustomerNotFoundException e) {
			model.addAttribute("pageTitle", "Invalid token");
			model.addAttribute("message",e.getMessage());
			return "message";
		} catch(ResetPasswordTokenExpirationException e) {
			model.addAttribute("pageTitle", "Invalid token");
			model.addAttribute("message",e.getMessage());
			return "message";
		}
 		
	}

}
