package com.jmehta.shopme.admin.user.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jmehta.shopme.admin.FileUploadUtil;
import com.jmehta.shopme.admin.paging.PagingAndSortingHelper;
import com.jmehta.shopme.admin.paging.PagingAndSortingParam;
import com.jmehta.shopme.admin.user.UserNotFoundException;
import com.jmehta.shopme.admin.user.UserService;
import com.jmehta.shopme.admin.user.export.UserCsvExporter;
import com.jmehta.shopme.admin.user.export.UserExcelExporter;
import com.jmehta.shopme.admin.user.export.UserPdfExporter;
import com.jmehta.shopme.common.entity.Role;
import com.jmehta.shopme.common.entity.User;

@Controller
public class UserController {

	@Autowired
	private UserService userSrvc;

	@GetMapping("/users")
	public String listFirstPage() {
		return "redirect:/users/page/1?sortField=firstName&sortDir=asc";
	}
	
	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PagingAndSortingParam(listName = "listUsers", moduleURL = "/users") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum")int pageNum) {
		
		userSrvc.listUsersByPage(pageNum,helper);
		return "users/users";
	}

	@GetMapping("/users/new")
	public String newUser(Model model) {

		List<Role> listRoles = userSrvc.listAllRoles();

		User user = new User();
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create New User");
		return "users/user_form";
	}

	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {

		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

			user.setPhotos(fileName);

			User savedUser = userSrvc.save(user);

			String uploadDir = "user-photos/" + savedUser.getId();

			FileUploadUtil.cleanDir(uploadDir);

			FileUploadUtil.uploadFile(uploadDir, fileName, multipartFile);
		} else {
			if (user.getPhotos().isEmpty())
				user.setPhotos(null);
			userSrvc.save(user);
		}

		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");
		
		String firstPartOfEmail = user.getEmail().split("@")[0];

		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" +firstPartOfEmail;
	}

	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {

			User user = userSrvc.get(id);
			List<Role> listRoles = userSrvc.listAllRoles();

			model.addAttribute("user", user);
			model.addAttribute("listRoles", listRoles);
			model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
			return "users/user_form";
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/users";
		}

	}

	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {

		try {

			userSrvc.delete(id);
			redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been deleted succesfully");

		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());

		}

		return "redirect:/users";

	}

	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
			RedirectAttributes redirectAttributes) {

		userSrvc.updateUserEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The user ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/users";
	}
	
	@GetMapping("/users/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		
		List<User> listOfUsers = userSrvc.listAll();
		
		UserCsvExporter exporter = new UserCsvExporter();
		
		exporter.export(listOfUsers, response);
		
	}
	
	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		
		List<User> listOfUsers = userSrvc.listAll();
		
		UserExcelExporter exporter = new UserExcelExporter();
		
		exporter.export(listOfUsers, response);
		
	}
	
	@GetMapping("/users/export/pdf")
	public void exportToPDF(HttpServletResponse response) throws IOException {
		
		List<User> listOfUsers = userSrvc.listAll();
		
		UserPdfExporter exporter = new UserPdfExporter();
		
		exporter.export(listOfUsers, response);
		
	}
}
