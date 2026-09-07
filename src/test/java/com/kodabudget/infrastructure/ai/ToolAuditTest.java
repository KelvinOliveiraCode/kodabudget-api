package com.kodabudget.infrastructure.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Testes da auditoria de tools (evolução do desafio). */
class ToolAuditTest {

    @Test
    @DisplayName("Cada chamada de tool é registrada com horário e argumentos")
    void recordsEntries() {
        ToolAudit audit = new ToolAudit();
        audit.record("createTransaction", "mercado", "45.9", "FOOD");
        audit.record("monthlySummary", "2026", "9", null);

        List<ToolAudit.Entry> entries = audit.entries();
        assertThat(entries).hasSize(2);
        assertThat(entries.get(0).tool()).isEqualTo("createTransaction");
        assertThat(entries.get(0).a()).isEqualTo("mercado");
        assertThat(entries.get(1).tool()).isEqualTo("monthlySummary");
    }
}
