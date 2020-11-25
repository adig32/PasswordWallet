package com.example.passwordwallet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.example.passwordwallet.Models.MyPasswordModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalletActivity extends AppCompatActivity {

    Button btnAdd, btnBack;
    String URL_MY_PASSWORDS = "http://192.168.56.1/passwordwallet/my_passwords.php";
    List<MyPasswordModel> myPasswordList;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        btnAdd = findViewById(R.id.btn_add_password);
        btnBack = findViewById(R.id.btn_back);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = findViewById(R.id.my_passwords);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myPasswordList = new ArrayList<>();

        Intent intent = getIntent();
        String login = intent.getStringExtra("login");
        String passwordHash = intent.getStringExtra("passwordHash");

        loadPasswords(login, passwordHash);

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
                intent2.putExtra("login", login);
                intent2.putExtra("passwordHash", passwordHash);
                startActivity(intent2);
            }
        });
    }

    private void loadPasswords(String login, String passwordHash) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MY_PASSWORDS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);

                            for(int i=0; i<array.length(); i++) {
                                JSONObject myPassword = array.getJSONObject(i);

                                myPasswordList.add(new MyPasswordModel(
                                        myPassword.getString("id"),
                                        myPassword.getString("password"),
                                        myPassword.getString("id_user"),
                                        myPassword.getString("web_address"),
                                        myPassword.getString("description"),
                                        myPassword.getString("login")
                                    ));
                            }
                            MyPasswordAdapter adapter = new MyPasswordAdapter(WalletActivity.this, myPasswordList, passwordHash);
                            recyclerView.setAdapter(adapter);
                            progressBar.setVisibility(View.INVISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_login", login);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}