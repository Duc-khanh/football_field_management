package com.example.football_field_management.service.user.order;


import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.OrderPayment;

import java.math.BigDecimal;

public interface OrderPaymentService {

    OrderPayment createOrder(Account account, BigDecimal amount, String paymentMethod);

    OrderPayment updateStatus(String orderCode, OrderPayment.Status status);
}
