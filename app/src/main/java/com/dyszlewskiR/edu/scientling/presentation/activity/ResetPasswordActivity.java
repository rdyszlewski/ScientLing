package com.dyszlewskiR.edu.scientling.presentation.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.service.net.connection.URLConnector;
import com.dyszlewskiR.edu.scientling.service.net.values.ResponseStatus;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.URLConnection;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText mEmailEditText;
    private Button mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        setupToolbar();
        setupControls();
        setListener();
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupControls(){
        mEmailEditText = (EditText)findViewById(R.id.email_edit_text);
        mSendButton = (Button)findViewById(R.id.send_button);
    }

    private void setListener(){
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetAsyncTask task = new ResetAsyncTask();
                task.execute(mEmailEditText.getText().toString());
            }
        });
    }

    private class ResetAsyncTask extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
            URLConnection connection;
            boolean result = false;
            try {
                connection = new ResetPasswordRequest().start(params[0]);
                ResetResponse resetResponse = new ResetResponse((HttpURLConnection) connection);
                result = resetResponse.getResultCode() == ResetResponse.MESSAGE_SEND;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(result){
                Toast.makeText(getBaseContext(), "Zresetowano hasło", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


class ResetPasswordRequest {
    private static final String RESET_REQUEST = Constants.SERVER_ADDRESS + "/reset";
    private final String EMAIL = "email";

    public HttpURLConnection start(String email) throws IOException, JSONException {
        HttpURLConnection connection = URLConnector.getHttpConnection(RESET_REQUEST);
        String content = getJson(email);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", String.valueOf(content.length()));
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        OutputStream os = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
        writer.write(content);
        writer.close();
        connection.connect();
        return connection;
        /*HttpURLConnection connection = URLConnector.getHttpConnection(Constants.SERVER_ADDRESS+"/hello");
        Log.d(getClass().getSimpleName(), Constants.SERVER_ADDRESS + "/hello");
        connection.connect();
        return connection;*/
    }

    public String getJson(String email) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(EMAIL, email);
        return json.toString();
    }
}

class ResetResponse{

    public static final int MESSAGE_SEND = 1;
    public static final int NOT_FOUND = -1;
    public static final int ERROR = -2;

    private HttpURLConnection mConnection;

    public ResetResponse(HttpURLConnection connection){
        mConnection = connection;
    }

    public int getResultCode() throws IOException{
        int responseCode = mConnection.getResponseCode();
        switch (responseCode){
            case ResponseStatus.OK:
                return MESSAGE_SEND;
            case ResponseStatus.NO_CONTENT:
                return NOT_FOUND;
            default:
                return ERROR;
        }
    }
}