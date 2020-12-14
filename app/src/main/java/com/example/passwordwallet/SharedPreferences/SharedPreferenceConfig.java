package com.example.passwordwallet.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.passwordwallet.R;

import java.util.Date;

public class SharedPreferenceConfig {
    private SharedPreferences sharedPreferences;
    private Context context;

    public SharedPreferenceConfig(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.login_shared_preference), Context.MODE_PRIVATE);
    }

    public void setLoginStatus(boolean status) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getResources().getString(R.string.login_status_shared_preference), status);
        editor.commit();
    }

    public boolean getLoginStatus() {
        boolean status = false;
        status = sharedPreferences.getBoolean(context.getResources().getString(R.string.login_status_shared_preference), false);
        return status;
    }

    public void setSuccessfulDate(String date) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("dateSuccessful", date);
        editor.commit();
    }

    public String getSuccessfulDate() {
        String date = "";
        date = sharedPreferences.getString("dateSuccessful", "");
        return date;
    }

    public void setUnsuccessfulDate(String date) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("dateUnsuccessful", date);
        editor.commit();
    }

    public String getUnsuccessfulDate() {
        String date = "";
        date = sharedPreferences.getString("dateUnsuccessful", "");
        return date;
    }

    public void setPasswordHash(String hash) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("hash", hash);
        editor.commit();
    }

    public String getPasswordHash() {
        String hash = "";
        hash = sharedPreferences.getString("hash", "");
        return hash;
    }

    public void setLogin(String login) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("login", login);
        editor.commit();
    }

    public String getLogin() {
        String login = "";
        login = sharedPreferences.getString("login", "");
        return login;
    }

    public void setBanTime(int time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("time", time);
        editor.commit();
    }

    public int getBanTime() {
        int time = 0;
        time = sharedPreferences.getInt("time", 0);
        return time;
    }
}
