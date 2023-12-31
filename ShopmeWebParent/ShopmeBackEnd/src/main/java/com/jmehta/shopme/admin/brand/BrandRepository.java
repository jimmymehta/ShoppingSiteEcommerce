package com.jmehta.shopme.admin.brand;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.jmehta.shopme.admin.paging.SearchRepository;
import com.jmehta.shopme.common.entity.Brand;

public interface BrandRepository extends SearchRepository<Brand, Integer> {
	
	public Long countById(Integer id);

	public Brand findByName(String name);
	
	@Query("SELECT b from Brand b WHERE b.name like %?1%")
	public Page<Brand> findAll(String keyword, Pageable pageable);
	
	@Query("SELECT NEW Brand(b.id,b.name) FROM Brand b ORDER BY b.name ASC ")
	public List<Brand> findAll();
}
