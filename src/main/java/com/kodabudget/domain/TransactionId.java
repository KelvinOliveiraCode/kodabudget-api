package com.kodabudget.domain;

import java.util.UUID;

/** Identificador forte de transação — evita trocar IDs por engano em assinaturas. */
public record TransactionId(UUID value) {
    public static TransactionId newId() {
        return new TransactionId(UUID.randomUUID());
    }

    public static TransactionId from(UUID value) {
        return new TransactionId(value);
    }
}
