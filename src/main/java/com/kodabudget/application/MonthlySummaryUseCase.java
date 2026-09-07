package com.kodabudget.application;

import com.kodabudget.domain.Category;
import com.kodabudget.domain.Transaction;
import com.kodabudget.domain.TransactionRepository;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * Use case: sumário mensal — total por categoria, despesa total e receita total.
 * Evolução implementada no desafio (consulta financeira além do CRUD).
 */
@Service
public class MonthlySummaryUseCase {

    private final TransactionRepository repository;

    public MonthlySummaryUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public record Summary(YearMonth month, Map<Category, BigDecimal> byCategory,
                          BigDecimal expenses, BigDecimal income) {
    }

    public Summary execute(int year, int month) {
        List<Transaction> txs = repository.findByMonth(year, month);
        Map<Category, BigDecimal> byCategory = new EnumMap<>(Category.class);
        BigDecimal expenses = BigDecimal.ZERO;
        BigDecimal income = BigDecimal.ZERO;
        for (Transaction tx : txs) {
            byCategory.merge(tx.category(), tx.amount(), BigDecimal::add);
            if (tx.category() == Category.INCOME) {
                income = income.add(tx.amount());
            } else {
                expenses = expenses.add(tx.amount());
            }
        }
        return new Summary(YearMonth.of(year, month), byCategory, expenses, income);
    }
}
