package com.dyszlewskiR.edu.scientling.service.speech.textToSpeech;


import android.net.Uri;

import java.io.IOException;

public interface IRecordPlayer {
    void init();
    boolean isInit();
    void play(Uri uri) throws IOException;
    void release();
    void setCallback(ISpeechCallback callback);
}
