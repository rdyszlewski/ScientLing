package com.dyszlewskiR.edu.scientling.service.net.setList;

import android.util.JsonReader;

import com.dyszlewskiR.edu.scientling.models.others.SetItem;
import com.dyszlewskiR.edu.scientling.service.net.values.ResponseStatus;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SetDetailResponse {

    public static final int OK = 1;
    public static final int FAILED = -1;

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String L1 = "l1";
    private static final String L2 = "l2";
    private static final String AUTHOR = "author";
    private static final String DESCRIPTION = "description";
    private static final String WORDS_COUNT = "num_words";
    private static final String SIZE = "size";
    private static final String IMAGES_SIZE = "images_size";
    private static final String RECORDS_SIZE = "records_size";
    private static final String RATING = "rating";
    private static final String DOWNLOAD_COUNT = "download_count";
    private static final String ADDED_DATE = "added_date";
    private static final String USER_DOWNLOAD = "user_download";
    private static final String USER_RATING = "user_rating";


    private HttpURLConnection mConnection;

    public SetDetailResponse(HttpURLConnection connection){
        mConnection = connection;
    }

    public int getResultCode() throws IOException {
        int responseCode = mConnection.getResponseCode();
        switch (responseCode){
            case ResponseStatus.OK:
                return OK;
            default:
                return FAILED;
        }
    }

    public SetItem getSet() throws IOException, ParseException {
        InputStream response = mConnection.getInputStream();
        InputStreamReader reader = new InputStreamReader(response);
        JsonReader jsonReader = new JsonReader(reader);
        jsonReader.beginObject();
        String name;
        SetItem item = new SetItem();
        while(jsonReader.hasNext()){
            name = jsonReader.nextName();
            switch (name){
                case ID:
                    item.setId(jsonReader.nextLong()); break;
                case NAME:
                    item.setName(jsonReader.nextString()); break;
                case L1:
                    item.setLanguageL1(jsonReader.nextString()); break;
                case L2:
                    item.setLanguageL2(jsonReader.nextString()); break;
                case AUTHOR:
                    item.setAuthor(jsonReader.nextString()); break;
                case DESCRIPTION:
                    item.setDescription(jsonReader.nextString()); break;
                case WORDS_COUNT:
                    item.setWordsCount(jsonReader.nextInt()); break;
                case SIZE:
                    item.setBasicSize(jsonReader.nextInt()); break;
                case IMAGES_SIZE:
                    item.setImagesSize(jsonReader.nextInt()); break;
                case RECORDS_SIZE:
                    item.setRecordsSize(jsonReader.nextInt()); break;
                case RATING:
                    item.setRating((float)jsonReader.nextDouble()); break;
                case DOWNLOAD_COUNT:
                    item.setDownloads(jsonReader.nextInt()); break;
                case ADDED_DATE:
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.POSTGRESS_DATE_FORMAT);
                    item.setAddedDate(simpleDateFormat.parse(jsonReader.nextString())); break;
                case USER_DOWNLOAD:
                    item.setWasDownloaded(jsonReader.nextBoolean()); break;
                case USER_RATING:
                    item.setUserRating(jsonReader.nextInt()); break;
            }
        }
        return item;
    }
}
