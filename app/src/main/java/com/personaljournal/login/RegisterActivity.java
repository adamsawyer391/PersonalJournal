package com.personaljournal.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.personaljournal.R;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    Context mContext;
    private EditText etEmail, etPassword, etPasswordConfirm;
    private Button button_register;
    private String mEmail, mPassword, mPasswordConfirm;

    FirebaseAuth mAuth;
    String currentUserID;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = RegisterActivity.this;
        mAuth = FirebaseAuth.getInstance();

        deployWidgets();
    }

    private void deployWidgets(){
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        button_register = findViewById(R.id.buttonRegister);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInputs();
            }
        });
    }

    private void checkInputs(){
        mEmail = etEmail.getText().toString();
        mPassword = etPassword.getText().toString();
        mPasswordConfirm = etPasswordConfirm.getText().toString();

        if (TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword) || TextUtils.isEmpty(mPasswordConfirm)){
            Toasty.warning(mContext, "Please fill out all fields", Toasty.LENGTH_LONG).show();
        }
        else if (!mPassword.equals(mPasswordConfirm)){
            Toasty.warning(mContext, "Your passwords do not match. Please try again", Toasty.LENGTH_LONG).show();
        }
        else if (mPassword.length() < 6){
            Toasty.warning(mContext, "Your password length is too short", Toasty.LENGTH_LONG).show();
        }
        else{
            checkEmailAvailability(mEmail, mPassword);
        }
    }

    private void checkEmailAvailability(final String email, final String password){
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.getResult().getSignInMethods().size() == 1){
                    Toasty.warning(mContext, "That email is already in use. Please select another", Toast.LENGTH_LONG).show();
                }else{
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                registerNewUser(email);
                            }

                        }
                    });
                }
            }
        });
    }

    private void registerNewUser(final String email){
        FirebaseUser user = mAuth.getCurrentUser();
        currentUserID = user.getUid();
        HashMap hashMap = new HashMap();
        hashMap.put("user_id", currentUserID);
        hashMap.put("email", email);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);
        databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(mContext, CompleteSetupActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    Toasty.warning(mContext, "Sometheing went wrong. Please try again", Toasty.LENGTH_LONG).show();
                }
            }
        });
    }
}

















