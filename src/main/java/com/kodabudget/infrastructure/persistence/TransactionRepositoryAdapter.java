package com.kodabudget.infrastructure.persistence;

import com.kodabudget.domain.Category;
import com.kodabudget.domain.Transaction;
import com.kodabudget.domain.TransactionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Adaptador JPA do contrato de domínio. */
@Repository
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final JpaTransactionRepo jpa;

    public TransactionRepositoryAdapter(JpaTransactionRepo jpa) {
        this.jpa = jpa;
    }

    @Override
    public Transaction save(Transaction transaction) {
        jpa.save(TransactionJpa.fromDomain(transaction));
        return transaction;
    }

    @Override
    public Optional<Transaction> findById(com.kodabudget.domain.TransactionId id) {
        return jpa.findById(id.value()).map(TransactionJpa::toDomain);
    }

    @Override
    public List<Transaction> findAll() {
        return jpa.findAll().stream().map(TransactionJpa::toDomain).toList();
    }

    @Override
    public List<Transaction> findByMonth(int year, int month) {
        return jpa.findByDateBetween(
                        java.time.LocalDate.of(year, month, 1),
                        java.time.LocalDate.of(year, month, 1).plusMonths(1).minusDays(1))
                .stream().map(TransactionJpa::toDomain).toList();
    }

    @Override
    public List<Transaction> findByCategoryAndMonth(Category category, int year, int month) {
        return jpa.findByCategoryAndDateBetween(category.name(),
                        java.time.LocalDate.of(year, month, 1),
                        java.time.LocalDate.of(year, month, 1).plusMonths(1).minusDays(1))
                .stream().map(TransactionJpa::toDomain).toList();
    }
}

interface JpaTransactionRepo extends JpaRepository<TransactionJpa, UUID> {
    List<TransactionJpa> findByDateBetween(java.time.LocalDate start, java.time.LocalDate end);

    List<TransactionJpa> findByCategoryAndDateBetween(String category,
                                                      java.time.LocalDate start,
                                                      java.time.LocalDate end);
}
