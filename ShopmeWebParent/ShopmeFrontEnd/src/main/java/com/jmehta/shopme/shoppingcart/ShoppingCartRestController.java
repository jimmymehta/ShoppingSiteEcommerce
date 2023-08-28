package com.jmehta.shopme.shoppingcart;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jmehta.shopme.Utility;
import com.jmehta.shopme.common.entity.Customer;
import com.jmehta.shopme.common.exception.CustomerNotFoundException;
import com.jmehta.shopme.customer.CustomerService;

@RestController
public class ShoppingCartRestController {

	@Autowired
	private ShoppingCartService cartService;

	@Autowired
	private CustomerService customerService;

	@PostMapping("/cart/add/{productId}/{quantity}")
	public String addProductToCart(@PathVariable(name = "productId") Integer productId,
			@PathVariable(name = "quantity") Integer quantity, HttpServletRequest request) {

		try {
			Customer customer = getAuthenticatedCustomer(request);
			
			Integer updatedQuantity = cartService.addProduct(productId, quantity, customer);
			
			return updatedQuantity+ " item(s) of this product were added to your shopping cart";
			
		} catch (CustomerNotFoundException e) {
			return "You Must login to add this product to cart.";
		} catch (ShoppingCartException e) {
			return e.getMessage();
		}
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {

		String email = Utility.getEmailOfAuthenticatedCustomer(request);

		if (email == null) {
			throw new CustomerNotFoundException("No Authenticated customer");
		}

		return customerService.getCustomerByEmail(email);

	}
	
	@PostMapping("/cart/update/{productId}/{quantity}")
	public String updateQuantity(@PathVariable(name = "productId") Integer productId,
			@PathVariable(name = "quantity") Integer quantity, HttpServletRequest request) {
		
		try {
			Customer customer = getAuthenticatedCustomer(request);
			
			float subtotal = cartService.updateQuantity(productId, quantity, customer);
			
			return String.valueOf(subtotal);
			
		} catch (CustomerNotFoundException e) {
			return "You must login to change quantity of this product.";
		} 
		
	}
	
	@DeleteMapping("/cart/remove/{productId}")
	public String removeProduct(@PathVariable(name = "productId") Integer productId, HttpServletRequest request) {
		
		Customer customer;
		try {
			customer = getAuthenticatedCustomer(request);
			cartService.deleteProduct(productId, customer);
			return "The product has been removed from your shopping cart.";
		} catch (CustomerNotFoundException e) {
			return "You must login to remove product.";
		}
		
		
	}

}
