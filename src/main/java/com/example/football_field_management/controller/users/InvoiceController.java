package com.example.football_field_management.controller.users;



import com.example.football_field_management.model.Invoice;
import com.example.football_field_management.service.user.order.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/create")
    public ResponseEntity<?> createInvoice(
            @RequestParam Long bookingId,
            @RequestParam Long paymentMethodId,
            @RequestParam(defaultValue = "PAID") Invoice.Status status
    ) {
        Invoice invoice = invoiceService.createInvoice(bookingId, paymentMethodId, status);
        return ResponseEntity.ok(invoice);
    }
}
