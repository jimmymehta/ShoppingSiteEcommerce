package com.jmehta.shopme.common.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "categories")
public class Category extends IdBasedEntity {
	
	
	@Column(length = 128, nullable = false, unique = true)
	private String name;
	
	@Column(length = 64, nullable = false, unique = true)
	private String alias;
	
	@Column(length = 64, nullable = false)
	private String image;
	
	private boolean enabled;
	
	@Column(name = "all_parent_ids", length=256, nullable = true)
	private String allParentIDs;
	
	@OneToOne
	@JoinColumn(name = "parent_id")
	private Category parent;
	
	@OneToMany(mappedBy = "parent")
	@OrderBy("name asc")
	private Set<Category> children = new HashSet<>();
	
	public Category() {
		
	}
	
	public Category(Integer id) {
		this.id = id;
	}

	public Category(String name) {
		this.name = name;
		this.alias = name;
		this.image = "default.png";
	}
	
	public Category(String name, Category parent) {
		this(name);
		this.parent = parent;
	}
	
	public Category(Integer id, String name, String alias) {
		super();
		this.id = id;
		this.name = name;
		this.alias = alias;
	}

	public static Category copyIdAndName(Category category) {
		Category copyObj = new Category();
		copyObj.setId(category.getId());
		copyObj.setName(category.getName());
		return copyObj;
	}
	
	public static Category copyIdAndName(Integer id, String name) {
		Category copyObj = new Category();
		copyObj.setId(id);
		copyObj.setName(name);
		return copyObj;
	}
	
	public static Category copyFull(Category category) {
		
		Category copyObj = new Category();
		copyObj.setId(category.getId());
		copyObj.setName(category.getName());
		copyObj.setImage(category.getImage());
		copyObj.setAlias(category.getAlias());
		copyObj.setEnabled(category.isEnabled());
		copyObj.setHasChildren(category.getChildren().size() > 0);
		return copyObj;
	}
	
	public static Category copyFull(Category category, String name) {
		Category copyCategory = copyFull(category);
		copyCategory.setName(name);
		return copyCategory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public Set<Category> getChildren() {
		return children;
	}

	public void setChildren(Set<Category> children) {
		this.children = children;
	}
	
	
	public String getAllParentIDs() {
		return allParentIDs;
	}

	public void setAllParentIDs(String allParentIDs) {
		this.allParentIDs = allParentIDs;
	}

	@Transient
	public String getImagePath() {
		
		if(this.id == null) return "/images/image-thumbnail.png";
		
		return "/category-images/" + this.id + "/" + this.image;
	}
	
	@Transient
	private boolean hasChildren;

	public boolean isHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

	@Override
	public String toString() {
		return this.name;
	}
	
}
