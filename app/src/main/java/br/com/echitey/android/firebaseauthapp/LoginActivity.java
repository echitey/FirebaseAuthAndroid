package br.com.echitey.android.firebaseauthapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void nothing(View view) {
    }

    public void forgotPassword(View view) {
        intent = new Intent(this, RecoverPasswordActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
    }

    public void register(View view) {
        intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}
