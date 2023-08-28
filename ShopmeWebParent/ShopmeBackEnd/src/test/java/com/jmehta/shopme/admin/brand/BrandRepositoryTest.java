package com.jmehta.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.jmehta.shopme.common.entity.Brand;
import com.jmehta.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTest {

	@Autowired
	private BrandRepository repo;

	@Test
	public void testCreateBran1() {
		
		Category laptops = new Category(6);

		Brand brandAcer = new Brand("Acer");

		brandAcer.getCategories().add(laptops);

		Brand savedBrand = repo.save(brandAcer);

		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);

	}

	@Test
	public void testCreateBrand2() {
		
		Category cellphones = new Category(4);
		Category tablets = new Category(7);

		Brand brandApple = new Brand("Apple");

		brandApple.getCategories().add(cellphones);
		brandApple.getCategories().add(tablets);

		Brand savedBrand = repo.save(brandApple);

		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);

	}
	
	@Test
	public void testCreateBrand3() {
		
		Brand samsung = new Brand("Samsung");
		
		samsung.getCategories().add(new Category(29)); // category memory
		samsung.getCategories().add(new Category(24)); // category internal hard drive
		
		Brand savedBrand = repo.save(samsung);
		
		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testFindAll() {
		
		Iterable<Brand> brands = repo.findAll();
		
		brands.forEach(System.out::println);
		
		assertThat(brands).isNotEmpty();
		
	}
	
	@Test
	public void testById() {
		
		Brand brand = repo.findById(1).get();
		
		assertThat(brand.getName()).isEqualTo("Acer");
		
	}
	
	@Test
	public void testUpdateName() {
		
		String newName = "Samsung Electronics";
		Brand samsung = repo.findById(3).get();
		samsung.setName(newName);
		
		Brand savedBrand = repo.save(samsung);
		assertThat(savedBrand.getName()).isEqualTo(newName);
		
		
	}
	
	@Test
	public void testDelete() {
		
		Integer id = 2;
		
		repo.deleteById(id);
		
		Optional<Brand> result = repo.findById(2);
		
		assertThat(result).isEmpty();
		
		
	}

}
