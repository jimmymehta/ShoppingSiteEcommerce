package com.jmehta.shopme.admin.shippingrate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.jmehta.shopme.common.entity.Country;
import com.jmehta.shopme.common.entity.ShippingRate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ShippingRateRepositoryTests {
	
	@Autowired
	private ShippingRateRepository shippingrateRepo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateNew() {
		
		Country india = new Country(106);
		ShippingRate newRate = new ShippingRate();
		newRate.setCountry(india);
		newRate.setState("Gujarat");
		newRate.setRate(6.25f);
		newRate.setDays(2);
		newRate.setCodSupported(true);
		
		ShippingRate savedRate = shippingrateRepo.save(newRate);
		assertThat(savedRate).isNotNull();
		assertThat(savedRate.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testUpdate() {
		Integer rateId = 1;
		ShippingRate rate = entityManager.find(ShippingRate.class, rateId);
		rate.setRate(9.15f);
		rate.setDays(2);
		ShippingRate updatedRate = shippingrateRepo.save(rate);
		
		assertThat(updatedRate.getRate()).isEqualTo(9.15f);
		assertThat(updatedRate.getDays()).isEqualTo(2);
	}
	
	@Test
	public void testFindAll() {
		List<ShippingRate> rates = (List<ShippingRate>) shippingrateRepo.findAll();
		assertThat(rates.size()).isGreaterThan(0);
		
		rates.forEach(System.out::println);
	}
	
	@Test
	public void testFindByCountryAndState() {
		Integer countryId = 106;
		String state = "Maharashtra";
		ShippingRate rate = shippingrateRepo.findByCountryAndState(countryId, state);
		
		assertThat(rate).isNotNull();
		System.out.println(rate);
	}
	
	@Test
	public void testUpdateCODSupport() {
		Integer rateId = 1;
		shippingrateRepo.updateCODSupport(rateId, false);
		
		ShippingRate rate = entityManager.find(ShippingRate.class, rateId);
		assertThat(rate.isCodSupported()).isFalse();
	}

	@Test
	public void testDelete() {
		Integer rateId = 2;
		shippingrateRepo.deleteById(rateId);
		
		ShippingRate rate = entityManager.find(ShippingRate.class, rateId);
		assertThat(rate).isNull();
	}
}
