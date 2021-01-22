package com.personaljournal.login;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.personaljournal.home.MainActivity;
import com.personaljournal.R;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.Calendar;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class CompleteSetupActivity extends AppCompatActivity {

    Context mContext;
    EditText fullname, location;
    MaterialIconView calendar;
    Button finish_button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_setup);
        mContext = CompleteSetupActivity.this;

        fullname = findViewById(R.id.fullname);
        location = findViewById(R.id.location);
        finish_button = findViewById(R.id.buttonFinish);
        finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInputs();
            }
        });
    }

    private void checkInputs() {
        if (TextUtils.isEmpty(fullname.getText().toString()) || TextUtils.isEmpty(location.getText().toString())){
            Toasty.warning(mContext, "Please fill out all fields", Toasty.LENGTH_LONG).show();
        }else{
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String currentUserID = mAuth.getCurrentUser().getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            String mFullname, mLocation;
            mFullname = fullname.getText().toString();
            mLocation = location.getText().toString();

            HashMap hashMap = new HashMap();
            hashMap.put("fullname", mFullname);
            hashMap.put("location", mLocation);

            databaseReference.child("users").child(currentUserID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }else{
                        Toasty.error(mContext, "Something went wrong. Please try again", Toasty.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
