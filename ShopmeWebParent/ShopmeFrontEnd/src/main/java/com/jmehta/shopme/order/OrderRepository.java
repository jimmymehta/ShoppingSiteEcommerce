package com.jmehta.shopme.order;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jmehta.shopme.common.entity.order.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

}
