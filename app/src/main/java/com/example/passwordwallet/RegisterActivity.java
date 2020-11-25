package com.example.passwordwallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class RegisterActivity extends AppCompatActivity {

    EditText login, password;
    Button btnRegister;
    ProgressBar progressBar;
    RadioButton hashSHA512, hashHMAC;

    String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZqwertyuiopasdfghjklzxcvbnm1234567890";

    String URL_REGISTER = "http://192.168.56.1/passwordwallet/register.php";
    String pepper = "XBvQfSVBuPInwt4dwQgB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        btnRegister = findViewById(R.id.btn_regist);
        progressBar = findViewById(R.id.progress_bar);
        hashSHA512 = findViewById(R.id.hash_sha512);
        hashHMAC = findViewById(R.id.hash_hmac);

        progressBar.setVisibility(View.INVISIBLE);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String getLogin = login.getText().toString().trim();
                String getPassword = password.getText().toString().trim();

                if (!getLogin.isEmpty() && !getPassword.isEmpty())
                    checkHash(getLogin, getPassword);
                else if(getLogin.isEmpty()) {
                    login.setError("Please enter login!");
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else if(getPassword.isEmpty()) {
                    password.setError("Please enter password!");
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else {
                    login.setError("Please enter login!");
                    password.setError("Please enter password!");
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void checkHash(String login, String password) {
        if(hashSHA512.isChecked()) {
            String salt = getSaltString(SALTCHARS);
            String pass = this.password.getText().toString();
            String hash = calculateSHA512(pass+salt+pepper);
            Register(login, hash, salt, "1");
        } else {
            String salt = getSaltString(SALTCHARS);
            String pass = this.password.getText().toString();
            String hash = calculateHMAC(pass, salt+pepper);
            Register(login, hash, salt, "0");
        }
    }

    public static String calculateSHA512(String text)
    {
        try {
            //get an instance of SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            //calculate message digest of the input string - returns byte array
            byte[] messageDigest = md.digest(text.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        }

        // If wrong message digest algorithm was specified
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String calculateHMAC(String text, String key){
        Mac sha512Hmac;
        String result="";
        try {
            final byte[] byteKey = key.getBytes(StandardCharsets.UTF_8);
            sha512Hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, "HmacSHA512");
            sha512Hmac.init(keySpec);
            byte[] macData = sha512Hmac.doFinal(text.getBytes(StandardCharsets.UTF_8));
            result = Base64.getEncoder().encodeToString(macData);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
        }
        return result;
    }


    protected String getSaltString(String SALTCHARS) {
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 20) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    private void Register(String login, String password, String salt, String isHash){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(RegisterActivity.this, "Register completed!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Register error! " + e.toString(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, "Register error! " + error.toString(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("login", login);
                params.put("password_hash", password);
                params.put("salt", salt);
                params.put("isPasswordKeptAsHash", isHash);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}