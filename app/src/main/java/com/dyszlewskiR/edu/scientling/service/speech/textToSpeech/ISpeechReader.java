package com.dyszlewskiR.edu.scientling.service.speech.textToSpeech;

public interface ISpeechReader {
    boolean isInit();
    void speak(final String text);
    void setLanguage(final String languageCode);
    void release();
    void setCallback(ISpeechCallback callback);
}
