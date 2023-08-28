package com.jmehta.shopme.admin.setting;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jmehta.shopme.admin.FileUploadUtil;
import com.jmehta.shopme.common.entity.Currency;
import com.jmehta.shopme.common.entity.setting.Setting;

@Controller
public class SettingController {
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private CurrencyRepository currencyRepo;
	
	
	@GetMapping("/settings")
	public String listAll(Model model) {
		
		
		List<Setting> listSettings = settingService.listAllSettings();
		List<Currency> listCurrencies = currencyRepo.findAllByOrderByNameAsc();
		
		
		for(Setting setting: listSettings) {
			model.addAttribute(setting.getKey(), setting.getValue());
		}
		
		model.addAttribute("listCurrencies", listCurrencies);
		
		return "settings/settings";
		
	}
	
	@PostMapping("/settings/save_general")
	public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile, HttpServletRequest request,
			RedirectAttributes ra) throws IOException {
		
		GeneralSettingBag settingBag = settingService.getGeneralSettings();
		
		saveSitelogo(multipartFile, settingBag);
		saveCurrencySymbol(request, settingBag);
		
		updateSettingValuesFromForm(request, settingBag.list());
		
		ra.addFlashAttribute("message", "General settigs have been saved.");
		
		
		return "redirect:/settings#general";
	}
	
	@PostMapping("/settings/save_mail_server")
	public String saveMailServerSettings(HttpServletRequest request,
			RedirectAttributes ra) {
		
		List<Setting> mailServerSettings = settingService.getMailServerSettings();
		
		updateSettingValuesFromForm(request, mailServerSettings);
		
		ra.addFlashAttribute("message", "Mail Server settigs have been saved.");
		
		return "redirect:/settings#mailServer";
		
	}
	
	@PostMapping("/settings/save_mail_templates")
	public String saveMailTemplateSettings(HttpServletRequest request,
			RedirectAttributes ra) {
		
		List<Setting> mailTemplateSettings = settingService.getMailTemplateSettings();
		
		updateSettingValuesFromForm(request, mailTemplateSettings);
		
		ra.addFlashAttribute("message", "Mail Template settigs have been saved.");
		
		return "redirect:/settings#mailTemplates";
		
	}
	
	@PostMapping("/settings/save_payment")
	public String savePaymentSettings(HttpServletRequest request, RedirectAttributes ra) {
		
		List<Setting> paymentSettings = settingService.getPaymentSettings();
		
		updateSettingValuesFromForm(request, paymentSettings);
		
		ra.addFlashAttribute("message", "Payment settigs have been saved.");
		
		return "redirect:/settings#payment";
	}
	
	

	private void saveSitelogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String value = "/site-logo/" + fileName;
			
			
			settingBag.updateSitLogo(value);
			
			String uploadDir = "../site-logo/";
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.uploadFile(uploadDir, fileName, multipartFile);
		
		}
	}
	
	private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag) {
		
		Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
		
		Optional<Currency> findByIdResult = currencyRepo.findById(currencyId);
		
		if(findByIdResult.isPresent()) {
			
			Currency currency = findByIdResult.get();
			
			settingBag.updateCurrencySymbol(currency.getSymbol());
			
		}
		
	}
	
	private void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> listSettings) {
		
		for(Setting setting: listSettings) {
			String value = request.getParameter(setting.getKey());
			
			if(value != null) {
				setting.setValue(value);
			}
		}
		
		settingService.saveAll(listSettings);
		
	}
	
	
	

}
