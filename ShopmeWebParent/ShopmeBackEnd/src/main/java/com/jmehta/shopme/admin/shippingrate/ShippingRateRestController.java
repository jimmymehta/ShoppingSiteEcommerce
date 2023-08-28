package com.jmehta.shopme.admin.shippingrate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShippingRateRestController {

	@Autowired
	private ShippingRateService shipService;
	
	
	@PostMapping("/get_shipping_cost")
	public String getShippingCost(Integer productId, Integer countryId, String state) throws ShippingRateNotFoundException {
		
		float shippingCost = shipService.calculateShippingCost(productId, countryId, state);
		return String.valueOf(shippingCost);
	}
}
