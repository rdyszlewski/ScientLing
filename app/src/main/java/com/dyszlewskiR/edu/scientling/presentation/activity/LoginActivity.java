package com.dyszlewskiR.edu.scientling.presentation.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.service.net.login.Login;
import com.dyszlewskiR.edu.scientling.service.net.login.LoginParams;
import com.dyszlewskiR.edu.scientling.service.preferences.LogPref;
import com.dyszlewskiR.edu.scientling.service.net.login.LoginRequest;
import com.dyszlewskiR.edu.scientling.service.net.login.LoginResponse;
import com.dyszlewskiR.edu.scientling.service.net.connection.ConnectivityUtils;
import com.dyszlewskiR.edu.scientling.utils.MD5;

import org.json.JSONException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private TextView mErrorTextView;
    private TextView mInfoTextView;
    private EditText mLoginEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private TextView mNotRememberTextView;
    private TextView mRegisterTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupControls();
        setListeners();
    }

    private void setupControls() {
        mLoginEditText = (EditText) findViewById(R.id.login_edit_text);
        mInfoTextView = (TextView) findViewById(R.id.info_text_view);
        mErrorTextView = (TextView) findViewById(R.id.error_text_view);
        mPasswordEditText = (EditText) findViewById(R.id.password_edit_text);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mNotRememberTextView = (TextView) findViewById(R.id.not_remember_button);
        mRegisterTextView = (TextView) findViewById(R.id.register_button);
    }

    private void setListeners() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityUtils.isConnected(getBaseContext())) {
                    setLoggingControls(true, false, getString(R.string.wait_logging));
                    try {
                        startLoginAsyncTask();
                    } catch (NoSuchAlgorithmException e) {
                        Toast.makeText(getBaseContext(), getString(R.string.connection_error), Toast.LENGTH_LONG); //TODO tutaj zmienić komunikat
                    }
                } else {
                    setLoggingControls(false, true, getString(R.string.log_no_net));
                }
            }
        });

        mRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegistrationActivity();
            }
        });

        mNotRememberTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setLoggingControls(boolean isLogging, boolean isError, String infoTextView) {
        if (isLogging) {
            mInfoTextView.setVisibility(View.VISIBLE);
            mInfoTextView.setText(infoTextView);
            mErrorTextView.setVisibility(View.GONE);
            mLoginButton.setEnabled(false);
        } else if (isError) {
            mInfoTextView.setVisibility(View.GONE);
            mErrorTextView.setVisibility(View.VISIBLE);
            mErrorTextView.setText(infoTextView);
            mLoginButton.setEnabled(true);
        } else {
            mInfoTextView.setVisibility(View.GONE);
            mErrorTextView.setVisibility(View.GONE);
            mLoginButton.setEnabled(true);
        }

    }

    private void startLoginAsyncTask() throws NoSuchAlgorithmException {
        Login.LoginCallback loginCallback = new Login.LoginCallback() {
            @Override
            public void onSuccessLogin(String login) {
                Intent intent = new Intent();
                intent.putExtra("login", login);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

            @Override
            public void onIncorrectData() {
                setLoggingControls(false, true, getString(R.string.bad_login));

            }

            @Override
            public void onError() {
                setLoggingControls(false, true, getString(R.string.logging_error));
            }
        };
        LoginParams params = new LoginParams(mLoginEditText.getText().toString(), mPasswordEditText.getText().toString());

        Login.login(params, loginCallback);
    }

    private void startRegistrationActivity() {
        Intent intent = new Intent(getBaseContext(), RegistrationActivity.class);
        startActivity(intent);
    }
}


