package com.personaljournal.utils;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.personaljournal.R;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class ChangeEmailActivity extends AppCompatActivity {

    Context mContext;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String currentUserID = mAuth.getCurrentUser().getUid();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);

    RelativeLayout authenticate_layout, change_email_layout;
    EditText etEmail, etConfirmEmail, etNewEmail, etConfirmNewEmail, etPassword;
    Button button_email, button_authenticate;

    AdView adView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        mContext = ChangeEmailActivity.this;

        adView = findViewById(R.id.adBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        authenticate_layout = findViewById(R.id.authenticate_email);
        change_email_layout = findViewById(R.id.change_email);

        etEmail = findViewById(R.id.etEmail);
        etConfirmEmail = findViewById(R.id.etConfirmEmail);
        etPassword = findViewById(R.id.password);

        etNewEmail = findViewById(R.id.etNewEmail);
        etConfirmNewEmail = findViewById(R.id.etConfirmNewEmail);

        button_authenticate = findViewById(R.id.authenticate);
        button_authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = etEmail.getText().toString();
                String mConfirmEmail = etConfirmEmail.getText().toString();
                String mPassword = etPassword.getText().toString();
                if (checkInputs(mEmail, mConfirmEmail, mPassword)){
                    AuthCredential authCredential = EmailAuthProvider.getCredential(mEmail, mPassword);
                    mAuth.getCurrentUser().reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                authenticate_layout.setVisibility(View.GONE);
                                change_email_layout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });

        button_email = findViewById(R.id.button_email);
        button_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mNewEmail = etNewEmail.getText().toString();
                String mConfirmNewEmail = etConfirmNewEmail.getText().toString();
                if (checkOtherInputs(mNewEmail, mConfirmNewEmail)){
                    mAuth.fetchSignInMethodsForEmail(mNewEmail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (task.getResult().getSignInMethods().size() >= 1){
                                Toast toasty = Toasty.warning(mContext, "That email is already in use", Toasty.LENGTH_LONG);
                                toasty.setGravity(Gravity.CENTER, 0, 0);
                                toasty.show();
                                //Toasty.error(mContext, "That email is already in use", Toasty.LENGTH_LONG).show();
                            }else{
                                changeEmail(mNewEmail);
                            }
                        }
                    });

                }

            }
        });
    }

    private boolean checkOtherInputs(String mNewEmail, String mConfirmNewEmail) {
        if (TextUtils.isEmpty(mNewEmail) || TextUtils.isEmpty(mConfirmNewEmail)){
            Toasty.warning(mContext, "Please fill out both fields", Toasty.LENGTH_LONG).show();
            return false;
        }else if (!mNewEmail.equals(mConfirmNewEmail)){
            Toasty.warning(mContext, "Your email and confirm email do not match", Toasty.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean checkInputs(String mEmail, String mConfirmEmail, String mPassword){

        if (TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mConfirmEmail) || TextUtils.isEmpty(mPassword)){
            Toasty.warning(mContext, "Please fill out all fields", Toasty.LENGTH_LONG).show();
            return false;
        }else if (!mEmail.equals(mConfirmEmail)){
            Toasty.warning(mContext, "Your email and confirm email do not match", Toasty.LENGTH_LONG).show();
            return false;
        }
        else if (!mEmail.equals(mAuth.getCurrentUser().getEmail())){
            Toasty.warning(mContext, "That is not the email associated with this account", Toasty.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void changeEmail(final String mNewEmail){
        mAuth.getCurrentUser().updateEmail(mNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    HashMap hashMap = new HashMap();
                    hashMap.put("email", mNewEmail);
                    databaseReference.updateChildren(hashMap);
                    Toast toast = Toasty.success(mContext, "Your email has been successfully updated. You will login in with the new email",
                            Toasty.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
    }
}
