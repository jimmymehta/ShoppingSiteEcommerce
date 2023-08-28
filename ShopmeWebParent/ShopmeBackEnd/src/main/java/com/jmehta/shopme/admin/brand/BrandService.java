package com.jmehta.shopme.admin.brand;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jmehta.shopme.admin.paging.PagingAndSortingHelper;
import com.jmehta.shopme.common.entity.Brand;

@Service
public class BrandService {
	
	public static final int BRANDS_PER_PAGE=10;

	@Autowired
	private BrandRepository brandRepo;

	public List<Brand> listAll() {

		return (List<Brand>) brandRepo.findAll();

	}

	public void listBrandsByPage(int pageNum,PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, BRANDS_PER_PAGE, brandRepo);
	}

	public Brand saveBrand(Brand brand) {
		return brandRepo.save(brand);
	}

	public Brand get(Integer id) throws BrandNotFoundException {
		try {
			return brandRepo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new BrandNotFoundException("Could not find any brand with ID " + id);
		}
	}

	public void delete(Integer id) throws BrandNotFoundException {

		Long countById = brandRepo.countById(id);
		if (countById == null || countById == 0) {
			throw new BrandNotFoundException("Could Not find any brand with ID " + id);
		}
		brandRepo.deleteById(id);
	}

	public String checkUnique(Integer id, String name) {

		boolean isCreatingNew = (id == null || id == 0);

		Brand brand = brandRepo.findByName(name);

		if (isCreatingNew) {
			if (brand != null) {
				return "DuplicateName";
			}
		} else {
			if (brand != null && brand.getId() != id) {
				return "DuplicateName";
			}
		}

		return "OK";
	}

}
