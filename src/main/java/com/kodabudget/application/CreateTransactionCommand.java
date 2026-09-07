package com.kodabudget.application;

import com.kodabudget.domain.TransactionId;
import com.kodabudget.domain.Category;

/** Dados para criar uma transação (entrada dos use cases). */
public record CreateTransactionCommand(
        String description,
        java.math.BigDecimal amount,
        Category category,
        java.time.LocalDate date
) {
}
