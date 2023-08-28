package com.jmehta.shopme.checkout;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.jmehta.shopme.Utility;
import com.jmehta.shopme.address.AddressService;
import com.jmehta.shopme.checkout.paypal.PayPalService;
import com.jmehta.shopme.checkout.paypal.PaypalApiException;
import com.jmehta.shopme.common.entity.Address;
import com.jmehta.shopme.common.entity.CartItem;
import com.jmehta.shopme.common.entity.Customer;
import com.jmehta.shopme.common.entity.ShippingRate;
import com.jmehta.shopme.common.entity.order.Order;
import com.jmehta.shopme.common.entity.order.PaymentMethod;
import com.jmehta.shopme.customer.CustomerService;
import com.jmehta.shopme.order.OrderService;
import com.jmehta.shopme.setting.PaymentSettingBag;
import com.jmehta.shopme.setting.SettingService;
import com.jmehta.shopme.shipping.ShippingRateService;
import com.jmehta.shopme.shoppingcart.ShoppingCartService;

@Controller
public class CheckoutController {

	@Autowired
	private CheckoutService checkoutService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private ShippingRateService shippingService;

	@Autowired
	private ShoppingCartService cartService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private PayPalService paypalService;
	

	@GetMapping("/checkout")
	public String showCheckoutPage(Model model, HttpServletRequest request) {

		Customer customer = getAuthenticatedCustomer(request);

		Address defaultAddress = addressService.getDefaultAddress(customer);
		ShippingRate shippingRate = null;
		
		if(defaultAddress != null) {
			model.addAttribute("shippingAddress", defaultAddress.toString());
			shippingRate = shippingService.getShippingRateForAddress(defaultAddress);
		}else {
			model.addAttribute("shippingAddress", customer.toString());
			shippingRate = shippingService.getShippingRateForCustomer(customer);
		}
		
		if(shippingRate == null) {
			return "redirect:/cart";
		}
		
		List<CartItem> cartItems = cartService.listCartItems(customer);
		
		CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);
		
		String currencyCode = settingService.getCurrencyCode();
		PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
		String paypalClientId = paymentSettings.getClientID();
		
		model.addAttribute("paypalClientId", paypalClientId);
		model.addAttribute("customer", customer);
		model.addAttribute("currencyCode", currencyCode);
		model.addAttribute("checkoutInfo", checkoutInfo);
		model.addAttribute("cartItems", cartItems);

		return "checkout/checkout";

	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) {

		String email = Utility.getEmailOfAuthenticatedCustomer(request);

		return customerService.getCustomerByEmail(email);

	}
	
	@PostMapping("/place_order")
	public String placeOrder(HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		
		String paymentType = request.getParameter("paymentMethod");
		
		PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);
		
		Customer customer = getAuthenticatedCustomer(request);

		Address defaultAddress = addressService.getDefaultAddress(customer);
		ShippingRate shippingRate = null;
		
		if(defaultAddress != null) {
			shippingRate = shippingService.getShippingRateForAddress(defaultAddress);
		}else {
			shippingRate = shippingService.getShippingRateForCustomer(customer);
		}
		
		
		List<CartItem> cartItems = cartService.listCartItems(customer);
		
		CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);
		
		Order createdOrder = orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkoutInfo);
		
		cartService.deleteByCustomer(customer);
		
		//send an email to customer
		orderService.sendOrderConfirmationEmail(request, createdOrder);
		
		return "checkout/order_completed";
	}
	
	@PostMapping("/process_paypal_order")
	public String processPaypalOrder(HttpServletRequest request, Model model) throws UnsupportedEncodingException, MessagingException {
		
		
		String orderId = request.getParameter("orderId");
		
		String pageTitle = "Checkout failure";
		String message = null;
		
		try {
			
			if(paypalService.validateOrder(orderId)) {
				return placeOrder(request);
			}else {
				message = "ERROR: Transaction could not be completed because order information is invalid";
			}
		}catch (PaypalApiException e) {
			message = "ERROR: Transaction failed due to error: " + e.getMessage();
		}
		
		model.addAttribute("title", pageTitle);
		model.addAttribute("message", message);
		
		return "message";
	
	}

}
