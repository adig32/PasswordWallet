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

    int arrayLength;

    String salt, hash;

    String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZqwertyuiopasdfghjklzxcvbnm1234567890";

    String URL_UPDATE_PASSWORD = "http://192.168.56.1/passwordwallet/update_password.php";
    String URL_GET_IDS = "http://192.168.56.1/passwordwallet/get_ids.php";
    String URL_GET_ARRAY_LENGTH = "http://192.168.56.1/passwordwallet/get_array_length.php";
    String URL_UPDATE_ROW = "http://192.168.56.1/passwordwallet/update_row.php";
    String pepper = "XBvQfSVBuPInwt4dwQgB";

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

    public int checkHash(String login, String password) {
        int length;
        salt = getSaltString(SALTCHARS);

        if(hashSHA512.isChecked()) {
            hash = calculateSHA512(password+salt+pepper);
            length = GetArrayLength(login, oldHash, this.password.getText().toString(), hash, salt, "1");
        } else {
            hash = calculateHMAC(password, salt+pepper);
            length = GetArrayLength(login, oldHash, this.password.getText().toString(), hash, salt, "0");
        }
        return length;
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPDATE_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ChangePasswordActivity.this, "Update completed!", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ChangePasswordActivity.this, "Update error! " + e.toString(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChangePasswordActivity.this, "Update error! " + error.toString(), Toast.LENGTH_SHORT).show();
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

    private void UpdateRow(String id, String password){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPDATE_ROW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ChangePasswordActivity.this, "Password update completed!", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ChangePasswordActivity.this, "Password update error! " + e.toString(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChangePasswordActivity.this, "Password update error! " + error.toString(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void GetRow(String login, String oldPassword, String newPassword, String salt, String rowNum) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GET_IDS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("passwords");

                            if(success.equals("1")) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject myPassword = jsonArray.getJSONObject(i);

                                    String rowId = myPassword.getString("id");
                                    String rowPassword = myPassword.getString("password");

                                    String rowOldPasswordHashDecrypted = decrypt(rowPassword, oldHash);
                                    String rowNewPasswordHash = encrypt(rowOldPasswordHashDecrypted, newPassword);
                                    UpdateRow(rowId, rowNewPasswordHash);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_login", login);
                params.put("row_num", rowNum);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private int GetArrayLength(String login, String oldPassword, String newPassword, String newPasswordHash, String salt, String isHash) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GET_ARRAY_LENGTH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("passwords");

                            if(success.equals("1")) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject myPassword = jsonArray.getJSONObject(i);

                                    String arrayLengthString = myPassword.getString("NumberOfRows");
                                    arrayLength = Integer.parseInt(arrayLengthString);

                                    Register(login, newPasswordHash, salt, isHash);
                                    int j=0;

                                    while(j<arrayLength) {
                                        GetRow(login, oldPassword, newPassword, salt, String.valueOf(j));
                                        j++;
                                    }
                                }

                                progressBar.setVisibility(View.VISIBLE);

                                startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));

                            }
                        } catch (JSONException e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
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
        return arrayLength;
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

    public String decrypt(String strToDecrypt, String secret) {
        try
        {
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secret.toCharArray(), pepper.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}