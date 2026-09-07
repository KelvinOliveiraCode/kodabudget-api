package com.kodabudget.infrastructure.ai;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Auditoria das chamadas de ferramenta feitas pela IA (evolução do desafio):
 * cada execução de tool é registrada com horário e argumentos, exposta em
 * endpoint de auditoria.
 */
@Component
public class ToolAudit {

    public record Entry(OffsetDateTime at, String tool, String a, String b, String c) {
    }

    private final List<Entry> entries = new ArrayList<>();

    public void record(String tool, String a, String b, String c) {
        entries.add(new Entry(OffsetDateTime.now(), tool, a, b, c));
    }

    public List<Entry> entries() {
        return Collections.unmodifiableList(entries);
    }
}
