package com.jmehta.shopme.admin.order;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jmehta.shopme.admin.paging.PagingAndSortingHelper;
import com.jmehta.shopme.admin.paging.PagingAndSortingParam;
import com.jmehta.shopme.admin.setting.SettingService;
import com.jmehta.shopme.common.entity.Country;
import com.jmehta.shopme.common.entity.order.Order;
import com.jmehta.shopme.common.entity.setting.Setting;
import com.jmehta.shopme.common.exception.OrderNotFoundException;

@Controller
public class OrderController {
	
	private String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private SettingService settingService;
	
	
	@GetMapping("/orders")
	public String listFirstPage(Model model) {
		return defaultRedirectURL;
	}
	
	@GetMapping("/orders/page/{pageNum}")
	public String listByPage(@PagingAndSortingParam(listName = "listOrders", moduleURL = "/orders") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum,
			HttpServletRequest request) {
		
		orderService.listByPage(pageNum, helper);
		loadCurrencySetting(request);
		
		return "orders/orders";
	}
	
	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(@PathVariable(name = "id")Integer orderId, Model model,RedirectAttributes ra,
			HttpServletRequest request) {
		
		try {
			Order order = orderService.get(orderId);
			
			loadCurrencySetting(request);
			
			model.addAttribute("order", order);
			
			return "orders/order_details_modal";
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}
	
	@GetMapping("/orders/delete/{id}")
	public String deleteOrder(@PathVariable(name = "id")Integer orderId,RedirectAttributes ra) {
		
		try {

			orderService.delete(orderId);
			ra.addFlashAttribute("message", "The order ID " + orderId + " has been deleted succesfully");

		} catch (OrderNotFoundException e) {
			ra.addFlashAttribute("message", e.getMessage());

		}

		return defaultRedirectURL;
		
	}
	
	@GetMapping("/orders/edit/{id}")
	public String editOrder(@PathVariable(name="id")Integer orderId, RedirectAttributes ra, 
			Model model,HttpServletRequest request) {
		
		try {
			Order order = orderService.get(orderId);
			
			List<Country> listCountries = orderService.listAllCountries();
			
			model.addAttribute("pageTitle", "Edit Order (ID: " + orderId + ")");
			model.addAttribute("order", order);
			model.addAttribute("listCountries", listCountries);
			
			return "orders/order_form";
			
		} catch(OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}
	
	private void loadCurrencySetting(HttpServletRequest request) {
		List<Setting> currencySettings = settingService.getCurrencySettings();
		
		for (Setting setting : currencySettings) {
			request.setAttribute(setting.getKey(), setting.getValue());
		}	
	}	
	

}
