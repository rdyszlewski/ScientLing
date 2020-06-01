package com.dyszlewskiR.edu.scientling.service.speech.textToSpeech;

import android.content.Context;
import android.net.Uri;

import com.dyszlewskiR.edu.scientling.data.file.MediaFileSystem;
import com.dyszlewskiR.edu.scientling.service.net.values.MediaType;

import java.io.IOException;

public class SpeechPlayer implements ISpeechPlayer {
    private ISpeechReader mTextToSpeech;
    private IRecordPlayer mMediaPlayer;
    private String mSetCatalog;
    private Uri mRecordUri;
    private Context mContext;

    private String mLanguageCode;
    private ISpeechCallback mCallback;

    private String mWord;
    private boolean mRecordsEnabled;
    private boolean mSynthesizeEnabled;

    public SpeechPlayer(Context context) {
        mContext = context;
        mRecordsEnabled = true;
        mSynthesizeEnabled = true;
    }

    public void setCallback(ISpeechCallback callback) {
        mCallback = callback;
        if(mMediaPlayer != null){
            mMediaPlayer.setCallback(callback);
        }
        if(mTextToSpeech != null){
            mTextToSpeech.setCallback(callback);
        }
    }

    public void setValues(String content, String recordName) {
       mRecordUri = MediaFileSystem.getMediaUri(recordName, mSetCatalog, MediaType.RECORDS, mContext);
        mWord = content;
    }

    public void setMediaCatalog(String setCatalog){
        mSetCatalog = setCatalog;
    }

    public void setLanguageCode(String languageCode){
        mLanguageCode = languageCode;
        if(mTextToSpeech != null){
            mTextToSpeech.setLanguage(languageCode);
        }
    }

    public void mute(boolean mute){
        mRecordsEnabled = !mute;
        mSynthesizeEnabled = !mute;
    }

    public void setRecordsEnabled(boolean enable){
        mRecordsEnabled = enable;
    }

    public void setSynthEnabled(boolean enable){
        mSynthesizeEnabled = enable;
    }

    /** Określa czy potrzebny element jest zainicjalisowany.
     * Jeżeli w ustawionym słówku będzie czytane nagranie sprawdzamy czy obiekt MediaPlayer jest zainicjalizowany
     * Jeżeli
     * @return
     */
    public boolean isInit(){
        if(mRecordsEnabled && mRecordUri !=null) {
            return mMediaPlayer != null && mMediaPlayer.isInit();
        } else if(mSynthesizeEnabled) {
            return mTextToSpeech != null && mTextToSpeech.isInit();
        }
        //zwracamy true. Jeżeli nie odtwarzamy żadnego dźwięku oznacza to że nie trzeba przeprowadzać
        //żadnych dodatkowych operacji. Przyjmujemy że odtwarzać jest zainicjalizowany
        return true;
    }

    public boolean isRecord() {
        return mRecordUri != null;
    }

    /** Odtwarza nagranie lub syntezuje podany wyraz.
     * Jeżeli odtwarzanie nagrań jest włączone i ustawiono nagranie
     *  -  odtwórz dźwięk
     * W przeciwnym przypadku jeśli synteza mowy jest włączona
     * - odtwórz syntezator
     * @throws IOException
     */
    public void speak() throws IOException {
        if(mRecordsEnabled && mRecordUri != null){
            playRecord(mRecordUri);
        } else if(mSynthesizeEnabled){
            synthesizeWord(mWord);
        }
    }

    private void playRecord(Uri recordUri) throws IOException {
        if(mMediaPlayer == null){
            mMediaPlayer = new RecordPlayer();
            mMediaPlayer.setCallback(mCallback);
        }
        if(!mMediaPlayer.isInit()){
            mMediaPlayer.init();
        }

        mMediaPlayer.play(recordUri);
    }

    private void synthesizeWord(String word){
        if(mTextToSpeech == null){
            mTextToSpeech = new TextToSpeech(mLanguageCode);
            mTextToSpeech.setCallback(mCallback);
        }
        mTextToSpeech.speak(word);
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        if (mTextToSpeech != null) {
            mTextToSpeech.setCallback(null);
            mTextToSpeech.release();
        }
        mCallback = null;
    }

}
