package com.assav.payment_api.repository;
import com.assav.payment_api.model.Transaction;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import java.util.HashMap;
import java.util.Map;
@Repository
public class TransactionRepository {
    private final DynamoDbClient dynamoDbClient;
    private final String TABLE_NAME = "transactions";
    public TransactionRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }
    public void save(Transaction transaction) {
        Map<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("accountId", AttributeValue.builder().s(transaction.getAccountId()).build());
        itemValues.put("timestamp", AttributeValue.builder().s(transaction.getTimestamp()).build());
        itemValues.put("amount", AttributeValue.builder().n(transaction.getAmount().toString()).build());
        itemValues.put("type", AttributeValue.builder().s(transaction.getType()).build());
        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(itemValues)
                .build();
        dynamoDbClient.putItem(request);
    }
}