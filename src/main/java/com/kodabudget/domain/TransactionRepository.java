package com.kodabudget.domain;

import java.util.List;
import java.util.Optional;

/** Contrato de persistência — pertence ao domínio; implementação fica na infra. */
public interface TransactionRepository {
    Transaction save(Transaction transaction);

    Optional<Transaction> findById(TransactionId id);

    List<Transaction> findAll();

    List<Transaction> findByMonth(int year, int month);

    List<Transaction> findByCategoryAndMonth(Category category, int year, int month);
}
