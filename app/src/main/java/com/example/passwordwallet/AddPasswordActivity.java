package com.example.passwordwallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.passwordwallet.Database.DatabaseHelper;
import com.example.passwordwallet.SharedPreferences.SharedPreferenceConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static com.example.passwordwallet.Database.HashHelper.encrypt;

public class AddPasswordActivity extends AppCompatActivity {

    EditText login, password, webAddress, description;
    Button btnAdd;
    ProgressBar progressBar;
    String secretKey, userLogin;
    private SharedPreferenceConfig sharedPreferenceConfig;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);

        sharedPreferenceConfig = new SharedPreferenceConfig(getApplicationContext());

        if(!sharedPreferenceConfig.getLoginStatus()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        webAddress = findViewById(R.id.web_address);
        description = findViewById(R.id.description);

        btnAdd = findViewById(R.id.btn_add);

        progressBar = findViewById(R.id.progress_bar);

        Intent intent = getIntent();
        userLogin = intent.getStringExtra("login");
        secretKey = intent.getStringExtra("passwordHash");

        progressBar.setVisibility(View.INVISIBLE);

        dbHelper = new DatabaseHelper(this, progressBar);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String getPassword = password.getText().toString().trim();

                if (!getPassword.isEmpty()) {
                    String encryptedPassword = encrypt(getPassword, secretKey);
                    dbHelper.AddPassword(login.getText().toString(), encryptedPassword, webAddress.getText().toString(), description.getText().toString(), userLogin, secretKey);
                }
                else {
                    password.setError("Please enter password!");
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}