package com.kodabudget.application;

import com.kodabudget.domain.Category;
import com.kodabudget.domain.Transaction;
import com.kodabudget.domain.TransactionId;
import com.kodabudget.domain.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes dos use cases com repositório fake em memória — rodam sem banco,
 * sem rede e sem chave de IA (arquitetura em camadas permite isso).
 */
class UseCaseTest {

    static class FakeRepo implements TransactionRepository {
        final Map<TransactionId, Transaction> data = new HashMap<>();

        public Transaction save(Transaction t) {
            data.put(t.id(), t);
            return t;
        }

        public Optional<Transaction> findById(TransactionId id) {
            return Optional.ofNullable(data.get(id));
        }

        public List<Transaction> findAll() {
            return new ArrayList<>(data.values());
        }

        public List<Transaction> findByMonth(int year, int month) {
            return data.values().stream()
                    .filter(t -> t.date().getYear() == year && t.date().getMonthValue() == month)
                    .toList();
        }

        public List<Transaction> findByCategoryAndMonth(Category c, int y, int m) {
            return findByMonth(y, m).stream().filter(t -> t.category() == c).toList();
        }
    }

    private final FakeRepo repo = new FakeRepo();

    @Test
    @DisplayName("Criar transação gera id, persiste e valida data default")
    void createTransaction() {
        var useCase = new CreateTransactionUseCase(repo);
        Transaction tx = useCase.execute(new CreateTransactionCommand(
                "mercado", new BigDecimal("45.90"), Category.FOOD, null));

        assertThat(tx.id()).isNotNull();
        assertThat(tx.date()).isEqualTo(LocalDate.now());
        assertThat(repo.data).hasSize(1);
    }

    @Test
    @DisplayName("Valor não-positivo é rejeitado pelo domínio")
    void invalidAmountRejected() {
        var useCase = new CreateTransactionUseCase(repo);
        assertThatThrownBy(() -> useCase.execute(new CreateTransactionCommand(
                "invalida", BigDecimal.ZERO, Category.OTHER, null)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Descrição vazia é rejeitada pelo domínio")
    void blankDescriptionRejected() {
        var useCase = new CreateTransactionUseCase(repo);
        assertThatThrownBy(() -> useCase.execute(new CreateTransactionCommand(
                "  ", new BigDecimal("10"), Category.OTHER, null)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Sumário mensal separa despesa, receita e categoria")
    void monthlySummary() {
        var create = new CreateTransactionUseCase(repo);
        create.execute(new CreateTransactionCommand(
                "mercado", new BigDecimal("100"), Category.FOOD, LocalDate.of(2026, 9, 2)));
        create.execute(new CreateTransactionCommand(
                "salario", new BigDecimal("5000"), Category.INCOME, LocalDate.of(2026, 9, 1)));
        create.execute(new CreateTransactionCommand(
                "onibus", new BigDecimal("50"), Category.TRANSPORT, LocalDate.of(2026, 9, 3)));
        create.execute(new CreateTransactionCommand(
                "outro mes", new BigDecimal("999"), Category.FOOD, LocalDate.of(2026, 8, 31)));

        var summary = new MonthlySummaryUseCase(repo).execute(2026, 9);

        assertThat(summary.expenses()).isEqualByComparingTo("150");
        assertThat(summary.income()).isEqualByComparingTo("5000");
        assertThat(summary.byCategory()).containsEntry(Category.FOOD, new BigDecimal("100"));
    }

    @Test
    @DisplayName("Listagem por mês filtra corretamente")
    void listByMonth() {
        var create = new CreateTransactionUseCase(repo);
        create.execute(new CreateTransactionCommand(
                "setembro", new BigDecimal("10"), Category.OTHER, LocalDate.of(2026, 9, 10)));
        create.execute(new CreateTransactionCommand(
                "agosto", new BigDecimal("20"), Category.OTHER, LocalDate.of(2026, 8, 10)));

        var list = new ListTransactionsUseCase(repo);
        assertThat(list.byMonth(2026, 9)).hasSize(1);
        assertThat(list.byCategoryAndMonth(Category.OTHER, 2026, 9)).hasSize(1);
    }
}
