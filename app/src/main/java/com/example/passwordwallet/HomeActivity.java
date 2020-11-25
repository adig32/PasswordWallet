package com.example.passwordwallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    TextView userName;
    Button btnWallet, btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userName = findViewById(R.id.user_name);
        btnWallet = findViewById(R.id.btn_wallet);
        btnChangePassword = findViewById(R.id.btn_change_password);

        Intent intent = getIntent();
        String passwordHash = intent.getStringExtra("passwordHash");
        String login = intent.getStringExtra("login");

        userName.setText(login);

        btnWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentWallet = new Intent(HomeActivity.this, WalletActivity.class);
                intentWallet.putExtra("login", login);
                intentWallet.putExtra("passwordHash", passwordHash);
                startActivity(intentWallet);
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChangePassword = new Intent(HomeActivity.this, ChangePasswordActivity.class);
                intentChangePassword.putExtra("login", login);
                intentChangePassword.putExtra("passwordHash", passwordHash);
                startActivity(intentChangePassword);
            }
        });
    }
}