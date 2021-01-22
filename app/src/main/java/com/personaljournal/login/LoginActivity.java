package com.personaljournal.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.personaljournal.home.MainActivity;
import com.personaljournal.R;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {
    
    Context mContext;
    TextView register_link, forgot_password;
    EditText etEmail, etPassword;
    Button button_login;
    String mEmail, mPassword;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = LoginActivity.this;
        mAuth = FirebaseAuth.getInstance();

        deployWidgets();
    }

    private void deployWidgets(){
        register_link = findViewById(R.id.register_link);
        forgot_password = findViewById(R.id.forgot_password_link);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        button_login = findViewById(R.id.buttonLogin);

        register_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInputs()){
                    mEmail = etEmail.getText().toString();
                    mPassword = etPassword.getText().toString();
                    mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(mContext, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }else{
                                Toasty.error(mContext, "Something went wrong. Please try again.", Toasty.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    Toasty.error(mContext, "Something went wrong. Please try again.", Toasty.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean checkInputs(){
        mEmail = etEmail.getText().toString();
        mPassword = etPassword.getText().toString();

        if (TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword)){
            Toasty.warning(mContext, "Please fillt out both fields", Toasty.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void registerUser() {
        Intent intent = new Intent(mContext, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}





















