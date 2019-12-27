package br.com.echitey.android.firebaseauthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import br.com.echitey.android.firebaseauthapp.utils.Utils;

public class RecoverPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private LinearLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        edtEmail = findViewById(R.id.edtLoginEmail);
        loadingLayout = findViewById(R.id.loadingLayout);
    }

    public void recover(View view) {
        String email = edtEmail.getText().toString().trim();
        if(Utils.isEmailValid(email)){

            Utils.dismissKeyboard(this);
            loadingLayout.setVisibility(View.VISIBLE);
            sendEmail(email);

        }else{
            Toast.makeText(this, R.string.ALERT_EMAIL_VALID, Toast.LENGTH_SHORT).show();
        }
    }

    public void cancel(View view) {
        finish();
    }

    private void sendEmail(String email){
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RecoverPasswordActivity.this, "PASSWORD RECOVERING LINK SENT TO EMAIL", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            loadingLayout.setVisibility(View.GONE);
                            Toast.makeText(RecoverPasswordActivity.this, "ERROR WHILE SENDING PASSWORD RECOVERING LINK", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public void nothing(View view) {
    }
}
