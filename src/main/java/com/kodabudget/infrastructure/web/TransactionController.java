package com.kodabudget.infrastructure.web;

import com.kodabudget.application.CreateTransactionCommand;
import com.kodabudget.application.CreateTransactionUseCase;
import com.kodabudget.application.ListTransactionsUseCase;
import com.kodabudget.domain.Transaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Endpoints REST tradicionais — mesmos use cases que a IA usa por tool calling. */
@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "CRUD de transações do KodaBudget")
public class TransactionController {

    private final CreateTransactionUseCase create;
    private final ListTransactionsUseCase list;

    public TransactionController(CreateTransactionUseCase create,
                                 ListTransactionsUseCase list) {
        this.create = create;
        this.list = list;
    }

    /** Payload de criação via REST. */
    public record CreateRequest(
            @NotBlank String description,
            @DecimalMin("0.01") BigDecimal amount,
            String category,
            LocalDate date) {
    }

    /** Visão de transação devolvida ao cliente. */
    public record TxView(UUID id, String description, BigDecimal amount,
                         String category, LocalDate date) {
        static TxView from(Transaction t) {
            return new TxView(t.id().value(), t.description(), t.amount(),
                    t.category().name(), t.date());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra uma transação")
    public TxView create(@Valid @RequestBody CreateRequest request) {
        var category = request.category() == null ? "OTHER" : request.category();
        var tx = create.execute(new CreateTransactionCommand(
                request.description(), request.amount(),
                com.kodabudget.domain.Category.valueOf(category.toUpperCase()),
                request.date()));
        return TxView.from(tx);
    }

    @GetMapping
    @Operation(summary = "Lista todas as transações")
    public List<TxView> all() {
        return list.all().stream().map(TxView::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca por id")
    public TxView byId(@PathVariable UUID id) {
        return list.all().stream()
                .filter(t -> t.id().value().equals(id))
                .map(TxView::from)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("nao encontrado"));
    }
}
