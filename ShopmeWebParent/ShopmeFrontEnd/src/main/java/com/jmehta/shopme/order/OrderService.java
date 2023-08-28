package com.jmehta.shopme.order;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.jmehta.shopme.Utility;
import com.jmehta.shopme.checkout.CheckoutInfo;
import com.jmehta.shopme.common.entity.Address;
import com.jmehta.shopme.common.entity.CartItem;
import com.jmehta.shopme.common.entity.Customer;
import com.jmehta.shopme.common.entity.order.Order;
import com.jmehta.shopme.common.entity.order.OrderDetail;
import com.jmehta.shopme.common.entity.order.OrderStatus;
import com.jmehta.shopme.common.entity.order.PaymentMethod;
import com.jmehta.shopme.common.entity.product.Product;
import com.jmehta.shopme.setting.CurrencySettingBag;
import com.jmehta.shopme.setting.EmailSettingBag;
import com.jmehta.shopme.setting.SettingService;

@Service
public class OrderService {
	
	@Autowired
	private OrderRepository repo;
	
	@Autowired
	private SettingService settingService;
	
	
	public Order createOrder(Customer customer, Address address, List<CartItem> cartItems,
			PaymentMethod paymentMethod, CheckoutInfo checkoutInfo) {
		
		Order newOrder = new Order();
		newOrder.setOrderTime(new Date());
		
		if(paymentMethod.equals(PaymentMethod.PAYPAL)) {
			newOrder.setOrderStatus(OrderStatus.PAID);
		}else {
			newOrder.setOrderStatus(OrderStatus.NEW);
		}
		
		newOrder.setCustomer(customer);
		newOrder.setProductCost(checkoutInfo.getProductCost());
		newOrder.setSubtotal(checkoutInfo.getProductTotal());
		newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
		newOrder.setTax(0.0f);
		newOrder.setTotal(checkoutInfo.getPaymentTotal());
		newOrder.setPaymentMethod(paymentMethod);
		newOrder.setDeliverDays(checkoutInfo.getDeliverDays());
		newOrder.setDeliverDate(checkoutInfo.getDeliverDate());
		
		if(address == null) {
			newOrder.copyAddressFromCustomer();
		}else {
			newOrder.copyShippingAddress(address);
		}
		
		Set<OrderDetail> orderDetails = newOrder.getOrderDetails();
		
		for(CartItem cartItem: cartItems) {
			
			Product product = cartItem.getProduct();
			
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setOrder(newOrder);
			orderDetail.setProduct(product);
			orderDetail.setQuantity(cartItem.getQuantity());
			orderDetail.setUnitPrice(product.getDiscountPrice());
			orderDetail.setProductCost(product.getCost() * cartItem.getQuantity());
			orderDetail.setShippingCost(cartItem.getShippingCost());
			orderDetail.setSubtotal(cartItem.getSubtotal());
			
			orderDetails.add(orderDetail);
			
		}
		
		
		return repo.save(newOrder);
		
	}
	
	public void sendOrderConfirmationEmail(HttpServletRequest request,Order order) throws UnsupportedEncodingException, MessagingException {
		
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		mailSender.setDefaultEncoding("utf-8");
		
		String toAddress = order.getCustomer().getEmail();
		String subject = emailSettings.getOrderConfirmationSubject();
		String content = emailSettings.getOrderConfirmationContent();
		
		subject = subject.replace("[[orderId]]", String.valueOf(order.getId()));
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		
		DateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss E, dd MMM YYYY");
		String orderTime = dateFormatter.format(order.getOrderTime());
		
		CurrencySettingBag currencySettings = settingService.getCurrencySettings();
		String totalAmount = Utility.formatCurrency(order.getTotal(), currencySettings);
		
		content = content.replace("[[name]]", order.getCustomer().getFullName());
		content = content.replace("[[orderId]]", String.valueOf(order.getId()));
		content = content.replace("[[orderTime]]", orderTime);
		content = content.replace("[[shippingAddress]]", order.getShippingAddress());
		content = content.replace("[[total]]", totalAmount);
		content = content.replace("[[paymentMethod]]", order.getPaymentMethod().toString());
		
		helper.setText(content, true);

		mailSender.send(message);

	}

}
