package com.kodabudget.infrastructure.persistence;

import com.kodabudget.domain.Category;
import com.kodabudget.domain.Transaction;
import com.kodabudget.domain.TransactionId;
import com.kodabudget.domain.TransactionRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Representação JPA da transação (infra — o domínio não conhece JPA). */
@Entity
@Table(name = "transactions")
public class TransactionJpa {

    @Id
    private UUID id;
    private String description;
    private BigDecimal amount;
    private String category;
    private LocalDate date;

    protected TransactionJpa() {
    }

    private TransactionJpa(UUID id, String description, BigDecimal amount,
                           String category, LocalDate date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    static TransactionJpa fromDomain(Transaction tx) {
        return new TransactionJpa(tx.id().value(), tx.description(), tx.amount(),
                tx.category().name(), tx.date());
    }

    Transaction toDomain() {
        return new Transaction(TransactionId.from(id), description, amount,
                Category.valueOf(category), date);
    }

    UUID getId() {
        return id;
    }
}
