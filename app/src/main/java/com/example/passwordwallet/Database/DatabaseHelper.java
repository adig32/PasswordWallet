package com.example.passwordwallet.Database;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ProgressBar;
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
import com.example.passwordwallet.AddPasswordActivity;
import com.example.passwordwallet.ChangePasswordActivity;
import com.example.passwordwallet.HomeActivity;
import com.example.passwordwallet.LoginActivity;
import com.example.passwordwallet.Models.MyPasswordModel;
import com.example.passwordwallet.RegisterActivity;
import com.example.passwordwallet.WalletActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    Context ctx;
    ProgressBar progressBar;
    static String pepper = "XBvQfSVBuPInwt4dwQgB";


    public DatabaseHelper(Context ctx, ProgressBar progressBar) {
        this.ctx = ctx;
        this.progressBar = progressBar;
    }

    public void Login(String login, String passwordHash, String password) {

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

                                    String passwordHash = object.getString("password_hash").trim();

                                    Intent intent = new Intent(ctx, HomeActivity.class);
                                    intent.putExtra("passwordHash", password);
                                    intent.putExtra("login", login);
                                    ctx.startActivity(intent);

                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ctx, "Incorrect login or password!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ctx, "Error " +e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, "Error " +error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("login", login);
                params.put("password", passwordHash);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    public void checkHash(String login, String password) {
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
                                        Login(login, HashHelper.calculateSHA512(password+salt+pepper), password);
                                    else
                                        Login(login, HashHelper.calculateHMAC(password, salt+pepper), password);

                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ctx, "Incorrect login or password!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ctx, "Error " +e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, "Error " +error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("login", login);
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

    public void loadPasswords(String login, String passwordHash, List<MyPasswordModel> myPasswordList, RecyclerView recyclerView) {
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

                                    Register(login, newPasswordHash, salt, isHash);
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
}
