package br.com.echitey.android.firebaseauthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import br.com.echitey.android.firebaseauthapp.models.User;
import br.com.echitey.android.firebaseauthapp.utils.Constants;
import br.com.echitey.android.firebaseauthapp.utils.MySharedPreferences;
import br.com.echitey.android.firebaseauthapp.utils.Utils;

public class SignupActivity extends AppCompatActivity {

    private EditText edtEmail, edtFullName;
    private TextInputEditText edtPassword;
    private LinearLayout loadingLayout;
    private FirebaseAuth mAuth;
    private Gson gson = new Gson();
    private Intent intent;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edtEmail = findViewById(R.id.edtLoginEmail);
        edtFullName = findViewById(R.id.edtFullName);
        edtPassword = findViewById(R.id.edtLoginPassword);
        loadingLayout = findViewById(R.id.loadingLayout);

        mAuth = FirebaseAuth.getInstance();
    }

    public void nothing(View view) {
    }

    public void register(View view) {

        String _email = edtEmail.getText().toString().trim();
        String _password = edtPassword.getText().toString().trim();
        String _fullName = edtFullName.getText().toString().trim();

        if(isvalid(_email, _password, _fullName)){


            Utils.dismissKeyboard(this);
            loadingLayout.setVisibility(View.VISIBLE);
            signup(_email, _fullName, _password);
        }
    }

    private boolean isvalid(String email, String fullName, String password){

        if(Utils.stringHasMinimumSize(fullName, 3)){

            if(Utils.isEmailValid(email)){

                if(Utils.stringHasMinimumSize(password, 6)){

                    return true;

                }else{
                    Toast.makeText(this, R.string.ALERT_PASSWORD_SIZE, Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(this, R.string.ALERT_EMAIL_VALID, Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, R.string.ALERT_FULLNAME_SIZE, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void signup(final String email, final String fullName, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            FirebaseUser fUser = mAuth.getCurrentUser();

                            //Create User and save Data
                            User user = new User();
                            user.setFullname(fullName);
                            user.setEmail(email);
                            user.setUid(fUser.getUid());
//
                            MySharedPreferences pref = new MySharedPreferences(SignupActivity.this);
                            pref.setString(Constants.USER, gson.toJson(user));

                            createUserData(user);

                        }else {
                            Exception e = task.getException();

                            loadingLayout.setVisibility(View.GONE);

                            if (e instanceof FirebaseAuthWeakPasswordException) {
                                Toast.makeText(SignupActivity.this, "WEAK PASSWORD", Toast.LENGTH_SHORT).show();
                            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(SignupActivity.this, "WRONG PASSWORD", Toast.LENGTH_SHORT).show();
                            } else if (e instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(SignupActivity.this, "WRONG EMAIL", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

    private void createUserData(User user){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.USER_COLLECTION + "/" + user.getUid());
        ref.setValue(user);

        DocumentReference userRef = db.collection(Constants.USER_COLLECTION)
                .document(FirebaseAuth.getInstance().getUid());
        userRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {

                    redirectToHome();

                }
            }
        });

    }

    private void redirectToHome(){

        intent = new Intent(SignupActivity.this, MainActivity .class);
        startActivity(intent);

        finish();

    }
}
