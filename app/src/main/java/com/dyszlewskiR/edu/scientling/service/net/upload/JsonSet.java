package com.dyszlewskiR.edu.scientling.service.net.upload;

import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;

import org.json.JSONException;
import org.json.JSONObject;


public class JsonSet {

    private long mSetId;
    private String mDescription;
    private DataManager mDataManager;
    private VocabularySet mSet;

    public JsonSet(long setId,String description, DataManager dataManager){
        mSetId = setId;
        mDescription = description;
        mDataManager = dataManager;
    }

    public boolean start(){
        mSet = mDataManager.getSetById(mSetId);
        return mSet!=null;
    }

    private final String ID = "id";
    private final String NAME = "name";
    private final String L1 = "l1";
    private final String L2 = "l2";
    private final String DESCRIPTION = "desc";

    public String getSetJson() throws JSONException {
        if(mSet != null){
            JSONObject node = new JSONObject();
            node.put(ID, mSet.getId());
            node.put(NAME, mSet.getName());
            node.put(L1, mSet.getLanguageL1().getId());
            node.put(L2, mSet.getLanguageL2().getId());
            node.put(DESCRIPTION, mDescription);

            return node.toString();
        }
        return null;
    }


}
