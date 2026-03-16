package com.assav.payment_api.model;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class PaymentRequest {
    private String fromAccountId; // Quem paga
    private String toAccountId;   // Quem recebe
    private BigDecimal amount;    // Valor
}