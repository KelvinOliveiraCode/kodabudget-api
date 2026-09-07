package com.kodabudget.infrastructure.voice;

/**
 * Ponte TTS: converte a resposta final em áudio MP3.
 *
 * <p>Implementação local via edge-tts (Python), usada quando não há
 * credencial de voz. Com OPENAI_API_KEY configurada, o projeto usa o
 * {@code TextToSpeechModel} oficial; sem chave, cai aqui.</p>
 */
public interface TextToSpeechBridge {
    /** Gera MP3 falado a partir do texto. */
    byte[] speak(String text);
}
