package com.assav.payment_api.service;
import com.assav.payment_api.model.PaymentRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import java.util.UUID;
@Service
public class PaymentService {
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final String QUEUE_NAME = "payment-queue";
    // Removemos o ObjectMapper daqui dos parâmetros
    public PaymentService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
        this.objectMapper = new ObjectMapper(); // Instanciamos manualmente!
    }
    public String sendPaymentForProcessing(PaymentRequest request) {
        try {
            // 1. Pedir à AWS o endereço (URL) exato da nossa fila
            String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                    .queueName(QUEUE_NAME)
                    .build()).queueUrl();
            // 2. Transformar o objeto Java numa String JSON
            String messageBody = objectMapper.writeValueAsString(request);
            // 3. Enviar a mensagem para o SQS
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build();
            sqsClient.sendMessage(sendMsgRequest);
            // 4. Gerar e devolver um ID de rastreamento
            return UUID.randomUUID().toString();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Falha ao converter o pedido de pagamento para JSON", e);
        }
    }
}