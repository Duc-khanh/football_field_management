package com.example.football_field_management.controller.users;



import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.OrderPayment;
import com.example.football_field_management.repository.AccountRepository;

import com.example.football_field_management.service.user.order.OrderPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class OrderPaymentController {

    private final OrderPaymentService orderPaymentService;
    private final AccountRepository accountRepository;

    // 🟢 Tạo đơn hàng thanh toán
    @PostMapping("/create-order")
    public ResponseEntity<?> createPayment(
            @RequestParam BigDecimal amount,
            @RequestParam(defaultValue = "CASH") String method,
            Authentication authentication) {

        String username = authentication.getName();
        Account account = accountRepository.findById(Long.valueOf(username))
                .orElseThrow(() -> new RuntimeException("Account not found"));

        OrderPayment order = orderPaymentService.createOrder(account, amount, method);
        return ResponseEntity.ok(order);
    }

    // 🟢 Cập nhật trạng thái thanh toán (VNPay/MoMo callback)
    @PostMapping("/update-status/{orderCode}")
    public ResponseEntity<?> updateStatus(
            @PathVariable String orderCode,
            @RequestParam OrderPayment.Status status) {

        OrderPayment order = orderPaymentService.updateStatus(orderCode, status);
        return ResponseEntity.ok(order);
    }

}
