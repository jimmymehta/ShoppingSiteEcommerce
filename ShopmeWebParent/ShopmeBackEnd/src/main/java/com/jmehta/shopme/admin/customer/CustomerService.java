package com.jmehta.shopme.admin.customer;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jmehta.shopme.admin.paging.PagingAndSortingHelper;
import com.jmehta.shopme.admin.setting.country.CountryRepository;
import com.jmehta.shopme.common.entity.Country;
import com.jmehta.shopme.common.entity.Customer;
import com.jmehta.shopme.common.exception.CustomerNotFoundException;

@Service
@Transactional
public class CustomerService {

	public static final int CUSTOMERS_PER_PAGE = 10;

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private CountryRepository countryRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<Customer> findAll() {
		return (List<Customer>) customerRepo.findAll();
	}

	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, CUSTOMERS_PER_PAGE, customerRepo);
	}

	public void save(Customer customerInForm) {

		Customer customerInDB = customerRepo.findById(customerInForm.getId()).get();

		if (!customerInForm.getPassword().isEmpty()) {
			String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
			customerInForm.setPassword(encodedPassword);
		} else {
			customerInForm.setPassword(customerInDB.getPassword());
		}
		
		customerInForm.setEnabled(customerInDB.isEnabled());
		customerInForm.setCreatedTime(customerInDB.getCreatedTime());
		customerInForm.setVerificationCode(customerInDB.getVerificationCode());
		customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
		customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());
		customerInForm.setResetPasswordTokenCreationTime(customerInDB.getResetPasswordTokenCreationTime());
		customerRepo.save(customerInForm);
	}

	public Customer get(Integer id) throws CustomerNotFoundException {
		try {
			return customerRepo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new CustomerNotFoundException("Could not find any customer with ID " + id);
		}
	}

	public void delete(Integer id) throws CustomerNotFoundException {

		Long countById = customerRepo.countById(id);
		if (countById == null || countById == 0) {
			throw new CustomerNotFoundException("Could Not find any customer with ID " + id);
		}
		customerRepo.deleteById(id);
	}

	public boolean isEmailUnique(Integer id, String email) {

		Customer customerByEmail = customerRepo.getCustomerByEmail(email);

		if (customerByEmail != null && customerByEmail.getId() != id) {
			// found another customer having the same email
			return false;
		}

		return true;
	}

	public void updateStatus(Integer id, boolean enabled) {
		customerRepo.updateStatus(id, enabled);
	}

	public List<Country> listAllCountries() {
		return countryRepo.findAllByOrderByNameAsc();
	}

}
