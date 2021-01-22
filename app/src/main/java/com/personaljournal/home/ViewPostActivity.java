package com.personaljournal.home;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.personaljournal.R;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class ViewPostActivity extends AppCompatActivity {

    Context mContext;

    CardView card_one, card_two, card_three, card_four;
    ImageView image_one, image_two, image_three, image_four;
    TextView post_title, post_text, post_date;
    MaterialIconView delete, edit;
    RelativeLayout background;
    ArrayList<String> mImages;
    String mKeyOne, mKeyTwo, mKeyThree, mKeyFour, imageKey;
    ImageView backArrow;
    MaterialIconView pdf;

    FirebaseAuth mAuth;
    String currentUserID;
    DatabaseReference databaseReference;
    StorageReference mStorage;
    String mExtra;
    AdView adView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        mContext = ViewPostActivity.this;

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID).child("entries");
        mStorage = FirebaseStorage.getInstance().getReference();

        adView = findViewById(R.id.adBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        card_one = findViewById(R.id.card_one);
        card_two = findViewById(R.id.card_two);
        card_three = findViewById(R.id.card_three);
        card_four = findViewById(R.id.card_four);

        image_one = findViewById(R.id.image_one);
        image_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(mExtra).child("images").child(mKeyOne).child("url").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String url = dataSnapshot.getValue().toString();
                            imageKey = mKeyOne;
                            Intent intent = new Intent(mContext, ViewImageActivity.class);
                            intent.putExtra("PostKey", mExtra);
                            intent.putExtra("URL", url);
                            intent.putExtra("ImageKey", imageKey);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        image_two = findViewById(R.id.image_two);
        image_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(mExtra).child("images").child(mKeyTwo).child("url").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String url = dataSnapshot.getValue().toString();
                            imageKey = mKeyTwo;
                            Intent intent = new Intent(mContext, ViewImageActivity.class);
                            intent.putExtra("PostKey", mExtra);
                            intent.putExtra("URL", url);
                            intent.putExtra("ImageKey", imageKey);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        image_three = findViewById(R.id.image_three);
        image_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(mExtra).child("images").child(mKeyThree).child("url").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String url = dataSnapshot.getValue().toString();
                            imageKey = mKeyThree;
                            Intent intent = new Intent(mContext, ViewImageActivity.class);
                            intent.putExtra("PostKey", mExtra);
                            intent.putExtra("URL", url);
                            intent.putExtra("ImageKey", imageKey);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        image_four = findViewById(R.id.image_four);
        image_four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(mExtra).child("images").child(mKeyFour).child("url").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String url = dataSnapshot.getValue().toString();
                            imageKey = mKeyFour;
                            Intent intent = new Intent(mContext, ViewImageActivity.class);
                            intent.putExtra("PostKey", mExtra);
                            intent.putExtra("URL", url);
                            intent.putExtra("ImageKey", imageKey);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        post_title = findViewById(R.id.post_title);
        post_text = findViewById(R.id.post_text);
        post_date = findViewById(R.id.post_date);
        delete = findViewById(R.id.delete_post);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Delete Entry");
                alertDialog.setMessage("Are you sure you want to delete this journal entry? All of it's information will be permanently deleted.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(currentUserID).child(mExtra);
//                        storageReference.delete();
                        databaseReference.child(mExtra).removeValue();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.setIcon(R.drawable.ic_alert);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });
        edit = findViewById(R.id.edit_post);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditPostActivity.class);
                intent.putExtra("PostKey", mExtra);
                startActivity(intent);
            }
        });
        background = findViewById(R.id.relativeLayout3);

        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
            }
        });
        pdf = findViewById(R.id.email_pdf);
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Email PDF Copy");
                alertDialog.setMessage("Do you wish to email a copy of this entry as a PDF file?");
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setIcon(R.drawable.ic_alert);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SEND EMAIL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(Build.VERSION.SDK_INT> Build.VERSION_CODES.M)
                        {
                            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
                            {
                                String[] parmission={Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                requestPermissions(parmission,1000);
                            }
                            else{
                                saveAsPDF();
                            }
                        }
                        else{
                            saveAsPDF();
                        }
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        getIntentExtra();
        loadData();
    }

    private void saveAsPDF() {
        Document document = new Document();
        String fileName = UUID.randomUUID().toString();
        String path = Environment.getExternalStorageDirectory()+"/"+fileName+".pdf";
        try{
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            String title = post_title.getText().toString();
            String date = post_date.getText().toString();
            String text = post_text.getText().toString();
            document.add(new Paragraph(title));
            document.add(new Paragraph(date));
            document.add(new Paragraph(text));
            Bitmap bitmap1, bitmap2, bitmap3, bitmap4;
            if (image_one.getDrawable() != null){
                BitmapDrawable drawable = (BitmapDrawable) image_one.getDrawable();
                bitmap1 = drawable.getBitmap();
                bitmap1 = Bitmap.createScaledBitmap(bitmap1, 300, 200, false);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Image image = Image.getInstance(byteArray);
                document.add(image);
            }
            if (image_two.getDrawable() != null){
                BitmapDrawable drawable = (BitmapDrawable) image_two.getDrawable();
                bitmap2 = drawable.getBitmap();
                bitmap2 = Bitmap.createScaledBitmap(bitmap2, 300, 200, false);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bytes = stream.toByteArray();
                Image image = Image.getInstance(bytes);
                document.add(image);
            }
            if (image_three.getDrawable() != null){
                BitmapDrawable drawable = (BitmapDrawable) image_three.getDrawable();
                bitmap3 = drawable.getBitmap();
                bitmap3 = Bitmap.createScaledBitmap(bitmap3, 300, 200, false);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap3.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bytes = stream.toByteArray();
                Image image = Image.getInstance(bytes);
                document.add(image);
            }
            if (image_four.getDrawable() != null){
                BitmapDrawable drawable = (BitmapDrawable) image_four.getDrawable();
                bitmap4 = drawable.getBitmap();
                bitmap4 = Bitmap.createScaledBitmap(bitmap4, 300, 200, false);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap4.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bytes = stream.toByteArray();
                Image image = Image.getInstance(bytes);
                document.add(image);
            }
            document.close();
            emailDocument(path);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void emailDocument(String document) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+document));
        intent.setData(Uri.parse("mailto:"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void getIntentExtra(){
        Intent intent = getIntent();
        mExtra = intent.getExtras().get("PostKey").toString();
    }

    private void loadData(){
        databaseReference.child(mExtra).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("title")){
                        String title = dataSnapshot.child("title").getValue().toString();
                        post_title.setText(title);
                    }
                    if (dataSnapshot.hasChild("text")){
                        String text = dataSnapshot.child("text").getValue().toString();
                        post_text.setText(text);
                    }
                    if (dataSnapshot.hasChild("time") && dataSnapshot.hasChild("day") && dataSnapshot.hasChild("date_created")){
                        String time = dataSnapshot.child("time").getValue().toString();
                        String day = dataSnapshot.child("day").getValue().toString();
                        String date_created = dataSnapshot.child("date_created").getValue().toString();
                        post_date.setText(date_created + " " + time + " | " + day);
                    }
                    //get design components
                    if (dataSnapshot.hasChild("text_size")){
                        String textSize = dataSnapshot.child("text_size").getValue().toString();
                        post_text.setTextSize(Integer.parseInt(textSize));
                    }
                    if (dataSnapshot.hasChild("text_color")){
                        String textColor = dataSnapshot.child("text_color").getValue().toString();
                        post_text.setTextColor(Color.parseColor("#"+textColor));
                    }
                    if (dataSnapshot.hasChild("bg_color")){
                        String bgColor = dataSnapshot.child("bg_color").getValue().toString();
                        background.setBackgroundColor(Color.parseColor("#"+bgColor));
                    }
                    displayImages(dataSnapshot);

                }else{
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayImages(DataSnapshot dataSnapshot){
        if (dataSnapshot.hasChild("images")){
            mImages = new ArrayList();
            long count = dataSnapshot.child("images").getChildrenCount();
            for (DataSnapshot snapshot : dataSnapshot.child("images").getChildren()){
                mImages.add(snapshot.getKey());
            }
            if (count == 4){
                mKeyOne = mImages.get(0);
                mKeyTwo = mImages.get(1);
                mKeyThree = mImages.get(2);
                mKeyFour = mImages.get(3);
                card_four.setVisibility(View.VISIBLE);
                card_three.setVisibility(View.VISIBLE);
                card_two.setVisibility(View.VISIBLE);
                card_one.setVisibility(View.VISIBLE);
                if (dataSnapshot.child("images").hasChild(mKeyOne)){
                    String url = dataSnapshot.child("images").child(mKeyOne).child("url").getValue().toString();
                    Glide.with(mContext).load(url).into(image_one);
                }
                if (dataSnapshot.child("images").hasChild(mKeyTwo)){
                    String url = dataSnapshot.child("images").child(mKeyTwo).child("url").getValue().toString();
                    Glide.with(mContext).load(url).into(image_two);
                }
                if (dataSnapshot.child("images").hasChild(mKeyThree)){
                    String url = dataSnapshot.child("images").child(mKeyThree).child("url").getValue().toString();
                    Glide.with(mContext).load(url).into(image_three);
                }
                if (dataSnapshot.child("images").hasChild(mKeyFour)){
                    String url = dataSnapshot.child("images").child(mKeyFour).child("url").getValue().toString();
                    Glide.with(mContext).load(url).into(image_four);
                }
            }
            if (count == 3){
                mKeyOne = mImages.get(0);
                mKeyTwo = mImages.get(1);
                mKeyThree = mImages.get(2);
                card_four.setVisibility(View.GONE);
                card_three.setVisibility(View.VISIBLE);
                card_two.setVisibility(View.VISIBLE);
                card_one.setVisibility(View.VISIBLE);
                if (dataSnapshot.child("images").hasChild(mKeyOne)){
                    String url = dataSnapshot.child("images").child(mKeyOne).child("url").getValue().toString();
                    Glide.with(mContext).load(url).into(image_one);
                }
                if (dataSnapshot.child("images").hasChild(mKeyTwo)){
                    String url = dataSnapshot.child("images").child(mKeyTwo).child("url").getValue().toString();
                    Glide.with(mContext).load(url).into(image_two);
                }
                if (dataSnapshot.child("images").hasChild(mKeyThree)){
                    String url = dataSnapshot.child("images").child(mKeyThree).child("url").getValue().toString();
                    Glide.with(mContext).load(url).into(image_three);
                }
            }
            if (count == 2){
                mKeyOne = mImages.get(0);
                mKeyTwo = mImages.get(1);
                card_four.setVisibility(View.GONE);
                card_three.setVisibility(View.GONE);
                card_two.setVisibility(View.VISIBLE);
                card_one.setVisibility(View.VISIBLE);
                if (dataSnapshot.child("images").hasChild(mKeyOne)){
                    String url = dataSnapshot.child("images").child(mKeyOne).child("url").getValue().toString();
                    Glide.with(mContext).load(url).into(image_one);
                }
                if (dataSnapshot.child("images").hasChild(mKeyTwo)){
                    String url = dataSnapshot.child("images").child(mKeyTwo).child("url").getValue().toString();
                    Glide.with(mContext).load(url).into(image_two);
                }
            }
            if (count == 1){
                mKeyOne = mImages.get(0);
                card_four.setVisibility(View.GONE);
                card_three.setVisibility(View.GONE);
                card_two.setVisibility(View.GONE);
                card_one.setVisibility(View.VISIBLE);
                if (dataSnapshot.child("images").hasChild(mKeyOne)){
                    String url = dataSnapshot.child("images").child(mKeyOne).child("url").getValue().toString();
                    Glide.with(mContext).load(url).into(image_one);
                }
            }
        }else{
            //all card are invisible/gone
            card_four.setVisibility(View.GONE);
            card_three.setVisibility(View.GONE);
            card_two.setVisibility(View.GONE);
            card_one.setVisibility(View.GONE);
        }
    }
}
