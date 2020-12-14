package com.example.passwordwallet.Database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.passwordwallet.Adapters.MyPasswordAdapter;
import com.example.passwordwallet.HomeActivity;
import com.example.passwordwallet.LoginActivity;
import com.example.passwordwallet.Models.MyPasswordModel;
import com.example.passwordwallet.SharedPreferences.SharedPreferenceConfig;
import com.example.passwordwallet.WalletActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper {
    static String  URL_LOGIN = "http://192.168.0.10/passwordwallet/login.php";
    static String URL_IS_HASH = "http://192.168.0.10/passwordwallet/is_hash.php";
    static String URL_REGISTER = "http://192.168.56.1/passwordwallet/register.php";
    static String URL_MY_PASSWORDS = "http://192.168.56.1/passwordwallet/my_passwords.php";
    static String URL_UPDATE_PASSWORD = "http://192.168.56.1/passwordwallet/update_password.php";
    static String URL_GET_IDS = "http://192.168.56.1/passwordwallet/get_ids.php";
    static String URL_GET_ARRAY_LENGTH = "http://192.168.56.1/passwordwallet/get_array_length.php";
    static String URL_UPDATE_ROW = "http://192.168.56.1/passwordwallet/update_row.php";
    static String URL_ADD_PASSWORD = "http://192.168.0.10/passwordwallet/add_password.php";
    static String URL_CHECK_BAN = "http://192.168.0.10/passwordwallet/check.php";
    static String URL_UNBAN = "http://192.168.0.10/passwordwallet/unban.php";
    Context ctx;
    ProgressBar progressBar;
    static String pepper = "XBvQfSVBuPInwt4dwQgB";
    String IP = Utils.getIPAddress(true);

    public DatabaseHelper(Context ctx, ProgressBar progressBar) {
        this.ctx = ctx;
        this.progressBar = progressBar;
    }

    public void Login(String login, String passwordHash, String password) {
        SharedPreferenceConfig sharedPreferenceConfig = new SharedPreferenceConfig(ctx.getApplicationContext());
         StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if (success.equals("1")) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String lastSuccessfulLogin = object.getString("last_successful_login");
                                    String lastUnsuccessfulLogin = object.getString("last_unsuccessful_login");

                                    sharedPreferenceConfig.setLoginStatus(true);
                                    sharedPreferenceConfig.setPasswordHash(password);
                                    sharedPreferenceConfig.setLogin(login);
                                    sharedPreferenceConfig.setSuccessfulDate(lastSuccessfulLogin);
                                    sharedPreferenceConfig.setUnsuccessfulDate(lastUnsuccessfulLogin);

                                    Intent intent = new Intent(ctx, HomeActivity.class);
                                    ctx.startActivity(intent);

                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ctx, "Incorrect password!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(ctx, "Incorrect login!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("login", login);
                params.put("password", passwordHash);
                params.put("IP", IP);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    public void CheckHash(String login, String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_IS_HASH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("is_hash");

                            if (success.equals("1")) {

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String isHash = object.getString("isPasswordKeptAsHash").trim();
                                    String salt = object.getString("salt").trim();

                                    if(isHash.equals("1"))
                                        Login(login, HashHelper.calculateSHA512(password+salt), password);
                                    else
                                        Login(login, HashHelper.calculateHMAC(password, salt), password);

                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ctx, "Incorrect password!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            Toast.makeText(ctx, "Incorrect login!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("login", login);
                params.put("IP", IP);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    public void getIncorrectLogins(String login, String password, Button btnUnban, TextView log1, TextView log2) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CHECK_BAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("logins");

                            if (success.equals("1")) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String permBan = object.getString("permBan");

                                    String numberOfUnsuccessfulLoginsString = object.getString("numberOfUnsuccessfulLogins").trim();
                                    int numberOfUnsuccessfulLogins = Integer.parseInt(numberOfUnsuccessfulLoginsString);

                                    String numberOfUnsuccessfulIPString = object.getString("numberOfUnsuccessfulIP").trim();
                                    int numberOfUnsuccessfulIP = Integer.parseInt(numberOfUnsuccessfulIPString);

                                    if (!permBan.equals("1"))
                                    {
                                        if(numberOfUnsuccessfulLogins == 1 || numberOfUnsuccessfulIP == 1) {
                                            if(numberOfUnsuccessfulLogins == 1) {
                                                log1.setText("Login: You need to wait 5 seconds to login");
                                            }
                                            if(numberOfUnsuccessfulIP == 1) {
                                                log2.setText("IP: You need to wait 5 seconds to login");
                                            }
                                            CheckHash(login, password);
                                        } else if (numberOfUnsuccessfulLogins == 2 || numberOfUnsuccessfulIP == 2) {
                                            if(numberOfUnsuccessfulLogins == 2) {
                                                log1.setText("Login: You need to wait 10 seconds to login");
                                            }
                                            if(numberOfUnsuccessfulIP == 2) {
                                                log2.setText("IP: You need to wait 10 seconds to login");
                                            }
                                            try {
                                                Thread.sleep(5000); //1000 milliseconds is one second.
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            CheckHash(login, password);

                                        } else if (numberOfUnsuccessfulLogins == 3 || numberOfUnsuccessfulIP == 3) {
                                            if(numberOfUnsuccessfulLogins == 3) {
                                                log1.setText("Login: You need to wait 2 minutes to login");
                                            }
                                            if(numberOfUnsuccessfulIP == 3) {
                                                log2.setText("IP: Your IP will be banned next time!");
                                            }
                                            try {
                                                Thread.sleep(10000); //1000 milliseconds is one second.
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            CheckHash(login, password);
                                        } else if (numberOfUnsuccessfulLogins >= 4) {
                                            if(numberOfUnsuccessfulLogins >= 4) {
                                                log1.setText("Login: You need to wait 2 minutes to login");
                                            }
                                            try {
                                                Thread.sleep(120000); //1000 milliseconds is one second.
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            CheckHash(login, password);
                                        } else if (numberOfUnsuccessfulIP >= 4) {
                                            btnUnban.setVisibility(View.VISIBLE);
                                            Toast.makeText(ctx, "You are permanently banned!", Toast.LENGTH_LONG).show();
                                            log2.setText("IP: Your IP is banned!");
                                        } else {
                                            log1.setText("");
                                            log2.setText("");
                                            CheckHash(login, password);
                                        }
                                     } else {
                                        btnUnban.setVisibility(View.VISIBLE);
                                        Toast.makeText(ctx, "You are permanently banned!", Toast.LENGTH_LONG).show();
                                    }

                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ctx, "Checking ban error!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            Toast.makeText(ctx, "Critical  ban error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("login", login);
                params.put("IP", IP);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    public void Register(String login, String password, String salt, String isHash){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ctx, "Register completed!", Toast.LENGTH_SHORT).show();
                                ctx.startActivity(new Intent(ctx, LoginActivity.class));
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ctx, "Login is taken!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ctx, "Register error! " + e.toString(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, "Register error! " + error.toString(), Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    public void LoadPasswords(String login, String passwordHash, List<MyPasswordModel> myPasswordList, RecyclerView recyclerView) {
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
                            MyPasswordAdapter adapter = new MyPasswordAdapter(ctx, myPasswordList, passwordHash);
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

        Volley.newRequestQueue(ctx).add(stringRequest);
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
                                Toast.makeText(ctx, "Password update completed!", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ctx, "Password update error! " + e.toString(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, "Password update error! " + error.toString(), Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    private void GetRow(String login, String oldPassword, String newPassword, String rowNum) {
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

                                    String rowOldPasswordHashDecrypted = HashHelper.decrypt(rowPassword, oldPassword);
                                    String rowNewPasswordHash = HashHelper.encrypt(rowOldPasswordHashDecrypted, newPassword);
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

        Volley.newRequestQueue(ctx).add(stringRequest);
    }

    public void GetArrayLength(String login, String oldPassword, String newPassword, String newPasswordHash, String salt, String isHash) {
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
                                    int arrayLength = Integer.parseInt(arrayLengthString);

                                    UpdatePassword(login, newPasswordHash, salt, isHash);
                                    int j=0;

                                    while(j<arrayLength) {
                                        GetRow(login, oldPassword, newPassword, String.valueOf(j));
                                        j++;
                                    }
                                }
                                progressBar.setVisibility(View.VISIBLE);

                                ctx.startActivity(new Intent(ctx, LoginActivity.class));

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

        Volley.newRequestQueue(ctx).add(stringRequest);
    }

    public void AddPassword(String login, String password, String webAddress, String description, String userLogin, String secretKey) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADD_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ctx, "Added!", Toast.LENGTH_SHORT).show();
                                Intent intent2 = new Intent(ctx, WalletActivity.class);
                                intent2.putExtra("login", userLogin);
                                intent2.putExtra("passwordHash", secretKey);
                                ctx.startActivity(intent2);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ctx, "Adding error! " + e.toString(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, "Adding error! " + error.toString(), Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    public void UpdatePassword(String login, String password, String salt, String isHash){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPDATE_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ctx, "Main password updated!", Toast.LENGTH_SHORT).show();
                                ctx.startActivity(new Intent(ctx, LoginActivity.class));
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ctx, "Can't update!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ctx, "Update error! " + e.toString(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, "Update error! " + error.toString(), Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    public void unbanMe(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UNBAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ctx, "Unban completed!", Toast.LENGTH_SHORT).show();
                                ctx.startActivity(new Intent(ctx, LoginActivity.class));
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ctx, "Can't Unban!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ctx, "Unban error! " + e.toString(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, "Update error! " + error.toString(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("IP", IP);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }
}
