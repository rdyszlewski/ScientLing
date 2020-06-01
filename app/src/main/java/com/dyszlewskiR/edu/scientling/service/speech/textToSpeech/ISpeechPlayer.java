package com.dyszlewskiR.edu.scientling.service.speech.textToSpeech;

import java.io.IOException;

public interface ISpeechPlayer {
    void setValues(String text, String record);
    void mute(boolean mute);
    void setRecordsEnabled(boolean enabled);
    void setSynthEnabled(boolean enabled);
    boolean isInit();
    void speak() throws IOException;
    void release();
    void setCallback(ISpeechCallback callback);
}
