package com.kodabudget.infrastructure.voice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * STT local via faster-whisper (host já tem o pacote Python instalado).
 * O áudio é salvo em arquivo temporário e transcrito pelo CLI do Python.
 */
@Component
public class LocalWhisperStt implements SpeechToTextBridge {

    private static final Logger log = LoggerFactory.getLogger(LocalWhisperStt.class);

    @Value("${kodabudget.voice.python:python}")
    private String python;

    @Value("${kodabudget.voice.stt-model:small}")
    private String model;

    @Value("${kodabudget.voice.stt-language:pt}")
    private String language;

    @Override
    public String transcribe(byte[] audio) {
        File tmp = null;
        try {
            tmp = File.createTempFile("kb-stt-", ".wav");
            Files.write(tmp.toPath(), audio);
            String script = """
                    import sys
                    from faster_whisper import WhisperModel
                    model = WhisperModel("%s", device="cpu", compute_type="int8")
                    segments, _ = model.transcribe(sys.argv[1], language="%s")
                    print("".join(s.text for s in segments).strip())
                    """.formatted(model, language);
            File scriptFile = File.createTempFile("kb-stt-", ".py");
            Files.writeString(scriptFile.toPath(), script);
            Process p = new ProcessBuilder(python, scriptFile.getAbsolutePath(),
                    tmp.getAbsolutePath())
                    .redirectErrorStream(false)
                    .start();
            String out = new String(p.getInputStream().readAllBytes());
            boolean done = p.waitFor(120, TimeUnit.SECONDS);
            if (!done || p.exitValue() != 0) {
                String err = new String(p.getErrorStream().readAllBytes());
                log.error("whisper exit={} err={}", p.exitValue(), err);
                throw new IllegalStateException("Falha na transcrição local");
            }
            Files.deleteIfExists(scriptFile.toPath());
            return out.trim();
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Falha na transcrição local", e);
        } finally {
            if (tmp != null) {
                try {
                    Files.deleteIfExists(tmp.toPath());
                } catch (IOException ignored) {
                }
            }
        }
    }
}
