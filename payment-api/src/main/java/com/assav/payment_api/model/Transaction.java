package com.assav.payment_api.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private String accountId;   // Partition Key (PK)
    private String timestamp;   // Sort Key (SK)
    private BigDecimal amount;
    private String type;        // 'DEPOSIT' ou 'WITHDRAW'
}