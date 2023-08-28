package com.jmehta.shopme.admin.brand;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jmehta.shopme.common.entity.Brand;
import com.jmehta.shopme.common.entity.Category;

@RestController
public class BrandRestController {

	@Autowired
	private BrandService brandService;
	
	@PostMapping("/brands/check_unique")
	public String checkUnique(@RequestParam("id") Integer id, @RequestParam("name") String name) {
		return brandService.checkUnique(id, name);
	}
	
	@GetMapping("/brands/{id}/categories")
	public List<CategoryDTO> listCategoriesByBrand(@PathVariable(name="id")Integer brandId) throws BrandNotFoundRestException{
		
		List<CategoryDTO> listCategories = new ArrayList<>();
		
		try {
			Brand brand = brandService.get(brandId);
			
			Set<Category> categories = brand.getCategories();
			
			for(Category category: categories) {
				
				CategoryDTO dto = new CategoryDTO(category.getId(), category.getName());
				listCategories.add(dto);
			}
			
			return listCategories;
			
		} catch (BrandNotFoundException e) {
		   throw new BrandNotFoundRestException();
		}
		
		
		
	}
}
