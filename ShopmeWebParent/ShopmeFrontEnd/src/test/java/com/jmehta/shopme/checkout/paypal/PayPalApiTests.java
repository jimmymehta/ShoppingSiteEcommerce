package com.jmehta.shopme.checkout.paypal;


import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class PayPalApiTests {
	
	private static final String BASE_URL = "https://api.sandbox.paypal.com";
	private static final String GET_ORDER_API = "/v2/checkout/orders/";
	private static final String CLIENT_ID= "AfM0g2fNKWV2Gdx7FIGUqzINOfbi95X5_GeoBkD3LhPAsBWUvekvs2KQVSaacqQTw0M-MwT6ys1C_q2B";
	private static final String CLIENT_SECRET = "EPlEh9uJ-xpmHILSgjpaEtvykvpZeL7P1Bhypn09EkpOlP3wHjLLCco7FeZjlC55w5ITHo6hnHoq82qk";
	
	
	@Test
	public void testGetOrderDetails() {
		
		String orderId = "053068137B3101308";
		
		String requestURL = BASE_URL + GET_ORDER_API + orderId;
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Accept-Language", "en_US");
		headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
		
		HttpEntity<MultiValueMap<String, String>> request = 
				new HttpEntity<>(headers);
		
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(requestURL, HttpMethod.GET, request, PayPalOrderResponse.class);
		
		PayPalOrderResponse orderResponse = response.getBody();
		
		System.out.println("Validated: " + orderResponse.validate(orderId));
		
		System.out.println("Order ID: " + orderResponse.getId());
		
		
	}

}
