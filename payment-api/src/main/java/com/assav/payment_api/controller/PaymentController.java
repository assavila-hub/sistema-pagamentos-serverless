package com.assav.payment_api.controller;
import com.assav.payment_api.model.PaymentRequest;
import com.assav.payment_api.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    @PostMapping
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest request) {
        try {
            String transactionId = paymentService.sendPaymentForProcessing(request);
            // Retorna HTTP 202 (Accepted) com o ID da transação
            return ResponseEntity.accepted().body(Map.of(
                    "status", "Em processamento",
                    "transactionId", transactionId
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("erro", e.getMessage()));
        }
    }
}