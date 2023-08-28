package com.jmehta.shopme.checkout;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jmehta.shopme.common.entity.CartItem;
import com.jmehta.shopme.common.entity.ShippingRate;
import com.jmehta.shopme.common.entity.product.Product;

@Service
public class CheckoutService {
	
	private static final int DIM_DIVISOR = 139;
	
	
	public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {
		
		CheckoutInfo checkoutInfo = new CheckoutInfo();
		
		float productCost = calculateProductCost(cartItems);
		float productTotal = calculateProductTotal(cartItems);
		float shippingCostTotal = calculateShippingCost(cartItems, shippingRate);
		float paymentTotal = productTotal + shippingCostTotal;
		
		checkoutInfo.setProductCost(productCost);
		checkoutInfo.setProductTotal(productTotal);
		checkoutInfo.setDeliverDays(shippingRate.getDays());
		checkoutInfo.setPaymentTotal(paymentTotal);
		checkoutInfo.setCodSupported(shippingRate.isCodSupported());
		checkoutInfo.setShippingCostTotal(shippingCostTotal);
		
		
		return checkoutInfo;
		
	}
	
	private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
		
		float totalShippingCost = 0.0f;
		
		for(CartItem cartItem: cartItems) {
			
			Product product = cartItem.getProduct();
			
			float dimWeight = (product.getLength() * product.getHeight() * product.getWidth()) / DIM_DIVISOR;
			
			float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;
			
			float shippingCost = finalWeight * cartItem.getQuantity() * shippingRate.getRate();
			
			cartItem.setShippingCost(shippingCost);
			
			totalShippingCost += shippingCost;
			
		}
		
		
		return totalShippingCost;
		
		
	}
	
	
	private float calculateProductTotal(List<CartItem> cartItems) {
		
		float total = 0.0f;
		
		for(CartItem item: cartItems) {
			total+=item.getSubtotal();
		}
		
		return total;
		
	}

	private float calculateProductCost(List<CartItem> cartItems) {
		
		float cost = 0.0f;
		
		for(CartItem cartItem : cartItems) {
			
			cost += cartItem.getQuantity() * cartItem.getProduct().getCost();
			
		}
		
		return cost;
	}

}
