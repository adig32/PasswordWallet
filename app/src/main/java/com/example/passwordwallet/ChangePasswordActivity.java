package com.example.passwordwallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.passwordwallet.Database.DatabaseHelper;
import com.example.passwordwallet.Database.HashHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText password;
    Button btnChangePassword;
    ProgressBar progressBar;
    RadioButton hashSHA512, hashHMAC;
    String login, oldHash;
    String salt, hash;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        password = findViewById(R.id.password);
        btnChangePassword = findViewById(R.id.btn_change_password);
        progressBar = findViewById(R.id.progress_bar);
        hashSHA512 = findViewById(R.id.hash_sha512);
        hashHMAC = findViewById(R.id.hash_hmac);

        progressBar.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        login = intent.getStringExtra("login");
        oldHash = intent.getStringExtra("passwordHash");

        dbHelper = new DatabaseHelper(this, progressBar);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String getPassword = password.getText().toString().trim();

                if (!getPassword.isEmpty()) {
                    checkHash(login, getPassword);
                }
                else {
                    password.setError("Please enter password!");
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void checkHash(String login, String password) {
        salt = HashHelper.getSaltString();

        if(hashSHA512.isChecked()) {
            hash = HashHelper.calculateSHA512(password+salt);
            dbHelper.GetArrayLength(login, oldHash, this.password.getText().toString(), hash, salt, "1");
        } else {
            hash = HashHelper.calculateHMAC(password, salt);
            dbHelper.GetArrayLength(login, oldHash, this.password.getText().toString(), hash, salt, "0");
        }
    }
}