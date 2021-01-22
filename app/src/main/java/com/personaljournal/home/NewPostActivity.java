package com.personaljournal.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.personaljournal.R;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

public class NewPostActivity extends AppCompatActivity {

    private static final int GALLERY_PICK = 1;

    Context mContext;
    Uri contentUri;

    FirebaseAuth mAuth;
    String currentUserID;
    DatabaseReference databaseReference;

    //bottom select bar
    private ImageView background_color, text_size, text_color, add_image;
    private ImageView image_one, image_two, image_three, image_four;

    private ProgressBar progressBar;
    private TextView progressTitle;

    private EditText journal_entry, title;
    private String mText, mTitle;
    private int bgColor;
    private int txColor;

    MaterialIconView close;
    TextView save;

    //cardview and pallet
    CardView color_select, text_size_select;
    TextView white, black, blue, aqua, teal, maroon, olive, green, lime, yellow, orange, red, fuscia, silver, gray;
    TextView tvClose, tvCloseFont, title_card_font, title_card;
    SeekBar seekBar;
    int seekValue;
    String postKey;

    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        mContext = NewPostActivity.this;

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID).child("entries");
        postKey = databaseReference.push().getKey();

        deployWidgets();
        activateWidgets();
    }

    private void deployWidgets(){

        close = findViewById(R.id.close);
        save = findViewById(R.id.save);

        add_image = findViewById(R.id.add_image);
        image_one = findViewById(R.id.image_one);
        image_two = findViewById(R.id.image_two);
        image_three = findViewById(R.id.image_three);
        image_four = findViewById(R.id.image_four);

        background_color = findViewById(R.id.background_color);
        text_size = findViewById(R.id.text_size);
        text_color = findViewById(R.id.text_color);

        progressBar = findViewById(R.id.progress_circular);
        progressTitle = findViewById(R.id.progress_title);

        progressBar.setVisibility(View.INVISIBLE);
        progressTitle.setVisibility(View.INVISIBLE);

        journal_entry = findViewById(R.id.journal_entry);
        title = findViewById(R.id.etTitle);

        text_size_select = findViewById(R.id.text_size_select);
        tvCloseFont = findViewById(R.id.tvCloseFont);
        title_card_font = findViewById(R.id.title_card_font);
        seekBar = findViewById(R.id.seekbar);

        color_select = findViewById(R.id.color_select);

        white = findViewById(R.id.table_white);
        black = findViewById(R.id.table_black);
        aqua = findViewById(R.id.table_aqua);
        teal = findViewById(R.id.table_teal);
        maroon = findViewById(R.id.table_maroon);
        olive = findViewById(R.id.table_olive);
        green = findViewById(R.id.table_green);
        lime = findViewById(R.id.table_lime);
        yellow = findViewById(R.id.table_yellow);
        orange = findViewById(R.id.table_orange);
        red = findViewById(R.id.table_red);
        fuscia = findViewById(R.id.table_fuscia);
        silver = findViewById(R.id.table_silver);
        gray = findViewById(R.id.table_gray);
        blue = findViewById(R.id.table_blue);

        tvClose = findViewById(R.id.tvClose);
        title_card = findViewById(R.id.title_card);
    }

    private void activateWidgets(){

        //open background color select
        background_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color_select.setVisibility(View.VISIBLE);
                changeBackgroundColors();
            }
        });
        //close background color select
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color_select.setVisibility(View.INVISIBLE);
            }
        });
        text_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color_select.setVisibility(View.VISIBLE);
                changeTextColor();
            }
        });
        text_size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_size_select.setVisibility(View.VISIBLE);
                changeFontSize();
            }
        });
        tvCloseFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_size_select.setVisibility(View.INVISIBLE);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setIcon(R.drawable.ic_alert);
                alertDialog.setTitle("Delete Entry");
                alertDialog.setMessage("Do you wish to delete this entry");
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReference.child(postKey).removeValue();
                        seekBar.setProgress(0);
                        Intent intent = new Intent(mContext, MainActivity.class);
                        startActivity(intent);
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
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePost();
            }
        });
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //all four images are full and cannot upload any more
                if (image_four.getDrawable() != null){
                    Toast toast = Toasty.warning(mContext, "You can only upload four images per post", Toasty.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else{
                    selectImageFromGallery();
                }
            }
        });
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK){
            if (resultCode == RESULT_OK && data != null){
                contentUri = data.getData();
                /**
                 * for getting exif data and properly rotating the image to be displayed
                 */
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentUri);
                    int orientation = 0;
                    InputStream inputStream = mContext.getContentResolver().openInputStream(contentUri);
                    if (inputStream != null){
                        ExifInterface exif = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            exif = new ExifInterface(inputStream);
                        }
                        orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        inputStream.close();
                    }
                    Bitmap bmRotated = rotateBitmap(bitmap, orientation);
                    placeBitmap(bmRotated);
                    uploadImageToFirebaseStorage(bmRotated);
                }catch (IOException e){
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int orientation){
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }

    }

    private void placeBitmap(Bitmap bitmap){
        if (image_one.getDrawable() == null){
            Glide.with(mContext).load(bitmap).into(image_one);
//            Toast toast = Toasty.info(mContext, "New Photo Added", Toasty.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
        }else if (image_two.getDrawable() == null){
            Glide.with(mContext).load(bitmap).into(image_two);
//            Toast toast = Toasty.info(mContext, "New Photo Added", Toasty.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
        }else if (image_three.getDrawable() == null){
            Glide.with(mContext).load(bitmap).into(image_three);
//            Toast toast = Toasty.info(mContext, "New Photo Added", Toasty.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
        }else if (image_four.getDrawable() == null){
            Glide.with(mContext).load(bitmap).into(image_four);
//            Toast toast = Toasty.info(mContext, "New Photo Added", Toasty.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
        }
    }

    private void uploadImageToFirebaseStorage(Bitmap bitmap){
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(currentUserID).child(postKey);
        final String fileName = UUID.randomUUID().toString();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageReference.child(fileName).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.error(mContext, "Something went wrong, please try again", Toasty.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        HashMap hashMap = new HashMap();
                        hashMap.put("url", url);
                        hashMap.put("filename", fileName);
                        String key = databaseReference.child(postKey).child("images").push().getKey();
                        databaseReference.child(postKey).child("images").child(key).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                }
                            }
                        });
                    }
                });

            }
        });
    }

    private void savePost(){
        progressBar.setVisibility(View.VISIBLE);
        progressTitle.setVisibility(View.VISIBLE);

        mTitle = title.getText().toString();

        //get text size
        mText = journal_entry.getText().toString();
        if (seekValue < 20){
            seekValue = 20;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String date = sdf.format(new Date());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        String time = simpleDateFormat.format(calendar.getTime());

        SimpleDateFormat dayOfWeek = new SimpleDateFormat("EEEE", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        String weekDay = dayOfWeek.format(c.getTime());

        //get background color
        ColorDrawable drawable = (ColorDrawable) journal_entry.getBackground();
        bgColor = drawable.getColor();
        String finalBgColor = Integer.toHexString(bgColor);

        //get text color
        txColor = journal_entry.getCurrentTextColor();
        String finalTextColor = Integer.toHexString(txColor);

        //store values to Firebase
        HashMap hashMap = new HashMap();
        hashMap.put("post_key", postKey);
        hashMap.put("date_created", date);
        hashMap.put("time", time);
        hashMap.put("day", weekDay);
        hashMap.put("title", mTitle);
        hashMap.put("text", mText);
        hashMap.put("bg_color", finalBgColor);
        hashMap.put("text_color", finalTextColor);
        hashMap.put("text_size", seekValue);
        databaseReference.child(postKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.INVISIBLE);
                    progressTitle.setVisibility(View.INVISIBLE);
                    Toast toast = Toasty.success(mContext, "A new entry is added!", Toasty.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    displayInterstitialAd();
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    progressTitle.setVisibility(View.INVISIBLE);
                    Toasty.warning(mContext, "Something went wrong, please try again.", Toasty.LENGTH_LONG).show();
                }
            }
        });
    }



    private void displayInterstitialAd() {
        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId("ca-app-pub-3741814945474848/1230270439");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                if (mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails
                Toast.makeText(mContext, errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void changeFontSize() {
        seekBar.setProgress(20);
        seekBar.setMax(64);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                journal_entry.setTextSize(seekValue);
            }
        });
    }

    private void changeTextColor() {

        title_card.setText(getString(R.string.select_text_color));

        //change text color
        white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.white);
                journal_entry.setTextColor(color);
                journal_entry.setHintTextColor(color);
                txColor = color;
            }
        });
        black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.black);
                journal_entry.setTextColor(color);
                journal_entry.setHintTextColor(color);
                txColor = color;
            }
        });
        aqua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.aqua);
                journal_entry.setTextColor(color);
                journal_entry.setHintTextColor(color);
                txColor = color;
            }
        });
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.blue);
                journal_entry.setTextColor(color);
                journal_entry.setHintTextColor(color);
            }
        });
        teal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.teal);
                journal_entry.setTextColor(color);
                journal_entry.setHintTextColor(color);
            }
        });
        maroon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.maroon);
                journal_entry.setTextColor(color);
                journal_entry.setHintTextColor(color);
            }
        });
        olive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.olive);
                journal_entry.setTextColor(color);
                journal_entry.setHintTextColor(color);
            }
        });
        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.green);
                journal_entry.setTextColor(color);
                journal_entry.setHintTextColor(color);
            }
        });
        lime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.lime);
                journal_entry.setTextColor(color);
                journal_entry.setHintTextColor(color);
            }
        });
        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.yellow);
                journal_entry.setTextColor(color);
                journal_entry.setHintTextColor(color);
            }
        });
        orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.orange);
                journal_entry.setTextColor(color);
                journal_entry.setHintTextColor(color);
            }
        });
        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.red);
                journal_entry.setTextColor(color);
                journal_entry.setHintTextColor(color);
            }
        });
        silver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.silver);
                journal_entry.setTextColor(color);
                journal_entry.setHintTextColor(color);
            }
        });
        gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.gray);
                journal_entry.setTextColor(color);
                journal_entry.setHintTextColor(color);
            }
        });
        fuscia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.fuscia);
                journal_entry.setTextColor(color);
                journal_entry.setHintTextColor(color);
            }
        });
    }

    private void changeBackgroundColors() {

        title_card.setText(getString(R.string.select_background_color));

        //change background colors
        white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.white);
                journal_entry.setBackgroundColor(color);
                bgColor = color;
            }
        });
        black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.black);
                journal_entry.setBackgroundColor(color);
                bgColor = color;
            }
        });
        aqua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.aqua);
                journal_entry.setBackgroundColor(color);
                bgColor = color;
            }
        });
        teal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.teal);
                journal_entry.setBackgroundColor(color);
            }
        });
        maroon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.maroon);
                journal_entry.setBackgroundColor(color);
            }
        });
        fuscia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.fuscia);
                journal_entry.setBackgroundColor(color);
            }
        });
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.blue);
                journal_entry.setBackgroundColor(color);
            }
        });
        olive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.olive);
                journal_entry.setBackgroundColor(color);
            }
        });
        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.green);
                journal_entry.setBackgroundColor(color);
            }
        });
        lime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.lime);
                journal_entry.setBackgroundColor(color);
            }
        });
        gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.gray);
                journal_entry.setBackgroundColor(color);
            }
        });
        silver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.silver);
                journal_entry.setBackgroundColor(color);
            }
        });
        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.yellow);
                journal_entry.setBackgroundColor(color);
            }
        });
        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.red);
                journal_entry.setBackgroundColor(color);
            }
        });
        orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ContextCompat.getColor(mContext, R.color.orange);
                journal_entry.setBackgroundColor(color);
            }
        });
    }
}
