package com.jmehta.shopme.shoppingcart;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jmehta.shopme.common.entity.CartItem;
import com.jmehta.shopme.common.entity.Customer;
import com.jmehta.shopme.common.entity.product.Product;
import com.jmehta.shopme.product.ProductRepository;

@Service
@Transactional
public class ShoppingCartService {
	
	@Autowired
	private CartItemRepository cartRepo;
	
	@Autowired
	private ProductRepository productRepo;
	
	public Integer addProduct(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
		
		Integer updatedQuantity = quantity;
		
		Product product = new Product(productId);
		
		CartItem cartItem = cartRepo.findByCustomerAndProduct(customer, product);
		
		if(cartItem != null) {
			
			updatedQuantity = cartItem.getQuantity() + quantity;
			
			if(updatedQuantity > 5) {
				throw new ShoppingCartException("Could not add more " + quantity + " item(s) because there is already " + cartItem.getQuantity() +
						" item(s) in your shopping cart. Maximum allowed quantity is 5.");
			}
		}else {
			
			cartItem = new CartItem();
			cartItem.setCustomer(customer);
			cartItem.setProduct(product);
		}
		
		cartItem.setQuantity(updatedQuantity);
		
		cartRepo.save(cartItem);
		
		return updatedQuantity;
		
	}
	
	public List<CartItem> listCartItems(Customer customer){
		return cartRepo.findByCustomer(customer);
	}
	
	public float updateQuantity(Integer productId, Integer quantity, Customer customer) {
		
		Product product = productRepo.findById(productId).get();
		
		cartRepo.updateQuantity(quantity, customer.getId(), productId);
		
		return quantity * product.getDiscountPrice();
		
	}
	
	public void deleteProduct(Integer productId, Customer customer) {
		cartRepo.deleteByCustomerAndProduct(customer.getId(), productId);
	}
	
	public void deleteByCustomer(Customer customer) {
		cartRepo.deleteByCustomer(customer.getId());
	}

}