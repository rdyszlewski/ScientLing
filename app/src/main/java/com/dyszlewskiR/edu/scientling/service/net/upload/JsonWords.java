package com.dyszlewskiR.edu.scientling.service.net.upload;

import android.database.Cursor;

import com.dyszlewskiR.edu.scientling.data.database.creators.WordCreator;
import com.dyszlewskiR.edu.scientling.models.entity.Definition;
import com.dyszlewskiR.edu.scientling.models.entity.Hint;
import com.dyszlewskiR.edu.scientling.models.entity.Sentence;
import com.dyszlewskiR.edu.scientling.models.entity.Translation;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JsonWords {

    private long mSetId;
    private DataManager mDataManager;
    private Cursor mCursor;

    public JsonWords(long setId,DataManager dataManager){
        mSetId = setId;
        mDataManager = dataManager;
    }

    public boolean start(){
        mCursor = mDataManager.getAllWordsCursor(mSetId);
        return mCursor != null && mCursor.getCount() >0;
    }

    public int getWordsCount(){
        if(mCursor != null){
            return mCursor.getCount();
        }
        return 0;
    }

    private  final String ID = "id";
    private  final String NAME = "name";
    private  final String L1 = "l1";
    private  final String L2 = "l2";

    private  final String NUMBER = "number";

    private  final String CONTENT = "content";
    private  final String TRANSLATIONS = "translations";
    private  final String TRANSLATION_CONTENT = "c";
    private  final String DEFINITION = "definition";
    private  final String DEFINITION_CONTENT = "c";
    private  final String DEFINITION_TRANSLATION = "t";
    private  final String CATEGORY = "category";
    private  final String PART_OF_SPEECH = "part";
    private  final String DIFFICULTY = "difficulty";
    private  final String SENTENCES = "sentences";
    private  final String SENTENCE_CONTENT = "c";
    private  final String SENTENCE_TRANSLATION = "t";
    private  final String HINTS = "hints";
    private  final String HINT_CONTENT = "c";
    private  final String IMAGE = "image";
    private  final String RECORD = "record";
    private  final String LESSON = "lesson";


    public String getWordJson() throws JSONException {
        if(cursorNext()){
            Word word = WordCreator.createFromCursor(mCursor);
            mDataManager.completeWord(word);
            if(word != null){
                JSONObject node = new JSONObject();
                node.put(CONTENT,word.getContent());
                if(word.getTranslations()!=null){
                    node.put(TRANSLATIONS, getTranslationArray(word.getTranslations()));
                }
                if(word.getDefinition() != null){
                    node.put(DEFINITION, getDefinitionObject(word.getDefinition()));
                }
                if(word.getCategory() != null){
                    node.put(CATEGORY,word.getCategory().getId());
                }
                if(word.getPartsOfSpeech() != null){
                    node.put(PART_OF_SPEECH, word.getPartsOfSpeech().getId());
                }
                if(word.getDifficult() > 0){
                    node.put(DIFFICULTY, word.getDifficult());
                }
                if(word.getSentences() != null && word.getSentences().size() >0){
                    node.put(SENTENCES, getSentencesArray(word.getSentences()));
                }
                if(word.getHints() != null && word.getHints().size() > 0){
                    node.put(HINTS, getHintsArray(word.getHints()));
                }
                if(word.getImageName() != null){
                    node.put(IMAGE, word.getImageName());
                }
                if(word.getRecordName() != null){
                    node.put(RECORD, word.getRecordName());
                }
                node.put(LESSON, word.getLessonId());

                return node.toString();
            }
        }
        return null;
    }

    private boolean cursorNext(){
        if(mCursor.isBeforeFirst()){
            return mCursor.moveToFirst();
        } else {
            return mCursor.moveToNext();
        }
    }

    private JSONArray getTranslationArray(List<Translation> translationList) throws JSONException {
        JSONArray translationArray = new JSONArray();
        for(Translation translation : translationList){
            JSONObject node = new JSONObject();
            node.put(TRANSLATION_CONTENT, translation.getContent());
            translationArray.put(node);
        }
        return translationArray;
    }

    private JSONObject getDefinitionObject(Definition definition) throws JSONException {
        if(definition != null) {
            JSONObject node = new JSONObject();
            node.put(DEFINITION_CONTENT, definition.getContent());
            if (definition.getTranslation() != null) {
                node.put(DEFINITION_TRANSLATION, definition.getTranslation());
            }
            return node;
        }
        return null;
    }

    private JSONArray getSentencesArray(List<Sentence> sentenceList) throws JSONException {
        JSONArray sentencesArray = new JSONArray();
        for(Sentence sentence: sentenceList){
            JSONObject node = new JSONObject();
            node.put(SENTENCE_CONTENT, sentence.getContent());
            if(sentence.getTranslation() != null){
                node.put(SENTENCE_TRANSLATION, sentence.getTranslation());
            }
            sentencesArray.put(node);
        }
        return sentencesArray;
    }

    private JSONArray getHintsArray(List<Hint> hintsList) throws JSONException {
        JSONArray hintsArray = new JSONArray();
        for(Hint hint: hintsList){
            JSONObject node = new JSONObject();
            node.put(HINT_CONTENT, hint.getContent());
            hintsArray.put(node);
        }
        return hintsArray;

    }

    public void release(){
        if(mCursor != null && !mCursor.isClosed()){
            mCursor.close();
        }
    }
}
