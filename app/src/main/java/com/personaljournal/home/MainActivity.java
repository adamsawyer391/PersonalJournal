package com.personaljournal.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.personaljournal.R;
import com.personaljournal.login.LoginActivity;
import com.personaljournal.utils.SettingsActivity;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Context mContext;

    TextView todays_date;
    RecyclerView recyclerView;
    MaterialIconView menu;
    AdView adView;

    FirebaseAuth mAuth;
    String currentUserID;
    DatabaseReference databaseReference;

    private PostAdapter mAdapter;
    private List<Object> postList = new ArrayList<>();

    HashMap<String, Object> firebaseDefaults;
    private final String LATEST_VERSION = "app_version";
    FirebaseRemoteConfig mRemoteConfig = FirebaseRemoteConfig.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;
        mAuth = FirebaseAuth.getInstance();

        mAdapter = new PostAdapter(getApplicationContext(), postList);

        MobileAds.initialize(this, getString(R.string.ad_mob_application_id));

        adView = findViewById(R.id.adBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        todays_date = findViewById(R.id.todays_date);
        menu = findViewById(R.id.home_menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.home_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.add_new:
                                Intent intent = new Intent(mContext, NewPostActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.settings:
                                Intent intent1 = new Intent(mContext, SettingsActivity.class);
                                startActivity(intent1);
                                break;
                            case R.id.logout:
                                confirmSignout();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE | MMMM dd, yyyy", Locale.getDefault());
        String date = sdf.format(new Date());
        todays_date.setText(date);

        /**
         * check app version against current version in Firebase Remote Config
         */
        firebaseDefaults = new HashMap<>();
        firebaseDefaults.put(LATEST_VERSION, getCurrentVersion());
        mRemoteConfig.setDefaults(firebaseDefaults);
        mRemoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(true).build());
        mRemoteConfig.fetch().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mRemoteConfig.activateFetched();
                    checkForUpdates();
                }
            }
        });

        checkLoginStatus();
        buildPostsList();
    }

    public int getCurrentVersion(){
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void checkForUpdates(){
        int fetchedVersionCode = (int)mRemoteConfig.getDouble(LATEST_VERSION);
        if (getCurrentVersion()<fetchedVersionCode){
            //display dialog box to indicate new update available. redirect to settings activity and include the code to direct to play store:
            /**
             * insert link to vote booth on google play store
             */
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.votedrop");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }else{
            //Toasty.info(mContext, "You have the latest version of the app", Toasty.LENGTH_LONG).show();
        }
    }

    private void buildPostsList(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID).child("entries");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (dataSnapshot.hasChildren()){
                        postList.add(snapshot.getKey());
                        setupRecyclerView(postList);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupRecyclerView(List<Object> postList){
        recyclerView = findViewById(R.id.home_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        recyclerView.setAdapter(mAdapter);
    }

    private void confirmSignout(){
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Sign Out");
        alertDialog.setMessage("Are you sure you want to sign out?");
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth.signOut();
                loginUser();
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

    private void checkLoginStatus(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null){
            loginUser();
        }else{
            currentUserID = mAuth.getCurrentUser().getUid();
        }
    }

    private void loginUser(){
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
