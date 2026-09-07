package com.kodabudget.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Transação financeira — objeto de valor imutável (record, conforme a diretriz
 * da trilha DIO: record para modelos de transporte e valores imutáveis).
 */
public record Transaction(
        TransactionId id,
        String description,
        BigDecimal amount,
        Category category,
        LocalDate date
) {
    public Transaction {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("descricao obrigatoria");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("valor deve ser positivo");
        }
        if (date == null) {
            date = LocalDate.now();
        }
    }
}
