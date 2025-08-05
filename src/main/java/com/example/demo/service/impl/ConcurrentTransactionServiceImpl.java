package com.example.demo.service.impl;


import com.example.demo.dto.TransactionRequest;
import com.example.demo.entity.Account;
import com.example.demo.entity.AccountTransaction;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.AccountTransactionRepository;
import com.example.demo.service.ConcurrentTransactionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class ConcurrentTransactionServiceImpl implements ConcurrentTransactionService {

     @Autowired
     private AccountRepository accountRepository;
     @Autowired
     private AccountTransactionRepository transactionRepository;
/**
    How It Works

    Pessimistic Locking (@Lock(PESSIMISTIC_WRITE)) makes MySQL put a row-level lock until the transaction finishes.

    If two threads try to debit the same account, the second will wait until the first finishes.

    All debit + credit steps run inside one transaction (@Transactional), so if anything fails, everything rolls back.
* */

@Transactional
@Override
public void checkDebitAndCreditConcurrentRequest(TransactionRequest request) {
     Long debitAccountId=request.getDebitAccountId();
     List<Long> creditAccountIds=request.getCreditAccountIds();
     Double amount=request.getAmount();
     Account debitAccount = accountRepository.findByIdForUpdate(debitAccountId); // Pessimistic lock

   //  waitForTenSeconds(debitAccountId);

     if (debitAccount.getBalance() < amount) {
          throw new RuntimeException("Insufficient funds!");
     }

     debitAccount.setBalance(debitAccount.getBalance() - amount);
     accountRepository.save(debitAccount);
     // Log debit
     logTransaction(debitAccount.getId(), "DEBIT", amount,
             "Debited for multiple credit transaction");

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

     private static void waitForTenSeconds(Long debitAccountId) {
          System.out.println("Got lock for debit account " + debitAccountId);
          try {
               Thread.sleep(10000); // Hold the lock for 10 seconds
          } catch (InterruptedException e) {
               throw new RuntimeException(e);
          }
     }
}
