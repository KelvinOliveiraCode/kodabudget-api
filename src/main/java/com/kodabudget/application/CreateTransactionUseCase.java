package com.kodabudget.application;

import com.kodabudget.domain.Transaction;
import com.kodabudget.domain.TransactionId;
import com.kodabudget.domain.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/** Use case: registrar uma nova transação (usado por REST e por tool calling). */
@Service
public class CreateTransactionUseCase {

    private final TransactionRepository repository;

    public CreateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction execute(CreateTransactionCommand command) {
        Transaction tx = new Transaction(
                TransactionId.newId(),
                command.description() == null ? "" : command.description().trim(),
                command.amount(),
                command.category(),
                command.date() == null ? java.time.LocalDate.now() : command.date());
        return repository.save(tx);
    }
}
