package com.personaljournal.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.personaljournal.R;
import com.personaljournal.home.MainActivity;
import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    Context mContext;
    ImageView backArrow;
    TextView version_number, change_email, reset_password, attribution, current_version, new_version;

    String site = "https://www.flaticon.com/authors/prosymbols";

    HashMap<String, Object> firebaseDefaults;
    private final String LATEST_VERSION = "app_version";
    FirebaseRemoteConfig mRemoteConfig = FirebaseRemoteConfig.getInstance();

    AdView adView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mContext = SettingsActivity.this;

        adView = findViewById(R.id.adBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        current_version = findViewById(R.id.current_version);
        new_version = findViewById(R.id.new_version);

        version_number = findViewById(R.id.version_number);
        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
            }
        });

        change_email = findViewById(R.id.reset_email);
        change_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChangeEmailActivity.class);
                startActivity(intent);
            }
        });
        reset_password = findViewById(R.id.reset_password);
        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        try{
            String version_control = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            version_number.setText(new StringBuilder().append(getString(R.string.current_version)).append(version_control).toString());
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }

        attribution = findViewById(R.id.attribution);
        SpannableString ss = new SpannableString("The Personal Journal logo is used with permission. The logo designer is Prosymbols. You can find their work and license here.");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(site));
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(mContext, R.color.link_blue));
            }
        };
        attribution.setMovementMethod(LinkMovementMethod.getInstance());
        ss.setSpan(clickableSpan, 120, 124, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        attribution.setText(ss);

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
            new_version.setVisibility(View.VISIBLE);
            current_version.setVisibility(View.GONE);
            new_version.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.votedrop");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            new_version.setTextColor(ContextCompat.getColor(mContext, R.color.link_blue));

        }else{
            //Toasty.info(mContext, "You have the latest version of the app", Toasty.LENGTH_LONG).show();
            new_version.setVisibility(View.GONE);
            current_version.setVisibility(View.VISIBLE);
        }
    }

}
