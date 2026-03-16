package com.assav.payment_api.model;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class TransactionRequest {
    private String accountId;
    private BigDecimal amount;
}