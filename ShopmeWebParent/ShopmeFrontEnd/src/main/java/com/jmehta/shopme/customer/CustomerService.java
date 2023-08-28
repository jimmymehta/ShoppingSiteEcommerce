package com.jmehta.shopme.customer;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jmehta.shopme.Utility;
import com.jmehta.shopme.common.entity.AuthenticationType;
import com.jmehta.shopme.common.entity.Country;
import com.jmehta.shopme.common.entity.Customer;
import com.jmehta.shopme.common.exception.CustomerNotFoundException;
import com.jmehta.shopme.common.exception.ResetPasswordTokenExpirationException;
import com.jmehta.shopme.setting.CountryRepository;
import com.jmehta.shopme.setting.EmailSettingBag;
import com.jmehta.shopme.setting.SettingService;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private CountryRepository countryRepo;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private SettingService settingService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<Country> listAllCountries() {
		return countryRepo.findAllByOrderByNameAsc();
	}

	public boolean isEmailUnique(String email) {

		Customer customer = customerRepo.findByEmail(email);

		return customer == null;
	}

	public boolean verify(String code) {

		Customer customer = customerRepo.findByVerificationCode(code);

		if (customer == null || customer.isEnabled()) {
			return false;
		} else {
			customerRepo.enable(customer.getId());
			return true;
		}
	}

	public void registerCustomer(Customer customer) {

		encodePassword(customer);
		customer.setEnabled(false);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(AuthenticationType.DATABASE);

		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);

		customerRepo.save(customer);

	}

	public void sendVerificationEmail(HttpServletRequest req, Customer customer)
			throws UnsupportedEncodingException, MessagingException {

		EmailSettingBag emailSettings = settingService.getEmailSettings();

		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

		String toAddress = customer.getEmail();
		String subject = emailSettings.getCustomerVerifySubject();
		String content = emailSettings.getCustomerVerifyContent();

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);

		content = content.replace("[[name]]", customer.getFullName());

		String verifyURL = Utility.getSiteURL(req) + "/verify?code=" + customer.getVerificationCode();

		content = content.replace("[[url]]", verifyURL);

		helper.setText(content, true);

		mailSender.send(message);

		System.out.println("to address:: " + toAddress);
		System.out.println("Verify URL:: " + verifyURL);

	}
	
	public void sendResetPasswordEmail(HttpServletRequest req, String email, String token) throws UnsupportedEncodingException, MessagingException {
		
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		
		String toAddress = email;
		String subject = emailSettings.getResetPasswordSubject();
		String content = emailSettings.getResetPasswordContent();
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		String resetPasswordURL = Utility.getSiteURL(req) + "/reset_password?token=" + token;
		
		content = content.replace("[[url]]", resetPasswordURL);
		
		helper.setText(content, true);

		mailSender.send(message);

		System.out.println("to address:: " + toAddress);
		System.out.println("Verify URL:: " + resetPasswordURL);
	}

	public void update(Customer customerInForm) {

		Customer customerInDB = customerRepo.findById(customerInForm.getId()).get();

		if (customerInDB.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
			if (!customerInForm.getPassword().isEmpty()) {
				String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
				customerInForm.setPassword(encodedPassword);
			} else {
				customerInForm.setPassword(customerInDB.getPassword());
			}
		}else {
			customerInForm.setPassword(customerInDB.getPassword());
		}

		customerInForm.setEnabled(customerInDB.isEnabled());
		customerInForm.setCreatedTime(customerInDB.getCreatedTime());
		customerInForm.setVerificationCode(customerInDB.getVerificationCode());
		customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
		customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());
		customerInForm.setResetPasswordTokenCreationTime(customerInDB.getResetPasswordTokenCreationTime());
		
		customerRepo.save(customerInForm);
	}

	public void encodePassword(Customer customer) {

		String encodedPasssword = encoder.encode(customer.getPassword());
		customer.setPassword(encodedPasssword);
	}

	public void updateAuthenticationType(Customer customer, AuthenticationType type) {
		if (!customer.getAuthenticationType().equals(type)) {
			customerRepo.updateAuthenticationType(customer.getId(), type);
		}

	}

	public Customer getCustomerByEmail(String email) {
		return customerRepo.findByEmail(email);
	}

	public void addNewCustomerUponOAuthLogin(String name, String email, AuthenticationType type, String countryCode) {

		Customer customer = new Customer();
		customer.setEmail(email);

		setName(name, customer);

		customer.setEnabled(true);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(type);
		customer.setPassword("");
		customer.setAddressLine1("");
		customer.setAddressLine2("");
		customer.setCity("");
		customer.setState("");
		customer.setPhoneNumber("");
		customer.setPostalCode("");
		customer.setCountry(countryRepo.findByCode(countryCode));

		customerRepo.save(customer);
	}

	private void setName(String name, Customer customer) {

		String[] nameArr = name.split(" ");

		if (nameArr.length < 2) {
			customer.setFirstName(name);
			customer.setLastName("");
		} else {

			String firstName = nameArr[0];

			customer.setFirstName(firstName);

			String lastName = name.replaceFirst(firstName + " ", "");

			customer.setLastName(lastName);

		}
	}
	
	public String updateResetPasswordToken(String email) throws CustomerNotFoundException {
		
		Customer customer = customerRepo.findByEmail(email);
		
		if(customer != null) {
			
			String token = RandomString.make(30);
			customer.setResetPasswordToken(token);
			customer.setResetPasswordTokenCreationTime(new Date());
			customerRepo.save(customer);
			
			return token;
		}else {
			throw new CustomerNotFoundException("Could Not find any customer with the email " + email);
		}
		
	}
	
	public Customer getByResetPasswordToken(String token) {
		return customerRepo.findByResetPasswordToken(token);
	}
	
	public void updatePassword(String token, String newPassword) throws CustomerNotFoundException, ResetPasswordTokenExpirationException {
		
		Customer customer = customerRepo.findByResetPasswordToken(token);
		
		if(customer == null) {
			throw new CustomerNotFoundException("No customer found, Invalid token");
		}
		
		if(customer != null && customer.isTokenExpired()) {
			throw new ResetPasswordTokenExpirationException("Sorry Token has expired, Please reset again");
		}
		
		customer.setPassword(newPassword);
		customer.setResetPasswordToken(null);
		customer.setResetPasswordTokenCreationTime(null);
		encodePassword(customer);
		customerRepo.save(customer);
	}

}
