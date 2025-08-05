package com.example.demo.service.impl;


import com.example.demo.dto.TransactionRequest;
import com.example.demo.entity.Account;
import com.example.demo.entity.AccountTransaction;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.AccountTransactionRepository;
import com.example.demo.service.TransactionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountTransactionRepository transactionRepository;



    @Transactional
    @Override
    public void debitAndCredit(Long debitAccountId, List<Long> creditAccountIds, Double amount) {
        // Lock the debit account row
        Account debitAccount = accountRepository.findByIdForUpdate(debitAccountId);

        if (debitAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds in debit account!");
        }

        // Debit once
        debitAccount.setBalance(debitAccount.getBalance() - amount);
        accountRepository.save(debitAccount);
        // Log debit
        logTransaction(debitAccount.getId(), "DEBIT", amount,
                "Debited for multiple credit transaction");

        // Credit multiple accounts sequentially
        double creditAmount = amount / creditAccountIds.size();
        for (Long creditId : creditAccountIds) {
            Account creditAccount = accountRepository.findByIdForUpdate(creditId);
            creditAccount.setBalance(creditAccount.getBalance() + creditAmount);
            accountRepository.save(creditAccount);

            // Log credit
            logTransaction(creditAccount.getId(), "CREDIT", creditAmount,
                    "Credited from account " + debitAccount.getId());
        }
    }

    private void logTransaction(Long accountId, String type, Double amount, String reference) {
        AccountTransaction tx = AccountTransaction.builder()
                .accountId(accountId)
                .transactionType(type)
                .amount(amount)
                .reference(reference)
                .transactionTime(LocalDateTime.now())
                .build();
        transactionRepository.save(tx);
    }
}
