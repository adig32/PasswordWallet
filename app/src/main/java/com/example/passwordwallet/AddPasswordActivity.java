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

public class AddPasswordActivity extends AppCompatActivity {

    EditText login, password, webAddress, description;
    Button btnAdd;
    ProgressBar progressBar;
    String URL_ADD_PASSWORD = "http://192.168.0.10/passwordwallet/add_password.php";
    String pepper = "XBvQfSVBuPInwt4dwQgB";
    String secretKey, userLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);

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

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String getPassword = password.getText().toString().trim();

                if (!getPassword.isEmpty()) {
                    String encryptedPassword = encrypt(getPassword, secretKey);
                    AddPassword(login.getText().toString(), encryptedPassword, webAddress.getText().toString(), description.getText().toString(), userLogin);
                }
                else {
                    password.setError("Please enter password!");
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public String encrypt(String strToEncrypt, String secret)
    {
        try
        {
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secret.toCharArray(), pepper.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    private void AddPassword(String login, String password, String webAddress, String description, String userLogin) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADD_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(AddPasswordActivity.this, "Added!", Toast.LENGTH_SHORT).show();
                                Intent intent2 = new Intent(AddPasswordActivity.this, WalletActivity.class);
                                intent2.putExtra("login", userLogin);
                                intent2.putExtra("passwordHash", secretKey);
                                startActivity(intent2);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddPasswordActivity.this, "Adding error! " + e.toString(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddPasswordActivity.this, "Adding error! " + error.toString(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("login", login);
                params.put("password", password);
                params.put("user_login", userLogin);
                params.put("description", description);
                params.put("web_address", webAddress);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}