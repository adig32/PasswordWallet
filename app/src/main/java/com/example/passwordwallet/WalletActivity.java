package com.example.passwordwallet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.passwordwallet.Adapters.MyPasswordAdapter;
import com.example.passwordwallet.Database.DatabaseHelper;
import com.example.passwordwallet.Models.MyPasswordModel;
import com.example.passwordwallet.SharedPreferences.SharedPreferenceConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalletActivity extends AppCompatActivity {

    Button btnAdd, btnBack;
    List<MyPasswordModel> myPasswordList;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    DatabaseHelper dbHelper;
    private SharedPreferenceConfig sharedPreferenceConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        sharedPreferenceConfig = new SharedPreferenceConfig(getApplicationContext());

        if(!sharedPreferenceConfig.getLoginStatus()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        btnAdd = findViewById(R.id.btn_add_password);
        btnBack = findViewById(R.id.btn_back);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = findViewById(R.id.my_passwords);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this, progressBar);

        myPasswordList = new ArrayList<>();

        String login = sharedPreferenceConfig.getLogin();
        String passwordHash = sharedPreferenceConfig.getPasswordHash();

        dbHelper.LoadPasswords(login, passwordHash, myPasswordList, recyclerView);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(WalletActivity.this, AddPasswordActivity.class);
                intent2.putExtra("login", login);
                intent2.putExtra("passwordHash", passwordHash);
                startActivity(intent2);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(WalletActivity.this, HomeActivity.class);
                startActivity(intent2);
            }
        });
    }


}