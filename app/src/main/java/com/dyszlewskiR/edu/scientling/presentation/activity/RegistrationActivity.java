package com.dyszlewskiR.edu.scientling.presentation.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.service.net.connection.URLConnector;
import com.dyszlewskiR.edu.scientling.service.net.register.RegisterRequest;
import com.dyszlewskiR.edu.scientling.service.net.register.RegisterResponse;
import com.dyszlewskiR.edu.scientling.service.net.values.ResponseStatus;
import com.dyszlewskiR.edu.scientling.service.preferences.LogPref;
import com.dyszlewskiR.edu.scientling.utils.Constants;
import com.dyszlewskiR.edu.scientling.utils.MD5;
import com.dyszlewskiR.edu.scientling.utils.resources.StreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private final int PASSWORD_LENGTH = 5;
    private final String[] INCORRECT_ELEMENTS = {"-", "_", "=",};

    private EditText mLoginEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mRepeatPassEditText;
    private Button mRegisterButton;

    private boolean mIsEditAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupToolbar();
        setupControls();
        setListeners();
        getData();
        if(mIsEditAccount){
            Log.d(getClass().getSimpleName(), "zaraz będzie pobierany email");
            startDownloadInfo();
        }
    }

    private void startDownloadInfo(){
        AccountInfoAsyncTask task = new AccountInfoAsyncTask();
        task.execute(mLoginEditText.getText().toString());
    }

    private void getData(){
        Intent intent = getIntent();
        String login = intent.getStringExtra("login");
        mIsEditAccount = login != null;
        if(mIsEditAccount){
            mLoginEditText.setEnabled(false);
            mLoginEditText.setText(login);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.registration));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupControls() {
        mLoginEditText = (EditText) findViewById(R.id.login_edit_text);
        mEmailEditText = (EditText) findViewById(R.id.email_edit_text);
        mPasswordEditText = (EditText) findViewById(R.id.password_edit_text);
        mRepeatPassEditText = (EditText) findViewById(R.id.repeat_password_edit_text);
        mRegisterButton = (Button) findViewById(R.id.register_button);

        if(mIsEditAccount){
            mLoginEditText.setEnabled(false);
        }
    }

    private void setListeners() {
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    RegisterParams params = new RegisterParams(mLoginEditText.getText().toString(),
                            mEmailEditText.getText().toString(),
                            mPasswordEditText.getText().toString());
                    if(!mIsEditAccount){
                        RegisterAsyncTask task = new RegisterAsyncTask();
                        task.execute(params);
                    } else {
                        EditAccountAsyncTask task = new EditAccountAsyncTask();
                        task.execute(params);
                    }

                }
            }
        });
    }

    private boolean validate() {
        boolean correct = true;
        if (mLoginEditText.getText().toString().isEmpty()) {
            mLoginEditText.setError(getString(R.string.not_empty_field));
            correct = false;
        }
        if (!checkEmail(mEmailEditText.getText().toString())) {
            correct = false;
        }
        if (!checkPasswords(mPasswordEditText.getText().toString(), mRepeatPassEditText.getText().toString())) {
            correct = false;
        }
        return correct;
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private boolean checkEmail(String email) {
        if (email.isEmpty() && !mIsEditAccount) {
            mEmailEditText.setError(getString(R.string.not_empty_field));
            return false;
        }
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        if (!matcher.find()) {
            mEmailEditText.setError(getString(R.string.email_incorrect));
            return false;
        }
        return true;
    }

    public boolean checkPasswords(String password, String repeatPassword) {
        if (password.isEmpty() && !mIsEditAccount) {
            mPasswordEditText.setError(getString(R.string.not_empty_field));
            return false;
        }
        if (!password.isEmpty() && password.length() < PASSWORD_LENGTH) {
            mPasswordEditText.setError(getString(R.string.short_password) + " " + PASSWORD_LENGTH + " " + getString(R.string.signs));
            return false;
        }

        /*if (!password.matches(".*\\d+.*")) {
            mPasswordEditText.setError(getString(R.string.must_contain_number));
            return false;
        }*/

        for (String element : INCORRECT_ELEMENTS) {
            if (password.contains(element)) {
                mPasswordEditText.setError(getString(R.string.not_contain_element) + getIncorrectElementList());
                return false;
            }
        }

        if (!password.equals(repeatPassword)) {
            mPasswordEditText.setError(getString(R.string.password_different));
            return false;
        }

        return true;
    }

    private String getIncorrectElementList() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < INCORRECT_ELEMENTS.length; i++) {
            stringBuilder.append(INCORRECT_ELEMENTS[i]);
            if (i != INCORRECT_ELEMENTS.length - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class RegisterParams {
        private String mLogin;
        private String mEmail;
        private String mPassword;

        public RegisterParams(String login, String email, String password) {
            mLogin = login;
            mEmail = email;
            mPassword = password;
        }

        public String getLogin() {
            return mLogin;
        }

        public String getEmail() {
            return mEmail;
        }

        public String getPassword() {
            return mPassword;
        }
    }

    private class RegisterAsyncTask extends AsyncTask<RegisterParams, Void, Integer> {

        @Override
        protected Integer doInBackground(RegisterParams... params) {
            try {
                String login = params[0].getLogin();
                String email = params[0].getEmail();
                String password = MD5.getMD5(params[0].getPassword());
                RegisterRequest registerRequest = new RegisterRequest();
                RegisterResponse response = new RegisterResponse(registerRequest.start(login, email, password));
                int responseCode = response.getResultCode();
                response.closeConnection();
                return responseCode;
            } catch (NoSuchAlgorithmException | JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result != null) {
                switch (result) {
                    case RegisterResponse.OK:
                        Toast.makeText(getBaseContext(), getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                        finish();
                        break;
                    case RegisterResponse.EXIST_EMAIL:
                        mEmailEditText.setError(getString(R.string.email_exist));
                        mLoginEditText.setError(null);
                        break;
                    case RegisterResponse.EXIST_LOGIN:
                        mLoginEditText.setError(getString(R.string.login_exist));
                        mEmailEditText.setError(null);
                        break;
                    case RegisterResponse.EXIST_BOTH:
                        mEmailEditText.setError(getString(R.string.email_exist));
                        mLoginEditText.setError(getString(R.string.login_exist));
                        break;
                }
            }
        }
    }

    private class EditAccountAsyncTask extends AsyncTask<RegisterParams, Void, Integer>{

        private final int OK = 1;
        private final int FAILED = -1;

        @Override
        protected Integer doInBackground(RegisterParams... params){
            String login = params[0].getLogin();
            String email = params[0].getEmail();
            String password = null;
            try {
                password = MD5.getMD5(params[0].getPassword());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            EditAccountRequest request = new EditAccountRequest();
            EditAccountResponse response = null;
            try {
                response = new EditAccountResponse(request.start(login, email, password));
                if(response.getResultCode()==EditAccountResponse.OK){ // TODO przerobić to, dostosować do poszczególnych wyjątków
                    return OK;
                } else {
                    return FAILED;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(response != null){
                    response.close();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer result){
            if(result != null){
                switch (result){
                    case OK:
                        Toast.makeText(getBaseContext(), "Pomyślnie zmieniono ustawiania", Toast.LENGTH_SHORT).show();
                        break;
                    case FAILED:
                        Toast.makeText(getBaseContext(), "Wystąpił błąd", Toast.LENGTH_SHORT).show();
                        break;
                }
            } else {
                Toast.makeText(getBaseContext(), "Wystąpił błąd", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AccountInfoAsyncTask extends AsyncTask<String,Void, String>{

        @Override
        protected String doInBackground(String... params) {
            AccountInfoRequest request = new AccountInfoRequest();
            AccountInfoResponse response = null;
            try {
                response = new AccountInfoResponse(request.start(params[0]));
                if(response.getResultCode() == AccountInfoResponse.OK){
                    String email = response.getEmail();
                    Log.d(getClass().getSimpleName(), email);
                    return email;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(response != null){
                    response.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            if(result != null){
                mEmailEditText.setText(result);
            }
        }
    }
}

class EditAccountRequest{
    private final String EDIT_REQUEST = Constants.SERVER_ADDRESS + "/account/";

    private final String EMAIL = "email";
    private final String PASSWORD = "password";

    public HttpURLConnection start(String login, String email, String password) throws IOException, JSONException {
        //TODO można zrobić cześc wspólną z rejestracją
        HttpURLConnection connection = URLConnector.getHttpConnection(EDIT_REQUEST + login);
        String content = getJson(email, password);
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


    }

    private String getJson(String email, String password) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(EMAIL, email);
        json.put(PASSWORD, password);
        return json.toString();
    }
}

class EditAccountResponse {
    public static final int OK = 1;
    public static final int EXIST_EMAIL = 2;
    public static final int ERROR = -1;

    public final String ERROR_PARAM = "error";
    private final String EMAIL = "email";

    private HttpURLConnection mConnection;

    public EditAccountResponse(HttpURLConnection connection){
        mConnection = connection;
    }

    public int getResultCode() throws IOException {
        int responseCode = 0;
        if(mConnection != null){
            responseCode = mConnection.getResponseCode();
            mConnection.getInputStream();
        }
        switch (responseCode){
            case ResponseStatus.OK:
                return OK;
            case ResponseStatus.PRECONDITION_FAILED:
                return EXIST_EMAIL;
            default:
                return ERROR;
        }
    }


    public void close(){
        if(mConnection != null){
            mConnection.disconnect();
        }
    }
}



class AccountInfoRequest {
    private final String INFO_REQUEST = Constants.SERVER_ADDRESS + "/account/";

    public HttpURLConnection start(String login) throws IOException {
        Log.d(getClass().getSimpleName(), login);
        HttpURLConnection connection = URLConnector.getHttpConnection(INFO_REQUEST + login);
        connection.connect();
        return connection;
    }
}

class AccountInfoResponse{
    public static final int OK = 1;
    public static final int FAILED = -1;

    private HttpURLConnection mConnection;


    public AccountInfoResponse(HttpURLConnection connection){
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



    public String getEmail() throws IOException, JSONException {
        final String EMAIL = "email";
        if(mConnection != null){
            InputStream inputStream = mConnection.getInputStream();
            String json = StreamReader.getString(inputStream);
            JSONObject object = new JSONObject(json);
            return object.getString(EMAIL);
        }
        return null;
    }

    public void close(){
        if(mConnection != null){
            mConnection.disconnect();
        }
    }
}
