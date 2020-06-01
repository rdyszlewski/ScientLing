package com.dyszlewskiR.edu.scientling.service.net.upload;

import android.database.Cursor;

import com.dyszlewskiR.edu.scientling.data.database.creators.LessonCreator;
import com.dyszlewskiR.edu.scientling.models.entity.Lesson;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonLessons {

    private long mSetId;
    private DataManager mDataManager;
    private Cursor mCursor;

    public JsonLessons(long setId, DataManager dataManager){
        mSetId = setId;
        mDataManager = dataManager;
    }

    public boolean start(){
        mCursor = mDataManager.getAllLessonsCursor(mSetId);
        return mCursor!=null && mCursor.getCount() >0;
    }

    private final String ID = "id";
    private final String NAME = "name";
    private final String NUMBEr = "number";

    public String getLessonJson() throws JSONException {
        JSONObject node = getLessonNode();
        if(node != null){
            return node.toString();
        }
        return null;
    }

    private JSONObject getLessonNode() throws JSONException {
        if(cursorNext()){
            Lesson lesson = LessonCreator.createFromCursor(mCursor);
            return getLessonNode(lesson);
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

    private JSONObject getLessonNode(Lesson lesson) throws JSONException {
        if(lesson != null){
            JSONObject node = new JSONObject();
            node.put(ID,lesson.getId());
            node.put(NAME, lesson.getName());
            node.put(NUMBEr,lesson.getNumber());
            return node;
        }
        return null;
    }

    public void release(){
        mCursor.close();
    }
}
