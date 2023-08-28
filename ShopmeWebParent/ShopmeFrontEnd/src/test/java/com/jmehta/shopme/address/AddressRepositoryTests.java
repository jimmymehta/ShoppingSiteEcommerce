package com.jmehta.shopme.address;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.jmehta.shopme.common.entity.Address;
import com.jmehta.shopme.common.entity.Country;
import com.jmehta.shopme.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class AddressRepositoryTests {
	
	@Autowired 
	private AddressRepository repo;
	
	
	@Test
	public void testAddNew() {
		
		
		Integer customerId = 43;
		Integer countryId = 234; // USA
		
		Address newAddress = new Address();
		newAddress.setCustomer(new Customer(customerId));
		newAddress.setCountry(new Country(countryId));
		newAddress.setFirstName("Rahul");
		newAddress.setLastName("Shah");
		newAddress.setPhoneNumber("408-323-4432");
		newAddress.setAddressLine1("Near Dallas");
		newAddress.setCity("Dallas");
		newAddress.setState("Texas");
		newAddress.setPostalCode("232323");
		
		Address savedAddress = repo.save(newAddress);
		
		assertThat(savedAddress).isNotNull();
		assertThat(savedAddress.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testFindByCustomer() {
		Integer customerId = 5;
		List<Address> listAddresses = repo.findByCustomer(new Customer(customerId));
		assertThat(listAddresses.size()).isGreaterThan(0);
		
		listAddresses.forEach(System.out::println);
	}
	
	@Test
	public void testFindByIdAndCustomer() {
		Integer addressId = 2;
		Integer customerId = 5;
		
		Address address = repo.findByIdAndCustomer(addressId, customerId);
		
		assertThat(address).isNotNull();
		System.out.println(address);
	}
	
	@Test
	public void testUpdate() {
		Integer addressId = 2;
	//	String phoneNumber = "646-232-3932";
		
		Address address = repo.findById(addressId).get();
	//	address.setPhoneNumber(phoneNumber);
		address.setDefaultForShipping(true);

		Address updatedAddress = repo.save(address);
//		assertThat(updatedAddress.getPhoneNumber()).isEqualTo(phoneNumber);
	}
	
	@Test
	public void testDeleteByIdAndCustomer() {
		Integer addressId = 1;
		Integer customerId = 5;
		
		repo.deleteByIdAndCustomer(addressId, customerId);
		
		Address address = repo.findByIdAndCustomer(addressId, customerId);
		assertThat(address).isNull();
	}
	
	@Test
	public void testSetDefault() {
		
		Integer addressId = 5;
		
		repo.setDefaultAddress(addressId);
		
		Address address = repo.findById(addressId).get();
		assertThat(address.isDefaultForShipping()).isTrue();
	}
	
	@Test
	public void testSetNonDefaultForOther() {
		
		Integer addressId = 5;
		Integer customerId = 43;
		
		repo.setNonDefaultForOthers(addressId, customerId);
	}
	
	@Test
	public void testGetDefault() {
		Integer customerId = 40;
		Address address = repo.findDefaultByCustomer(customerId);
		assertThat(address).isNotNull();
		System.out.println(address);
	}

}
