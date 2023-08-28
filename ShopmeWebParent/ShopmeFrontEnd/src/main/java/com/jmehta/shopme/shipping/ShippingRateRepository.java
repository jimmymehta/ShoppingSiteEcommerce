package com.jmehta.shopme.shipping;

import org.springframework.data.repository.CrudRepository;

import com.jmehta.shopme.common.entity.Country;
import com.jmehta.shopme.common.entity.ShippingRate;

public interface ShippingRateRepository extends CrudRepository<ShippingRate, Integer> {
	
	
	public ShippingRate findByCountryAndState(Country country, String state);
	

}
