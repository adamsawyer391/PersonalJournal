package com.personaljournal.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.personaljournal.R;

import net.steamcrafted.materialiconlib.MaterialIconView;

public class ViewImageActivity extends AppCompatActivity {

    Context mContext;
    ImageView post_image, backArrow;
    MaterialIconView delete_image, rotate_image;

    String urlExtra, postKeyExtra, imageKey;

    FirebaseAuth mAuth;
    String currentUserID;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    AdView adView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        mContext = ViewImageActivity.this;
        post_image = findViewById(R.id.post_image);
        delete_image = findViewById(R.id.delete_image);
        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID).child("entries");
        storageReference = FirebaseStorage.getInstance().getReference().child(currentUserID);

        adView = findViewById(R.id.adBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        rotate_image = findViewById(R.id.rotate_image);
        rotate_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage();
            }
        });

        getIntentExtra();

        delete_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setIcon(R.drawable.ic_alert);
                alertDialog.setTitle("Delete Image");
                alertDialog.setMessage("Are you sure you want to permanently delete this image?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReference.child(postKeyExtra).child("images").child(imageKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //delete from realtime database
                                databaseReference.child(postKeyExtra).child("images").child(imageKey).removeValue();
                                Glide.with(mContext).clear(post_image);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });
    }

    private void rotateImage() {
        post_image.setRotation(post_image.getRotation() + 90);
    }

    private void getIntentExtra(){
        Intent intent = getIntent();
        postKeyExtra = intent.getExtras().get("PostKey").toString();
        urlExtra = intent.getExtras().get("URL").toString();
        imageKey = intent.getExtras().get("ImageKey").toString();
        displayPhoto();
    }

    private void displayPhoto(){
        if (urlExtra != null){
            Glide.with(mContext).load(urlExtra).into(post_image);
        }else{
            Glide.with(mContext).clear(post_image);
        }
    }

}
