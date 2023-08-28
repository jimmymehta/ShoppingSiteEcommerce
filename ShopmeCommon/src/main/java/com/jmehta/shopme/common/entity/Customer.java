package com.jmehta.shopme.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer extends AbstractAddressWithCountry {
	
	private static final long RESET_PASSWORD_TOKEN_EXPIRATION_TIME = 10L * 60L * 1000L; //10mins

	
	@Column(nullable = false, unique=true, length = 45)
	private String email;
	
	@Column(nullable = false,length = 64)
	private String password;
	
	
	@Column(name = "created_time")
	private Date createdTime;
	
	
	private boolean enabled;
	
	@Column(name = "verification_code",length = 64)
	private String verificationCode;
	
	
	@Enumerated(EnumType.STRING)
	@Column(name = "authentication_type", length = 10)
	private AuthenticationType authenticationType;
	
	@Column(name = "reset_password_token", length = 30)
	private String resetPasswordToken;
	
	private Date resetPasswordTokenCreationTime;
	
	public Customer() {
		
	}
	
	public Customer(Integer id) {
		super();
		this.id = id;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}
	
	public String getFullName() {
		return firstName + " " + lastName;
	}

	public AuthenticationType getAuthenticationType() {
		return authenticationType;
	}

	public void setAuthenticationType(AuthenticationType authenticationType) {
		this.authenticationType = authenticationType;
	}

	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

	public Date getResetPasswordTokenCreationTime() {
		return resetPasswordTokenCreationTime;
	}

	public void setResetPasswordTokenCreationTime(Date resetPasswordTokenCreationTime) {
		this.resetPasswordTokenCreationTime = resetPasswordTokenCreationTime;
	}
	
	public boolean isTokenExpired() {
		
		if(this.resetPasswordTokenCreationTime == null) return false;
		
		long currentTime = System.currentTimeMillis();
		long creationTime = this.resetPasswordTokenCreationTime.getTime();
		
		return currentTime > creationTime + RESET_PASSWORD_TOKEN_EXPIRATION_TIME;
		
	}
	
}
