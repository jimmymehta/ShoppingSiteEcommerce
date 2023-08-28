package com.jmehta.shopme.customer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jmehta.shopme.Utility;
import com.jmehta.shopme.common.entity.Country;
import com.jmehta.shopme.common.entity.Customer;
import com.jmehta.shopme.security.CustomerUserDetails;
import com.jmehta.shopme.security.oauth.CustomerOAuth2User;

@Controller
public class CustomerController {
	
	@Autowired
	private CustomerService customerService;
	
	
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		
		List<Country> listCountries = customerService.listAllCountries();
		
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Customer Registration");
		model.addAttribute("customer", new Customer());
		
		return "register/register_form";
	}
	
	@PostMapping("/create_customer")
	public String createCustomer(Customer customer , Model model,
			HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		
		customerService.registerCustomer(customer);
		customerService.sendVerificationEmail(request,customer);
		
		model.addAttribute("pageTitle", "Registration Succeeded!");
		
		return "/register/register_success";
		
	}
	
	@GetMapping("/verify")
	public String verifyCustomer(@RequestParam("code")String verificationCode, Model model) {
		
		boolean verified = customerService.verify(verificationCode);
		
		
		return "register/" + (verified ? "verify_success" : "verify_fail");
		
	}
	
	@GetMapping("/account_details")
	public String viewAccountDetails(Model model, HttpServletRequest request) {
		
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		
		Customer customer = customerService.getCustomerByEmail(email);
		
		List<Country> listCountries = customerService.listAllCountries();
		
		model.addAttribute("customer", customer);
		model.addAttribute("listCountries", listCountries);
		
		return "customer/account_form";
	}
	
	
	
	@PostMapping("/update_account_details")
	public String updateAccountDetails(Model model, Customer customer,RedirectAttributes ra,HttpServletRequest request) {
		
		customerService.update(customer);
		ra.addFlashAttribute("message","Your account details have been updated");
		updateNameForAuthenticatedCustomer(customer,request);
		
		
		//handle redirect for addressbook
		
		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/account_details";
		
		if("address_book".equals(redirectOption)) {
			redirectURL = "redirect:/address_book";
		}else if ("cart".equals(redirectOption)) {
			redirectURL = "redirect:/cart";
		}else if ("checkout".equals(redirectOption)) {
			redirectURL = "redirect:/address_book?redirect=checkout";
		}
		
		return redirectURL;
	}
	
	private void updateNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
		
		Object principal = request.getUserPrincipal();
		
		if(principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken) {
			
			CustomerUserDetails userDetails = getCustomerUserDetailsObject(principal);
			Customer authenticatedCustomer = userDetails.getCustomer();
			authenticatedCustomer.setFirstName(customer.getFirstName());
			authenticatedCustomer.setLastName(customer.getLastName() 	);
			
			
		}else if(principal instanceof OAuth2AuthenticationToken) {
			
			OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
			CustomerOAuth2User oauth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
			String fullName = customer.getFirstName() + " " + customer.getLastName();
			oauth2User.setFullName(fullName);
		}
		
	}
	
	private CustomerUserDetails getCustomerUserDetailsObject(Object principal) {
		CustomerUserDetails userDetails = null;
		
		if(principal instanceof UsernamePasswordAuthenticationToken) {
			
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)principal;
			userDetails = (CustomerUserDetails) token.getPrincipal();
			
			
		}else if(principal instanceof RememberMeAuthenticationToken) {
			RememberMeAuthenticationToken token = (RememberMeAuthenticationToken)principal;
			userDetails = (CustomerUserDetails) token.getPrincipal();
		}
		
		return userDetails;
		
	}
	

}
