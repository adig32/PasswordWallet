package com.example.passwordwallet.Helpers;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.passwordwallet.ChangePasswordActivity;
import com.example.passwordwallet.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.passwordwallet.Helpers.CalculateSHA512Helper.calculateSHA512;

public class CheckHashHelper {

    CheckHashHelper checkHash;

    public CheckHashHelper(CheckHashHelper checkHashHelper) {
        checkHash = checkHashHelper;
    }

    public CheckHashHelper() {
    }

    String URL_GET_ARRAY_LENGTH = "http://192.168.56.1/passwordwallet/get_array_length.php";
    String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZqwertyuiopasdfghjklzxcvbnm1234567890";
    String salt, hash;
    String pepper = "XBvQfSVBuPInwt4dwQgB";
    int arrayLength = 4;

    public int checkHash(String login, String password, int isSHA512) {
        int length;
        salt = GetSaltStringHelper.getSaltString(SALTCHARS);

        if(isSHA512 == 1) {
            hash = calculateSHA512(password+salt+pepper);
            length = checkHash.GetArrayLength(login);
        } else {
            hash = CalculateHMACHelper.calculateHMAC(password, salt+pepper);
            length = checkHash.GetArrayLength(login);
        }
        return length;
    }

    public int GetArrayLength(String login) {
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
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                params.put("user_login", login);
                return params;
            }
        };

        return arrayLength;
    }

}
