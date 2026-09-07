package com.kodabudget.infrastructure.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * Orquestra o fluxo de IA: prompt de sistema (persona financeira),
 * registro das tools e chamada ao modelo. Construído sobre o
 * {@code ChatClient} do Spring AI.
 */
@Service
public class AssistantService {

    private final ChatClient chatClient;

    public AssistantService(ChatClient.Builder builder, BudgetTools tools) {
        this.chatClient = builder
                .defaultSystem("""
                        Você é o assistente financeiro do KodaBudget. Trate a pessoa \
                        por 'você' e responda em português do Brasil, em uma ou duas \
                        frases curtas e naturais para serem faladas em voz. Para \
                        registrar ou consultar transações, use SEMPRE as ferramentas \
                        disponíveis — nunca invente dados. Se não entender o comando, \
                        peça para a pessoa repetir.
                        """)
                .defaultTools(tools)
                .build();
    }

    /** Processa o texto do comando e devolve a resposta final em texto. */
    public String chat(String userText) {
        return chatClient.prompt()
                .user(userText)
                .call()
                .content();
    }
}
