package com.kodabudget.application;

import com.kodabudget.domain.Category;
import com.kodabudget.domain.Transaction;
import com.kodabudget.domain.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/** Use case: listar transações (todas ou de um mês/categoria). */
@Service
public class ListTransactionsUseCase {

    private final TransactionRepository repository;

    public ListTransactionsUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> all() {
        return repository.findAll();
    }

    public List<Transaction> byMonth(int year, int month) {
        return repository.findByMonth(year, month);
    }

    public List<Transaction> byCategoryAndMonth(Category category, int year, int month) {
        return repository.findByCategoryAndMonth(category, year, month);
    }
}
