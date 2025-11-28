package com.example.football_field_management.service.user.order;



import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.OrderPayment;
import com.example.football_field_management.repository.OrderPaymentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderPaymentServiceImpl implements OrderPaymentService {

    private final OrderPaymentRepository orderPaymentRepository;

    @Override
    public OrderPayment createOrder(Account account, BigDecimal amount, String paymentMethod) {
        OrderPayment order = OrderPayment.builder()
                .code("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .totalAmount(amount)
                .paymentMethod(paymentMethod)
                .status(OrderPayment.Status.PENDING)
                .account(account)
                .paidAt(null)
                .build();

        return orderPaymentRepository.save(order);
    }

    @Override
    public OrderPayment updateStatus(String orderCode, OrderPayment.Status status) {
        OrderPayment order = orderPaymentRepository.findByCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);

        if (status == OrderPayment.Status.PAID) {
            order.setPaidAt(LocalDateTime.now());
        }

        return orderPaymentRepository.save(order);
    }
}

