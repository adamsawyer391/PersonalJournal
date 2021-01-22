package com.personaljournal.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.personaljournal.R;

import es.dmoral.toasty.Toasty;

public class ResetPasswordActivity extends AppCompatActivity {

    Context mContext;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String currentUserID = user.getUid();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);

    AdView adView;

    ImageView backArrow;
    EditText etEmail, etPassword, etNewPassword;
    Button button_authenticate, button_reset_password;
    RelativeLayout authenticate_layout, reset_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mContext = ResetPasswordActivity.this;

        adView = findViewById(R.id.adBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        authenticate_layout = findViewById(R.id.authenticate_email);
        reset_layout = findViewById(R.id.password_reset);

        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SettingsActivity.class);
                startActivity(intent);
            }
        });

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.password);
        etNewPassword = findViewById(R.id.new_password);
        button_authenticate = findViewById(R.id.authenticate);
        button_authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateUser();
            }
        });
        button_reset_password = findViewById(R.id.change_password);
        button_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mNewPassword = etNewPassword.getText().toString();
                changePassword(mNewPassword);
            }
        });
    }

    private void changePassword(String newPassword){
        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast toast = Toasty.success(mContext, "Your password is updated!", Toasty.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    private void authenticateUser(){
        String mEmail = etEmail.getText().toString();
        String mPassword = etPassword.getText().toString();
        if (checkInputs(mEmail, mPassword)){
            AuthCredential authCredential = EmailAuthProvider.getCredential(mEmail, mPassword);
            user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        authenticate_layout.setVisibility(View.GONE);
                        reset_layout.setVisibility(View.VISIBLE);
                    }else{
                        Toast toast = Toasty.warning(mContext, "An error occurred. Please try again.", Toasty.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });
        }
    }

    private boolean checkInputs(String email, String password){
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast toast = Toasty.warning(mContext, "Please fill out all fields", Toasty.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }else if (!email.equals(mAuth.getCurrentUser().getEmail())){
            Toast toast = Toasty.warning(mContext, "That is not the email associated with this account", Toasty.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }
        return true;
    }
}
