package com.example.passwordwallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.passwordwallet.SharedPreferences.SharedPreferenceConfig;

import java.time.Instant;

public class HomeActivity extends AppCompatActivity {

    TextView userName, successfulLogin, unsuccessfulLogin;
    Button btnWallet, btnChangePassword, btnLogout;
    private SharedPreferenceConfig sharedPreferenceConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferenceConfig = new SharedPreferenceConfig(getApplicationContext());

        if(!sharedPreferenceConfig.getLoginStatus()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        userName = findViewById(R.id.user_name);
        successfulLogin = findViewById(R.id.successful_date);
        unsuccessfulLogin = findViewById(R.id.unsuccessful_date);
        btnWallet = findViewById(R.id.btn_wallet);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnLogout = findViewById(R.id.btn_logout);

        String login = sharedPreferenceConfig.getLogin();
        String lastUnsuccessfulLogin = sharedPreferenceConfig.getUnsuccessfulDate();
        String lastSuccessfulLogin = sharedPreferenceConfig.getSuccessfulDate();

        userName.setText(login);
        successfulLogin.setText(lastSuccessfulLogin);
        unsuccessfulLogin.setText(lastUnsuccessfulLogin);

        btnWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentWallet = new Intent(HomeActivity.this, WalletActivity.class);
                startActivity(intentWallet);
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChangePassword = new Intent(HomeActivity.this, ChangePasswordActivity.class);
                startActivity(intentChangePassword);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogout = new Intent(HomeActivity.this, MainActivity.class);
                sharedPreferenceConfig.setLoginStatus(false);
                startActivity(intentLogout);
            }
        });
    }
}