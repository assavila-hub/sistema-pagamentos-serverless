package com.assav.payment_api;
import com.assav.payment_api.model.Account;
import com.assav.payment_api.repository.AccountRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import java.math.BigDecimal;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
class PaymentApiApplicationTests {
    static LocalStackContainer localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.0"))
            .withServices(LocalStackContainer.Service.DYNAMODB, LocalStackContainer.Service.SQS);
    @BeforeAll
    static void beforeAll() {
        localStack.start();
    }
    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("cloud.aws.endpoint.uri", () -> localStack.getEndpointOverride(LocalStackContainer.Service.SQS).toString());
        registry.add("cloud.aws.credentials.access-key", localStack::getAccessKey);
        registry.add("cloud.aws.credentials.secret-key", localStack::getSecretKey);
        registry.add("cloud.aws.region.static", localStack::getRegion);
    }
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountRepository accountRepository;
    @Test
    @WithMockUser // Fornece o Crachá JWT falso para passar na segurança durante o teste
    void deveRetornarContaComSucesso() throws Exception {
        // 1. Inserir dados no DynamoDB Falso do LocalStack
        Account contaTeste = new Account();
        contaTeste.setAccountId("99999");
        contaTeste.setBalance(new BigDecimal("1000.00"));
        accountRepository.save(contaTeste);
        // 2. Fazer requisição GET com a ferramenta MockMvc
        mockMvc.perform(get("/accounts/99999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value("99999"))
                .andExpect(jsonPath("$.balance").value(1000.00));
        System.out.println("✅ Teste Passou! A API foi ao DynamoDB Falso e trouxe os dados com sucesso.");
    }
}