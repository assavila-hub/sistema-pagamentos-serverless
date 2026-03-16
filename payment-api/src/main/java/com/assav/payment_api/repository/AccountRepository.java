package com.assav.payment_api.repository;
import com.assav.payment_api.model.Account;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@Repository
public class AccountRepository {
    private final DynamoDbClient dynamoDbClient;
    private final String TABLE_NAME = "accounts";
    public AccountRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }
    public void save(Account account) {
        Map<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("accountId", AttributeValue.builder().s(account.getAccountId()).build());
        itemValues.put("balance", AttributeValue.builder().n(account.getBalance().toString()).build());
        itemValues.put("createdAt", AttributeValue.builder().s(account.getCreatedAt()).build());
        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(itemValues)
                .build();
        dynamoDbClient.putItem(request);
    }
    public Optional<Account> findById(String accountId) {
        Map<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("accountId", AttributeValue.builder().s(accountId).build());
        GetItemRequest request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(keyToGet)
                .build();
        Map<String, AttributeValue> returnedItem = dynamoDbClient.getItem(request).item();
        if (returnedItem != null && !returnedItem.isEmpty()) {
            Account account = Account.builder()
                    .accountId(returnedItem.get("accountId").s())
                    .balance(new BigDecimal(returnedItem.get("balance").n()))
                    .createdAt(returnedItem.get("createdAt").s())
                    .build();
            return Optional.of(account);
        }
        return Optional.empty();
    }
}