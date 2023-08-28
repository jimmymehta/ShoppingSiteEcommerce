package com.jmehta.shopme.setting;

import org.springframework.data.repository.CrudRepository;

import com.jmehta.shopme.common.entity.Currency;

public interface CurrencyRepository extends CrudRepository<Currency, Integer> {
	
	

}
