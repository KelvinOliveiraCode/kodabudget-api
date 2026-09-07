package com.kodabudget.infrastructure.voice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

/**
 * TTS local via edge-tts (voz pt-BR AntoniNeural no host).
 * Gera MP3 da resposta final sem custo nem credencial.
 */
@Component
public class LocalEdgeTts implements TextToSpeechBridge {

    private static final Logger log = LoggerFactory.getLogger(LocalEdgeTts.class);

    @Value("${kodabudget.voice.python:python}")
    private String python;

    @Value("${kodabudget.voice.tts-voice:pt-BR-AntonioNeural}")
    private String voice;

    @Override
    public byte[] speak(String text) {
        File tmp = null;
        try {
            tmp = File.createTempFile("kb-tts-", ".mp3");
            Process p = new ProcessBuilder(python, "-m", "edge_tts",
                    "--voice", voice, "--text", text,
                    "--write-media", tmp.getAbsolutePath())
                    .start();
            boolean done = p.waitFor(60, TimeUnit.SECONDS);
            if (!done || p.exitValue() != 0) {
                String err = new String(p.getErrorStream().readAllBytes());
                log.error("edge-tts exit={} err={}", p.exitValue(), err);
                throw new IllegalStateException("Falha ao gerar audio");
            }
            byte[] mp3 = Files.readAllBytes(tmp.toPath());
            if (mp3.length < 100) {
                throw new IllegalStateException("audio vazio");
            }
            return mp3;
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Falha ao gerar audio", e);
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
