package com.jmehta.shopme.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.jmehta.shopme.common.entity.Customer;
import com.jmehta.shopme.customer.CustomerRepository;

public class CustomerUserDetailsService implements UserDetailsService {
	
	@Autowired
	private CustomerRepository customerRepo;

	@Override
	public CustomerUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		Customer customer = customerRepo.findByEmail(email);
		
		if(customer != null) {
			return new CustomerUserDetails(customer);
		}
		
		throw new UsernameNotFoundException("Could not find user with email::: " + email);
		
	}

}
