# Spring Boot - Single Debit Multiple Credit Transaction (MySQL + Pessimistic Locking)

## 📌 Overview
This project demonstrates a **Single Debit → Multiple Credit** transaction in **Spring Boot** with **MySQL**.  
It ensures that:
- The first transaction **completes before the next one starts** for the same account.
- All debit & credit steps happen in **one atomic transaction**.
- **Pessimistic Locking** prevents concurrent updates to the same account.

---

## ⚙️ Tech Stack
- **Java 17+**
- **Spring Boot 3+**
- **Spring Data JPA**
- **MySQL**
- **Lombok** (optional)

---

 
 🔒 How It Works
Pessimistic Locking (@Lock(PESSIMISTIC_WRITE))

    MySQL places a row-level lock on the debit account until the transaction finishes.

    Prevents other threads from modifying the same account.

Transactional Guarantee (@Transactional)

    All debit & credit operations happen inside one transaction.

    If any step fails, the entire transaction rolls back.

Concurrency Safety

    If two threads try to debit the same account, the second waits until the first finishes.

✅ Example Transaction Flow

    Debit account 1 (Customer A) by 100

    Credit account 2 (Customer B) and account 3 (Customer C) with 50 each

    All done atomically & safely.

✅ sample request
 
POST http://localhost:8080/transactions/debit-credit
?debitAccountId=1&creditAccountIds=2,3&amount=100

 
[![Swagger Logo](https://upload.wikimedia.org/wikipedia/commons/a/ab/Swagger-logo.png)](http://localhost:8080/swagger-ui/index.html)


 
 
