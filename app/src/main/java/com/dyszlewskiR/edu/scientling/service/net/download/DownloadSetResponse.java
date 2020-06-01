package com.dyszlewskiR.edu.scientling.service.net.download;

import com.dyszlewskiR.edu.scientling.service.net.values.ResponseStatus;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class DownloadSetResponse {
    public static final int OK = 1;
    public static final int AUTHORIZATION_FAILED = -1;
    public static final int ERROR = -2;
    public static final int NON_EXIST = -3;

    private final String NUM_WORDS = "num_words";
    private final String SET = "set";
    private final String LESSONS = "lessons";
    private final String WORDS = "words";

    private HttpURLConnection mConnection;
    private InputStream mInputStream;
    private ObjectMapper mMapper;

    private String mCurrentNode;
    private JsonParser mParser;
    private boolean mCheckedNext;

    public DownloadSetResponse(HttpURLConnection connection) throws IOException {
        mConnection = connection;
        prepareValues();
    }

    public int getResultCode() throws IOException {
        int responseCode = mConnection.getResponseCode();
        switch (responseCode){
            case ResponseStatus.OK:
                return OK;
            case ResponseStatus.UNAUTHORIZED:
                return AUTHORIZATION_FAILED;
            case ResponseStatus.NOT_FOUND:
                return NON_EXIST;
            default:
                return ERROR;
        }
    }
    public int getWordsCount() throws IOException {
        if(findNumberNode(NUM_WORDS)){
            return mParser.getIntValue();
        }
        return 0;
    }

    public JsonNode getSetJson() throws IOException {
        return getNode(SET);
    }

    private void prepareValues() throws IOException {
        if(mMapper == null){
            mMapper = new ObjectMapper();
        }
        if(mInputStream == null){
            mInputStream = mConnection.getInputStream();
        }
        if(mParser == null){
            JsonFactory jsonFactory = new JsonFactory();
            mParser = jsonFactory.createJsonParser(mInputStream);
            mParser.nextToken();
        }
    }

    private JsonNode getNode(String nodeName) throws IOException {
        if(findNode(nodeName)){
            return mMapper.readValue(mParser, JsonNode.class);
        }
        return null;
    }

    private boolean findNode(String nodeName) throws IOException {
       return findNode(nodeName, JsonToken.START_OBJECT);
    }

    private boolean findNumberNode(String nodeName)  throws IOException {
        return findNode(nodeName, JsonToken.VALUE_NUMBER_INT);
    }

    private boolean findNode(String nodeName, JsonToken token) throws IOException {
        while(mParser.nextToken() != JsonToken.END_OBJECT){
            String fieldName = mParser.getCurrentName();
            if(fieldName.equals(nodeName)){
                mCurrentNode = nodeName;
                JsonToken nextToken = mParser.nextToken();
                if(nextToken == token){
                    return true;
                }
            } else {
                mParser.skipChildren();
            }
        }
        return false;
    }

    private boolean findArray(String arrayName) throws IOException {
        return findNode(arrayName, JsonToken.START_ARRAY);
    }


    public String getSetJsonString() throws ParseException, JSONException, IOException {
        return mMapper.writeValueAsString(getSetJson());
    }

    public synchronized JsonNode getLessonJson() throws IOException {
        if(!setParserPosition(LESSONS)){
            return null;
        }
        return mMapper.readValue(mParser, JsonNode.class);
    }

    private synchronized boolean setParserPosition(String arrayName) throws IOException {
        if(mCurrentNode.equals(arrayName)){
            if(!findArray(arrayName)){
                return false;
            }
        }
        JsonToken currentToken = mParser.getCurrentToken();
        if(currentToken!=JsonToken.START_OBJECT){
            mParser.nextToken();
        }
        return mParser.getCurrentToken()==JsonToken.START_OBJECT;
    }

    public synchronized JsonNode getWordJson() throws IOException {
        if(!setParserPosition(WORDS)){
            return null;
        }
        return mMapper.readValue(mParser, JsonNode.class);
    }

    public boolean hasNextLesson() throws IOException {
        return hasNext(LESSONS);
    }

    private boolean hasNext(String arrayName) throws IOException {

        if(mCurrentNode.equals(arrayName)){
            if(mParser.nextToken() == JsonToken.START_OBJECT){
                mCheckedNext = true;
                return true;
            }
        } else {
            if(findArray(arrayName)){
                if(mParser.nextToken() == JsonToken.START_OBJECT){
                    mCheckedNext = true;
                    return true;
                }
            }
        }
        mCurrentNode = null;
        return false;
    }

    public boolean hasNextWord() throws IOException {
        return hasNext(WORDS);
    }
}
