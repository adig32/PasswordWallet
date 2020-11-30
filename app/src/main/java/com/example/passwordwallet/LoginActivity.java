package com.example.passwordwallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.passwordwallet.Database.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class LoginActivity extends AppCompatActivity {

    EditText login, password;
    Button btnLogin;
    ProgressBar progressBar;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.INVISIBLE);

        dbHelper = new DatabaseHelper(this, progressBar);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String getLogin = login.getText().toString().trim();
                String getPassword = password.getText().toString().trim();

                if (!getLogin.isEmpty() && !getPassword.isEmpty())
                    dbHelper.checkHash(getLogin, getPassword);
                else if (getLogin.isEmpty()) {
                    login.setError("Please enter login!");
                    progressBar.setVisibility(View.INVISIBLE);
                } else if (getPassword.isEmpty()) {
                    password.setError("Please enter password!");
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    login.setError("Please enter login!");
                    password.setError("Please enter password!");
                    progressBar.setVisibility(View.INVISIBLE);
                }

                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}