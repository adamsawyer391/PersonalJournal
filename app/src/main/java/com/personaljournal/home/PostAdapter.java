package com.personaljournal.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.personaljournal.R;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Object> mPost;
    private ArrayList<String> mImages = new ArrayList<String>();
    private String mKeyOne, mKeyTwo, mKeyThree, mKeyFour;

    public PostAdapter(Context mContext, List<Object> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_list_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final PostViewHolder postViewHolder = (PostViewHolder) holder;
        final String postKey = mPost.get(position).toString();
        postViewHolder.getTitle(postKey);
        postViewHolder.getText(postKey);
        postViewHolder.getBackgroundColor(postKey);
        postViewHolder.getTextColor(postKey);
        postViewHolder.getTextSize(postKey);
        postViewHolder.getDateAndTime(postKey);
        postViewHolder.displayImages(postKey);
        postViewHolder.post_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewPostActivity.class);
                intent.putExtra("PostKey", postKey);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID).child("entries");

        private TextView post_title, post_date, post_text;
        CardView background;
        RelativeLayout relativeLayout;
        CardView card_one, card_two, card_three, card_four;
        ImageView image_one, image_two_one, image_two_two, image_three_one, image_three_two, image_three_three,
        image_four_one, image_four_two, image_four_three, image_four_four;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.background);
            relativeLayout = itemView.findViewById(R.id.relativeLayout1);
            post_title = itemView.findViewById(R.id.post_title);
            post_date = itemView.findViewById(R.id.post_date);
            post_text = itemView.findViewById(R.id.post_text);

            card_one = itemView.findViewById(R.id.card_one);
            image_one = itemView.findViewById(R.id.image_one);

            card_two = itemView.findViewById(R.id.card_two);
            image_two_one = itemView.findViewById(R.id.image_two_one);
            image_two_two = itemView.findViewById(R.id.image_two_two);

            card_three = itemView.findViewById(R.id.card_three);
            image_three_one = itemView.findViewById(R.id.image_three_one);
            image_three_two = itemView.findViewById(R.id.image_three_two);
            image_three_three = itemView.findViewById(R.id.image_three_three);

            card_four = itemView.findViewById(R.id.card_four);
            image_four_one = itemView.findViewById(R.id.image_four_one);
            image_four_two = itemView.findViewById(R.id.image_four_two);
            image_four_three = itemView.findViewById(R.id.image_four_three);
            image_four_four = itemView.findViewById(R.id.image_four_four);
        }

        private void getTitle(final String postKey){
            databaseReference.child(postKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("title")){
                        String title = dataSnapshot.child("title").getValue().toString();
                        post_title.setText(title);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void getText(final String postKey){
            databaseReference.child(postKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("text")){
                        String text = dataSnapshot.child("text").getValue().toString();
                        post_text.setText(text);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void getBackgroundColor(final String postKey){
            databaseReference.child(postKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("bg_color")){
                        String color = dataSnapshot.child("bg_color").getValue().toString();
                        relativeLayout.setBackgroundColor(Color.parseColor("#"+color));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void getTextColor(final String postKey){
            databaseReference.child(postKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("text_color")){
                        String color = dataSnapshot.child("text_color").getValue().toString();
                        post_text.setTextColor(Color.parseColor("#"+color));
                        post_date.setTextColor(Color.parseColor("#"+color));
                        post_title.setTextColor(Color.parseColor("#"+color));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void getTextSize(final String postKey){
            databaseReference.child(postKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("text_size")){
                        String size = dataSnapshot.child("text_size").getValue().toString();
                        post_text.setTextSize(Integer.parseInt(size));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void getDateAndTime(final String postKey){
            databaseReference.child(postKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("date_created")){
                        String dateAndTime = dataSnapshot.child("date_created").getValue().toString();
                        if (dataSnapshot.hasChild("day")){
                            String day = dataSnapshot.child("day").getValue().toString();
                            if (dataSnapshot.hasChild("time")){
                                String time = dataSnapshot.child("time").getValue().toString();
                                post_date.setText(new StringBuilder().append(dateAndTime).append(" ").append(time).append(" - ").append(day).toString());
                            }

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void displayImages(final String postKey){
            databaseReference.child(postKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //images exist for this post
                    if (dataSnapshot.hasChild("images")){
                        long count = dataSnapshot.child("images").getChildrenCount();
                        if (count == 4){
                            mImages.clear();
                            for (DataSnapshot snapshot : dataSnapshot.child("images").getChildren()){
                                mImages.add(snapshot.getKey());
                            }
                            mKeyOne = mImages.get(0);
                            mKeyTwo = mImages.get(1);
                            mKeyThree = mImages.get(2);
                            mKeyFour = mImages.get(3);
                            card_four.setVisibility(View.VISIBLE);
                            card_three.setVisibility(View.GONE);
                            card_two.setVisibility(View.GONE);
                            card_one.setVisibility(View.GONE);
                            String url1 = dataSnapshot.child("images").child(mKeyOne).child("url").getValue().toString();
                            Glide.with(mContext).load(url1).into(image_four_one);
                            String url2 = dataSnapshot.child("images").child(mKeyTwo).child("url").getValue().toString();
                            Glide.with(mContext).load(url2).into(image_four_two);
                            String url3 = dataSnapshot.child("images").child(mKeyThree).child("url").getValue().toString();
                            Glide.with(mContext).load(url3).into(image_four_three);
                            String url4 = dataSnapshot.child("images").child(mKeyFour).child("url").getValue().toString();
                            Glide.with(mContext).load(url4).into(image_four_four);
                        }
                        if (count == 3){
                            mImages.clear();
                            for (DataSnapshot snapshot : dataSnapshot.child("images").getChildren()){
                                mImages.add(snapshot.getKey());
                            }
                            mKeyOne = mImages.get(0);
                            mKeyTwo = mImages.get(1);
                            mKeyThree = mImages.get(2);
                            card_four.setVisibility(View.GONE);
                            card_three.setVisibility(View.VISIBLE);
                            card_two.setVisibility(View.GONE);
                            card_one.setVisibility(View.GONE);
                            String url1 = dataSnapshot.child("images").child(mKeyOne).child("url").getValue().toString();
                            Glide.with(mContext).load(url1).into(image_three_one);
                            String url2 = dataSnapshot.child("images").child(mKeyTwo).child("url").getValue().toString();
                            Glide.with(mContext).load(url2).into(image_three_two);
                            String url3 = dataSnapshot.child("images").child(mKeyThree).child("url").getValue().toString();
                            Glide.with(mContext).load(url3).into(image_three_three);
                        }
                        if (count == 2){
                            mImages.clear();
                            for (DataSnapshot snapshot : dataSnapshot.child("images").getChildren()){
                                mImages.add(snapshot.getKey());
                            }
                            mKeyOne = mImages.get(0);
                            mKeyTwo = mImages.get(1);
                            card_four.setVisibility(View.GONE);
                            card_three.setVisibility(View.GONE);
                            card_two.setVisibility(View.VISIBLE);
                            card_one.setVisibility(View.GONE);
                            String url1 = dataSnapshot.child("images").child(mKeyOne).child("url").getValue().toString();
                            Glide.with(mContext).load(url1).into(image_two_one);
                            String url2 = dataSnapshot.child("images").child(mKeyTwo).child("url").getValue().toString();
                            Glide.with(mContext).load(url2).into(image_two_two);
                        }
                        if (count == 1){
                            mImages.clear();
                            for (DataSnapshot snapshot : dataSnapshot.child("images").getChildren()){
                                mImages.add(snapshot.getKey());
                            }
                            mKeyOne = mImages.get(0);
                            card_four.setVisibility(View.GONE);
                            card_three.setVisibility(View.GONE);
                            card_two.setVisibility(View.GONE);
                            card_one.setVisibility(View.VISIBLE);
                            String url1 = dataSnapshot.child("images").child(mKeyOne).child("url").getValue().toString();
                            Glide.with(mContext).load(url1).into(image_one);
                        }

                    }
                    //images do not exist for this post
                    else{
                        card_one.setVisibility(View.GONE);
                        card_two.setVisibility(View.GONE);
                        card_three.setVisibility(View.GONE);
                        card_four.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

//        private void deletePost(final String postKey, Context context) {
//            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
//            alertDialog.setIcon(R.drawable.ic_alert);
//            alertDialog.setTitle("Delete Post");
//            alertDialog.setMessage("This entry will be permanently deleted. Are you sure you wish to continue?");
//            alertDialog.setCanceledOnTouchOutside(false);
//            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    databaseReference.child(postKey).removeValue();
//                    //notifyDataSetChanged();
//                    dialog.dismiss();
//                }
//            });
//            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            alertDialog.show();
//        }
    }
}
