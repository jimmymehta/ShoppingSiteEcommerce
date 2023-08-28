package com.jmehta.shopme.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jmehta.shopme.common.entity.Category;
import com.jmehta.shopme.common.exception.CategoryNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repo;
	
	public List<Category> listNoChildrenCategories(){
		
		List<Category> listNoChildrenCategories = new ArrayList<Category>();
		
		List<Category> enabledCategories = repo.findAllEnabled();
		
		enabledCategories.forEach(category -> {
			Set<Category> children = category.getChildren();
			
			if(children == null || children.size() == 0) {
				listNoChildrenCategories.add(category);
			}
		});
		
		return listNoChildrenCategories;
		
	}
	
	public Category getCategoryByAlias(String alias) throws CategoryNotFoundException {
		
		Category category = repo.findByAliasEnabled(alias);
		
		if(category == null) {
			throw new CategoryNotFoundException("Could not find any categories with alias " + alias);
		}
		
		return category;
	}
	
	public List<Category> getCategoryParents(Category child){
		
		List<Category> listParents = new ArrayList<>();
		
		Category parent = child.getParent();
		
		while(parent != null) {
			
			listParents.add(0,parent);
			
			parent = parent.getParent();
			
		}
		
		listParents.add(child);
		
		return listParents;
		
	}

}
