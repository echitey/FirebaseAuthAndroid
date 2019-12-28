package br.com.echitey.android.firebaseauthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import br.com.echitey.android.firebaseauthapp.models.User;
import br.com.echitey.android.firebaseauthapp.utils.Constants;
import br.com.echitey.android.firebaseauthapp.utils.MySharedPreferences;
import br.com.echitey.android.firebaseauthapp.utils.Utils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Intent intent;
    private EditText edtEmail;
    private TextInputEditText edtPassword;
    private Button btnLogin, btnNewAccount, btnForgotPassword;
    private LinearLayout loadingLayout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edtLoginEmail);
        edtPassword = findViewById(R.id.edtLoginPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        btnNewAccount = findViewById(R.id.btnNewAccount);
        btnNewAccount.setOnClickListener(this);

        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnForgotPassword.setOnClickListener(this);

        loadingLayout = findViewById(R.id.loadingLayout);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null){

            intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnLogin:
                login();
                break;
            case R.id.btnNewAccount:
                createNewAccount();
                break;
            case R.id.btnForgotPassword:
                forgotPassword();
                break;

        }
    }

    private void forgotPassword(){
        intent = new Intent(this, RecoverPasswordActivity.class);
        startActivity(intent);
    }

    private void createNewAccount(){
        intent = new Intent(this, SignupActivity.class);
        startActivity(intent);

        finish();
    }

    private void login(){
        //intent = new Intent(this, HomeActivity.class);
        //startActivity(intent);

        String _email = edtEmail.getText().toString().trim();
        String _password = edtPassword.getText().toString().trim();

        if(isValid(_email, _password)){

            Utils.dismissKeyboard(this);
            loadingLayout.setVisibility(View.VISIBLE);
            doLogin(_email, _password);

        }else{
            //SHOW ERROR
        }
    }

    private boolean isValid(String email, String password){
        if(Utils.isEmailValid(email)){

            if(Utils.stringHasMinimumSize(password, 1)){

                return true;

            }else{
                //PASSWORD CANT BE NULL
                Toast.makeText(this, R.string.ALERT_PASSWORD_NULL, Toast.LENGTH_SHORT).show();
                return false;
            }

        }else{
            //ALERT EMAIL NOT VALID
            Toast.makeText(this, R.string.ALERT_EMAIL_VALID, Toast.LENGTH_SHORT).show();
            return false;
        }
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

    private void doLogin(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //GET USER INFO AND SAVE IT TO USER DEFAULT
                            DocumentReference userRef = db.collection(Constants.USER_COLLECTION).document(user.getUid());

                            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){

                                        User me = task.getResult().toObject(User.class);

                                        Gson gson = new Gson();
                                        MySharedPreferences pref = new MySharedPreferences(LoginActivity.this);
                                        pref.setString(Constants.USER, gson.toJson(me, User.class));

                                        intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);

                                        finish();

                                    }
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            loadingLayout.setVisibility(View.GONE);

                            Exception e = task.getException();

                            loadingLayout.setVisibility(View.GONE);

                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginActivity.this, "WRONG PASSWORD", Toast.LENGTH_SHORT).show();
                            } else if (e instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(LoginActivity.this, "WRONG EMAIL", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
    }
}
