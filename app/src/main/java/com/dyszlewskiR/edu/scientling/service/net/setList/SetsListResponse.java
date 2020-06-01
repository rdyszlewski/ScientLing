package com.dyszlewskiR.edu.scientling.service.net.setList;

import android.util.JsonReader;
import android.util.JsonToken;

import com.dyszlewskiR.edu.scientling.models.others.SetItem;
import com.dyszlewskiR.edu.scientling.service.net.values.ResponseStatus;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class SetsListResponse {

    public static final int OK = 1;
    public static final int FAILED = -1;

    private HttpURLConnection mConnection;

    public SetsListResponse(HttpURLConnection connection){
        mConnection = connection;
    }

    public int getResultCode() throws IOException {
        if(mConnection != null){
            int responseCode = mConnection.getResponseCode();
            switch (responseCode){
                case ResponseStatus.OK:
                    return OK;
            }
        }
        return FAILED;
    }

    public List<SetItem> getSetItemsList() throws IOException, JSONException {
        if(mConnection == null){
            return null;
        }
        InputStreamReader reader = new InputStreamReader(mConnection.getInputStream());
        JsonReader jsonReader = new JsonReader(reader);
        List<SetItem> itemsList = new ArrayList<>();
        jsonReader.beginArray();
        SetItem item;
        while(jsonReader.hasNext()){
            item = getSingleItem(jsonReader);
            if(item != null){
                itemsList.add(item);
            }
        }
        jsonReader.endArray();
        return itemsList;
    }

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String WORDS_COUNT = "words_count";
    private static final String SIZE = "size";
    private static final String DESCRIPTION = "description";
    private static final String AUTHOR = "author";
    private static final String RATING = "rating";
    private static final String DOWNLOADS = "downloads";
    private static final String IMAGES_SIZE = "images_size";
    private static final String RECORDS_SIZE = "records_size";
    private static final String LANGUAGE_L1 = "l1";
    private static final String LANGUAGE_L2 = "l2";

    private static SetItem getSingleItem(JsonReader jsonReader) throws IOException, JSONException {
        SetItem item = null;
        jsonReader.beginObject();
        String name;
        JsonToken token;
        do {
            if (item == null) {
                item = new SetItem();
            }
            name = jsonReader.nextName();
            switch (name) {
                case ID:
                    item.setId(jsonReader.nextLong());
                    break;
                case NAME:
                    item.setName(jsonReader.nextString());
                    break;
                case WORDS_COUNT:
                    item.setWordsCount(jsonReader.nextInt());
                    break;
                case SIZE:
                    item.setBasicSize(jsonReader.nextInt());
                    break;
                case DESCRIPTION:
                    item.setDescription(jsonReader.nextString());
                    break;
                case AUTHOR:
                    item.setAuthor(jsonReader.nextString());
                    break;
                case RATING:
                    item.setRating((float) jsonReader.nextDouble());
                    break;
                case DOWNLOADS:
                    item.setDownloads(jsonReader.nextInt());
                    break;
                case IMAGES_SIZE:
                    item.setImagesSize(jsonReader.nextInt());
                    break;
                case RECORDS_SIZE:
                    item.setRecordsSize(jsonReader.nextInt());
                    break;
                case LANGUAGE_L1:
                    item.setLanguageL1(jsonReader.nextString());
                    break;
                case LANGUAGE_L2:
                    item.setLanguageL2(jsonReader.nextString());
                    break;
            }
            token = jsonReader.peek();
        } while (token != null && !token.equals(JsonToken.END_OBJECT));
        jsonReader.endObject();
        return item;
    }
}
