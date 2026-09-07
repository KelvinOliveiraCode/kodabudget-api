package com.kodabudget.infrastructure.voice;

/**
 * Ponte STT: transcreve áudio em texto.
 *
 * <p>Implementação local via faster-whisper (Python), usada quando não há
 * credencial de provedor de voz configurada. Mantém a interface
 * {@code TranscriptionModel} do Spring AI no endpoint, então trocar por
 * OpenAI (whisper-1) ao setar OPENAI_API_KEY não exige mudança de código.</p>
 */
public interface SpeechToTextBridge {
    /** Transcreve os bytes de áudio (wav/mp3) em texto. */
    String transcribe(byte[] audio);
}
