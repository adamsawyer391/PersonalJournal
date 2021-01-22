package com.personaljournal.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.personaljournal.R;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class EditPostActivity extends AppCompatActivity {

    Context mContext;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String currentUserID = mAuth.getCurrentUser().getUid();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID).child("entries");

    String mExtra, mText, mTitle;
    private ProgressBar progressBar;
    private TextView progressTitle;

    TextView white, black, blue, aqua, teal, maroon, olive, green, lime, yellow, orange, red, fuscia, silver, gray;
    SeekBar seekBar;
    int seekValue;
    private int bgColor;
    private int txColor;

    MaterialIconView close;
    TextView save_changes, title_card, tvClose, tvCloseFont;
    EditText etTitle, journal_entry;
    private ImageView background_color, text_size, text_color, add_image;
    CardView color_select, text_size_select;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        mContext = EditPostActivity.this;

        etTitle = findViewById(R.id.etTitle);
        journal_entry = findViewById(R.id.journal_entry);

        progressBar = findViewById(R.id.progress_circular);
        progressTitle = findViewById(R.id.progress_title);
        progressBar.setVisibility(View.INVISIBLE);
        progressTitle.setVisibility(View.INVISIBLE);

        seekBar = findViewById(R.id.seekbar);
        tvCloseFont = findViewById(R.id.tvCloseFont);

        close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        save_changes = findViewById(R.id.save);
        save_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update post
                updatePost();
            }
        });

        color_select = findViewById(R.id.color_select);
        text_size_select = findViewById(R.id.text_size_select);
        title_card = findViewById(R.id.title_card);
        tvClose = findViewById(R.id.tvClose);
        background_color = findViewById(R.id.background_color);
        text_size = findViewById(R.id.text_size);
        text_color = findViewById(R.id.text_color);
        add_image = findViewById(R.id.add_image);
        background_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color_select.setVisibility(View.VISIBLE);
                changeBackgroundColors();
            }
        });
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toasty.error(mContext, "Adding or deleting photos is not allowed in this activity", Toasty.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
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
                text_size_select.setVisibility(View.GONE);
            }
        });
        text_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color_select.setVisibility(View.VISIBLE);
                changeTextColor();
            }
        });
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color_select.setVisibility(View.INVISIBLE);
            }
        });

        getIntentExtras();
        designateColors();
    }

    private void getIntentExtras(){
        Intent intent = getIntent();
        mExtra = intent.getExtras().get("PostKey").toString();

        loadPost(mExtra);
    }

    private void loadPost(String postKey){
        databaseReference.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String title = dataSnapshot.child("title").getValue().toString();
                String body_text = dataSnapshot.child("text").getValue().toString();
                String textSize = dataSnapshot.child("text_size").getValue().toString();
                String backgroundColor = dataSnapshot.child("bg_color").getValue().toString();
                String textColor = dataSnapshot.child("text_color").getValue().toString();

                etTitle.setText(title);
                journal_entry.setText(body_text);
                etTitle.setTextSize(Integer.parseInt(textSize));
                journal_entry.setTextSize(Integer.parseInt(textSize));
                journal_entry.setBackgroundColor(Color.parseColor("#"+backgroundColor));
                journal_entry.setTextColor(Color.parseColor("#"+textColor));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void designateColors(){
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

    private void updatePost(){
        progressBar.setVisibility(View.VISIBLE);
        progressTitle.setVisibility(View.VISIBLE);

        //final String postKey = databaseReference.push().getKey();

        mTitle = etTitle.getText().toString();

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
        hashMap.put("date_created", date);
        hashMap.put("time", time);
        hashMap.put("day", weekDay);
        hashMap.put("title", mTitle);
        hashMap.put("text", mText);
        hashMap.put("bg_color", finalBgColor);
        hashMap.put("text_color", finalTextColor);
        hashMap.put("text_size", seekValue);
        databaseReference.child(mExtra).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.INVISIBLE);
                    progressTitle.setVisibility(View.INVISIBLE);
                    Toast toast = Toasty.success(mContext, "Your post has been updated!", Toasty.LENGTH_LONG);
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
        /*
        actual ad unit ID
         */
        //mInterstitialAd.setAdUnitId("ca-app-pub-3741814945474848/5123120270");
        //test ad ID
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
                // Code to be executed when an ad request fails.
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
}
