package com.jmehta.shopme.admin.customer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jmehta.shopme.admin.paging.PagingAndSortingHelper;
import com.jmehta.shopme.admin.paging.PagingAndSortingParam;
import com.jmehta.shopme.common.entity.Country;
import com.jmehta.shopme.common.entity.Customer;
import com.jmehta.shopme.common.exception.CustomerNotFoundException;

@Controller
public class CustomerController {
	
	@Autowired
	private CustomerService customerService;
	
	
	@GetMapping("/customers")
	public String listFirstPage(Model model) {
		return "redirect:/customers/page/1?sortField=firstName&sortDir=asc";
	}
	
	@GetMapping("/customers/page/{pageNum}")
	public String listByPage(@PagingAndSortingParam(listName = "listCustomers", moduleURL = "/customers") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum) {
		
		customerService.listByPage(pageNum,helper);
		return "customers/customers";	
	}
	
	@PostMapping("/customers/save")
	public String saveCustomer(Customer customer, Model model,RedirectAttributes redirectAttributes) {
		
		customerService.save(customer);
		redirectAttributes.addFlashAttribute("message", "The Customer has been saved successfully");
		return "redirect:/customers";
		
	}
	
	@GetMapping("/customers/edit/{id}")
	public String editCustomer(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {

			Customer customer = customerService.get(id);
			List<Country> countries = customerService.listAllCountries();
			
			model.addAttribute("customer", customer);
			model.addAttribute("listCountries", countries);
			model.addAttribute("pageTitle", "Edit Custoemr (ID: " + id + ")");

			return "customers/customer_form";
			
		} catch (CustomerNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/customers";
		}

	}
	
	@GetMapping("/customers/delete/{id}")
	public String deleteBrand(@PathVariable(name = "id") Integer id,
			RedirectAttributes redirectAttributes) {

		try {

			customerService.delete(id);
			redirectAttributes.addFlashAttribute("message", "The customer ID " + id + " has been deleted succesfully");

		} catch (CustomerNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());

		}

		return "redirect:/customers";

	}
	
	@GetMapping("/customers/{id}/enabled/{status}")
	public String updateCustomerStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
			RedirectAttributes redirectAttributes) {

		customerService.updateStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The customer ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/customers";
	}
	
	@GetMapping("/customers/detail/{id}")
	public String viewCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Customer customer = customerService.get(id);
			model.addAttribute("customer", customer);
			
			return "customers/customer_detail_modal";
		} catch (CustomerNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return "redirect:/customers";			
		}
	}
	
	

}
