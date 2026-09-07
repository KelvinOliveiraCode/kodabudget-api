package com.kodabudget.infrastructure.web;

import com.kodabudget.infrastructure.ai.AssistantService;
import com.kodabudget.infrastructure.ai.ToolAudit;
import com.kodabudget.infrastructure.voice.SpeechToTextBridge;
import com.kodabudget.infrastructure.voice.TextToSpeechBridge;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Fluxo de IA do desafio:
 * 1. cliente envia texto ou áudio;
 * 2. áudio é transcrito (STT);
 * 3. o modelo escolhe a tool e executa o use case;
 * 4. a resposta final volta em JSON — ou em MP3 quando pedida em voz.
 */
@RestController
@RequestMapping("/api/v1/assistant")
@Tag(name = "Assistant", description = "Assistente financeiro com IA")
public class AssistantController {

    private final AssistantService assistant;
    private final SpeechToTextBridge stt;
    private final TextToSpeechBridge tts;
    private final ToolAudit audit;

    public AssistantController(AssistantService assistant, SpeechToTextBridge stt,
                               TextToSpeechBridge tts, ToolAudit audit) {
        this.assistant = assistant;
        this.stt = stt;
        this.tts = tts;
        this.audit = audit;
    }

    /** Comando em texto → resposta em texto. */
    public record TextCommand(String text) {
    }

    /** Resposta do assistente. */
    public record AssistantReply(String transcript, String reply) {
    }

    @PostMapping("/chat")
    @Operation(summary = "Comando em texto", description = "Envia um comando em "
            + "texto e recebe a resposta do assistente.")
    public AssistantReply chat(@RequestBody TextCommand command) {
        String reply = assistant.chat(command.text());
        return new AssistantReply(command.text(), reply);
    }

    @PostMapping(value = "/voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Comando por voz", description = "Envia um arquivo de "
            + "áudio; a API transcreve, entende com IA, executa a ação e devolve a "
            + "resposta em texto e áudio MP3.")
    public AssistantReply voice(@RequestParam("file") MultipartFile file) throws Exception {
        String transcript = stt.transcribe(file.getBytes());
        String reply = assistant.chat(transcript);
        return new AssistantReply(transcript, reply);
    }

    @PostMapping(value = "/voice/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Comando por voz com resposta falada", description =
            "Igual ao /voice, mas devolve MP3 da resposta (text-to-speech).")
    public byte[] voiceWithAudio(@RequestParam("file") MultipartFile file) throws Exception {
        String transcript = stt.transcribe(file.getBytes());
        String reply = assistant.chat(transcript);
        return tts.speak(reply);
    }

    /** Auditoria das tools executadas pela IA (evolução do desafio). */
    @GetMapping("/audit")
    @Operation(summary = "Auditoria de chamadas de tools pela IA")
    public List<Map<String, String>> auditLog() {
        return audit.entries().stream()
                .map(e -> Map.of(
                        "at", e.at().toString(),
                        "tool", e.tool(),
                        "args", String.valueOf(e.a()) + " " + String.valueOf(e.b())
                                + " " + String.valueOf(e.c())))
                .toList();
    }
}
