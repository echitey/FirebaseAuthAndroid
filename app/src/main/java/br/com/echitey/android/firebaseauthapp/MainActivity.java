package br.com.echitey.android.firebaseauthapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import br.com.echitey.android.firebaseauthapp.models.User;
import br.com.echitey.android.firebaseauthapp.utils.Constants;
import br.com.echitey.android.firebaseauthapp.utils.MySharedPreferences;

public class MainActivity extends AppCompatActivity {

    private TextView txtUser, txtEmail;
    private FirebaseAuth mAuth;
    private Intent intent;
    private User me;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtUser = findViewById(R.id.txtUser);
        txtEmail = findViewById(R.id.txtEmail);

        mAuth = FirebaseAuth.getInstance();


        // CHECK IF USER CONNECTED
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null){

            intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();

        } else {

            getLocalData();

        }


    }

    private void getLocalData(){
        // CHECK LOCAL USER DATA
        MySharedPreferences pref = new MySharedPreferences(MainActivity.this);
        String userJson = pref.getString(Constants.USER);
        me = gson.fromJson(userJson, User.class);

        txtUser.setText(me.getFullname());
        txtEmail.setText(me.getEmail());
    }

    public void logout(View view) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        doLogout();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        // NOTHING
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    private void deleteLocal(){
        MySharedPreferences pref = new MySharedPreferences(MainActivity.this);
        pref.setString(Constants.USER, "");
    }

    private void doLogout(){

        FirebaseAuth.getInstance().signOut();
        deleteLocal();
        finish();
    }
}
