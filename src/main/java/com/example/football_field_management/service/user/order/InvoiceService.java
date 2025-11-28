package com.example.football_field_management.service.user.order;



import com.example.football_field_management.model.*;
import com.example.football_field_management.repository.BookingRepository;
import com.example.football_field_management.repository.InvoiceRepository;

import com.example.football_field_management.repository.OrderPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepository;
    private final OrderPaymentRepository paymentMethodRepository;

    public Invoice createInvoice(Long bookingId, Long paymentMethodId, Invoice.Status status) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại!"));

        OrderPayment paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new RuntimeException("Payment Method không tồn tại!"));

        Invoice invoice = Invoice.builder()
                .booking(booking)
                .orderPayment(paymentMethod)
                .paymentDate(LocalDateTime.now())
                .status(status)
                .build();

        return invoiceRepository.save(invoice);
    }
}
