package com.kodabudget.infrastructure.ai;

import com.kodabudget.application.CreateTransactionCommand;
import com.kodabudget.application.CreateTransactionUseCase;
import com.kodabudget.application.ListTransactionsUseCase;
import com.kodabudget.application.MonthlySummaryUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * Ferramentas expostas ao modelo via Spring AI Tool Calling.
 * Cada @Tool chama um use case da aplicação — a IA nunca toca no
 * repositório direto, respeitando as camadas.
 */
@Component
public class BudgetTools {

    private static final Logger log = LoggerFactory.getLogger(BudgetTools.class);

    private final CreateTransactionUseCase createTransaction;
    private final ListTransactionsUseCase listTransactions;
    private final MonthlySummaryUseCase monthlySummary;
    private final ToolAudit audit;

    public BudgetTools(CreateTransactionUseCase createTransaction,
                       ListTransactionsUseCase listTransactions,
                       MonthlySummaryUseCase monthlySummary, ToolAudit audit) {
        this.createTransaction = createTransaction;
        this.listTransactions = listTransactions;
        this.monthlySummary = monthlySummary;
        this.audit = audit;
    }

    @Tool(description = "Registra uma transacao financeira (despesa ou receita). "
            + "Use quando a pessoa disser que gastou ou recebeu um valor.")
    public String createTransaction(
            @ToolParam(description = "descricao curta, ex: 'mercado'") String description,
            @ToolParam(description = "valor positivo em reais, ex: 45.90") double amount,
            @ToolParam(description = "categoria: FOOD, TRANSPORT, HOUSING, HEALTH, "
                    + "EDUCATION, ENTERTAINMENT, INCOME, OTHER") String category) {
        log.info("TOOL createTransaction: {} {} {}", description, amount, category);
        audit.record("createTransaction", description, String.valueOf(amount), category);
        var tx = createTransaction.execute(new CreateTransactionCommand(
                description, BigDecimal.valueOf(amount), parse(category), null));
        return "Transacao registrada com id " + tx.id().value();
    }

    @Tool(description = "Lista transacoes de um mes. Use quando a pessoa pedir "
            + "para ver gastos ou lancamentos do mes.")
    public String listTransactions(
            @ToolParam(description = "ano, ex: 2026") int year,
            @ToolParam(description = "mes de 1 a 12") int month) {
        log.info("TOOL listTransactions: {}/{}", month, year);
        audit.record("listTransactions", String.valueOf(year), String.valueOf(month), null);
        String rows = listTransactions.byMonth(year, month).stream()
                .map(t -> "%s | R$ %.2f | %s | %s".formatted(
                        t.date(), t.amount(), t.category(), t.description()))
                .collect(Collectors.joining("\n"));
        return rows.isEmpty() ? "Nenhuma transacao neste mes." : rows;
    }

    @Tool(description = "Sumario mensal: total por categoria, despesas e receitas "
            + "do mes. Use quando a pessoa pedir um resumo ou balanco.")
    public String monthlySummary(
            @ToolParam(description = "ano, ex: 2026") int year,
            @ToolParam(description = "mes de 1 a 12") int month) {
        log.info("TOOL monthlySummary: {}/{}", month, year);
        audit.record("monthlySummary", String.valueOf(year), String.valueOf(month), null);
        var s = monthlySummary.execute(year, month);
        String byCat = s.byCategory().entrySet().stream()
                .map(e -> "%s: R$ %.2f".formatted(e.getKey(), e.getValue()))
                .collect(Collectors.joining(", "));
        return "Despesas: R$ %.2f | Receitas: R$ %.2f | Por categoria: %s"
                .formatted(s.expenses(), s.income(), byCat.isEmpty() ? "sem lancamentos" : byCat);
    }

    private com.kodabudget.domain.Category parse(String category) {
        try {
            return com.kodabudget.domain.Category.valueOf(category.trim().toUpperCase());
        } catch (Exception e) {
            return com.kodabudget.domain.Category.OTHER;
        }
    }
}
