package com.jmehta.shopme.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jmehta.shopme.common.entity.Country;
import com.jmehta.shopme.common.entity.State;

public interface StateRepository extends CrudRepository<State, Integer> {

	public List<State> findByCountryOrderByNameAsc(Country country);
}
