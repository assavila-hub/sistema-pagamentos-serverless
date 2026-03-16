package com.assav.payment_api.service;
import com.assav.payment_api.model.Account;
import com.assav.payment_api.model.Transaction;
import com.assav.payment_api.repository.AccountRepository;
import com.assav.payment_api.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }
    public Account createAccount(Account account) {
        if (account.getBalance() == null || account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O saldo inicial não pode ser negativo e é obrigatório.");
        }
        if (account.getCreatedAt() == null || account.getCreatedAt().isEmpty()) {
            account.setCreatedAt(Instant.now().toString());
        }
        accountRepository.save(account);
        return account;
    }
    public Account getAccount(String accountId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (accountOptional.isEmpty()) {
            throw new IllegalArgumentException("Conta não encontrada para o ID: " + accountId);
        }
        return accountOptional.get();
    }
    public Account deposit(String accountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do depósito deve ser maior que zero.");
        }
        Account account = getAccount(accountId);
        account.setBalance(account.getBalance().add(amount));
        // Atualiza a conta
        accountRepository.save(account);
        // Registra o histórico
        Transaction transaction = Transaction.builder()
                .accountId(accountId)
                .timestamp(Instant.now().toString())
                .amount(amount)
                .type("DEPOSIT")
                .build();
        transactionRepository.save(transaction);
        return account;
    }
    public Account withdraw(String accountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do saque deve ser maior que zero.");
        }
        Account account = getAccount(accountId);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar o saque.");
        }
        account.setBalance(account.getBalance().subtract(amount));
        // Atualiza a conta
        accountRepository.save(account);
        // Registra o histórico
        Transaction transaction = Transaction.builder()
                .accountId(accountId)
                .timestamp(Instant.now().toString())
                .amount(amount)
                .type("WITHDRAW")
                .build();
        transactionRepository.save(transaction);
        return account;
    }
}