package com.jmehta.shopme.admin.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.jmehta.shopme.admin.paging.SearchRepository;
import com.jmehta.shopme.common.entity.Customer;

public interface CustomerRepository extends SearchRepository<Customer, Integer> {
	
	public Long countById(Integer id);
	
	@Query("SELECT c FROM Customer c WHERE CONCAT(c.email, ' ', c.firstName, ' ', c.lastName, ' ', "
			+ "c.addressLine1, ' ', c.addressLine2, ' ', c.city, ' ', c.state, "
			+ "' ', c.postalCode, ' ', c.country.name) LIKE %?1%")
	public Page<Customer> findAll(String keyword, Pageable pageable);
	
	@Query("SELECT c FROM Customer c WHERE c.email = ?1")
	public Customer getCustomerByEmail(String email);
	
	@Query("UPDATE Customer c SET c.enabled = ?2 WHERE c.id = ?1")
	@Modifying
	public void updateStatus(Integer id, boolean enabled);

}
