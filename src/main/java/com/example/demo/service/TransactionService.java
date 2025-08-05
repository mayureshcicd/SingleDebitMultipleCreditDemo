package com.example.demo.service;


import java.util.List;

public interface TransactionService {
/**
    How It Works

    Pessimistic Locking (@Lock(PESSIMISTIC_WRITE)) makes MySQL put a row-level lock until the transaction finishes.

    If two threads try to debit the same account, the second will wait until the first finishes.

    All debit + credit steps run inside one transaction (@Transactional), so if anything fails, everything rolls back.
* */
      void debitAndCredit(Long debitAccountId, List<Long> creditAccountIds, Double amount);

}
