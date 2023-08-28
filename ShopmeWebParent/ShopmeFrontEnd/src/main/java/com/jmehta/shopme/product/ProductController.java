package com.jmehta.shopme.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.jmehta.shopme.category.CategoryService;
import com.jmehta.shopme.common.entity.Category;
import com.jmehta.shopme.common.entity.product.Product;
import com.jmehta.shopme.common.exception.CategoryNotFoundException;
import com.jmehta.shopme.common.exception.ProductNotFoundException;

@Controller
public class ProductController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@GetMapping("/c/{category_alias}")
	public String viewCategoryFirstPage(@PathVariable("category_alias") String alias, Model model) {
		return viewCategoryByPage(alias, model, 1);
	}

	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewCategoryByPage(@PathVariable("category_alias") String alias, Model model,
			@PathVariable(name = "pageNum") int pageNum) {

		try {

			Category categoryByAlias = categoryService.getCategoryByAlias(alias);

			List<Category> listCategoryParents = categoryService.getCategoryParents(categoryByAlias);

			Page<Product> pageProducts = productService.listByCategory(pageNum, categoryByAlias.getId());
			List<Product> listProducts = pageProducts.getContent();

			long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
			long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
			if (endCount > pageProducts.getTotalElements()) {
				endCount = pageProducts.getTotalElements();
			}

			model.addAttribute("pageTitle", categoryByAlias.getName());
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("listProducts", listProducts);
			model.addAttribute("category", categoryByAlias);

			model.addAttribute("currentPage", pageNum);
			model.addAttribute("totalPages", pageProducts.getTotalPages());
			model.addAttribute("startCount", startCount);
			model.addAttribute("endCount", endCount);
			model.addAttribute("totalItems", pageProducts.getTotalElements());

			return "product/products_by_category";
		} catch (CategoryNotFoundException ex) {
			return "error/404";
		}

	}
	
	@GetMapping("/p/{product_alias}")
	public String viewProductDetails(@PathVariable("product_alias") String alias, Model model) {
		
		try {
			Product product = productService.getProduct(alias);
			
			List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());
			
			
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("product", product);
			model.addAttribute("pageTitle", product.getShortName());
			
			return "product/product_detail";
		} catch (ProductNotFoundException e) {
			return "error:404";
		}
		
	}
	
	@GetMapping("/search")
	public String searchFirstPage(@RequestParam("keyword")String keyword, Model model) {
		
		return searchByPage(keyword, model, 1);
	}
	
	@GetMapping("/search/page/{pageNum}")
	public String searchByPage(@RequestParam("keyword") String keyword, Model model,
			@PathVariable("pageNum")int pageNum) {
		
		Page<Product> pageProducts = productService.search(keyword, pageNum);
		List<Product> listResult = pageProducts.getContent();
		
		long startCount = (pageNum - 1) * ProductService.SEARCH_RESULTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.SEARCH_RESULTS_PER_PAGE - 1;
		if (endCount > pageProducts.getTotalElements()) {
			endCount = pageProducts.getTotalElements();
		}
		
		model.addAttribute("listResult", listResult);
		model.addAttribute("keyword", keyword);
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageProducts.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageProducts.getTotalElements());
		model.addAttribute("pageTitle", keyword + "- Search Result");
		
		return "product/search_result";
	}

}
