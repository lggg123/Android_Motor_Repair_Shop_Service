package com.brainyapps.motolabz.DriversView;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.brainyapps.motolabz.Adapters.CommentRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.Comment;
import com.brainyapps.motolabz.Models.Driver;
import com.brainyapps.motolabz.Models.PostCommunity;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverCommentActivity extends AppCompatActivity implements View.OnClickListener, CommentRecyclerAdapter.OnClickItemListener{

    private ImageView onBack;
    private ImageView sendButton;
    private EditText sendContent;
    private CircleImageView posterAvatar;
    private TextView posterName;
    private TextView postTime;
    private TextView postDescription;
    private ImageView postImage;
    private ImageView likeIcon;
    private TextView likeCount;
    private ImageView dislikeIcon;
    private TextView dislikeCount;
    private TextView commentCount;

    private ArrayList<Comment> commentList = new ArrayList<>();
    private RecyclerView commentItemRecyclerView;
    private CommentRecyclerAdapter commentItemRecyclerAdapter;

    private String vehicleModel = "";
    private String postKey = "";
    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private int like_count = 0;
    private int dislike_count = 0;
    private int comment_count = 0;
    private boolean isLike = false;
    private boolean isDislike = false;
    private ProgressHUD mProgressDialog;

    private void showProgressHUD(String text) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = ProgressHUD.show(this, text, true);
        mProgressDialog.show();
    }

    private void hideProgressHUD() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_comment);

        onBack = (ImageView) findViewById(R.id.driver_main_comment_back);
        onBack.setOnClickListener(this);
        sendButton = (ImageView) findViewById(R.id.driver_comment_send_button);
        sendButton.setOnClickListener(this);
        sendContent = (EditText) findViewById(R.id.driver_comment_add_text);
        posterAvatar = (CircleImageView) findViewById(R.id.driver_comment_poster_avatar);
        posterName = (TextView)findViewById(R.id.driver_comment_poster_name);
        postTime = (TextView)findViewById(R.id.driver_comment_post_time);
        postDescription = (TextView)findViewById(R.id.driver_comment_post_description);
        postImage = (ImageView)findViewById(R.id.driver_comment_post_image);
        likeIcon = (ImageView)findViewById(R.id.driver_comment_post_like_icon);
        likeIcon.setOnClickListener(this);
        likeCount = (TextView)findViewById(R.id.driver_comment_post_like_count);
        dislikeIcon = (ImageView)findViewById(R.id.driver_comment_post_dislike_icon);
        dislikeIcon.setOnClickListener(this);
        dislikeCount = (TextView)findViewById(R.id.driver_comment_post_dislike_count);
        commentCount = (TextView)findViewById(R.id.driver_comment_post_comment_count);

        commentItemRecyclerAdapter = new CommentRecyclerAdapter(commentList);
        commentItemRecyclerView = (RecyclerView) findViewById(R.id.driver_comment_recycler_view);
        commentItemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        commentItemRecyclerView.setAdapter(commentItemRecyclerAdapter);


        if(getIntent().getExtras() != null){
            Intent i = getIntent();
            vehicleModel = i.getStringExtra("ModelName");
            postKey = i.getStringExtra("PostKey");
        }

        Query postInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_POST).child(vehicleModel).child(postKey);
        postInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PostCommunity post = dataSnapshot.getValue(PostCommunity.class);
                postDescription.setText(post.description);
                postTime.setText(Utils.converteTimestamp(post.time));
                like_count = post.likeCount();
                likeCount.setText(String.valueOf(like_count));
                dislike_count = post.unlikeCount();
                dislikeCount.setText(String.valueOf(dislike_count));
                comment_count = post.commentCount();
                commentCount.setText(String.valueOf(comment_count)+" comments");
                if(post.likes.containsKey(myId)){
                    isLike = true;
                    likeIcon.setImageResource(R.drawable.ic_like);
                }else {
                    isLike = false;
                    likeIcon.setImageResource(R.drawable.ic_like_gray);
                }
                if(post.dislikes.containsKey(myId)){
                    isDislike = true;
                    dislikeIcon.setImageResource(R.drawable.ic_unlike);
                }else {
                    isDislike = false;
                    dislikeIcon.setImageResource(R.drawable.ic_unlike_gray);
                }
                Glide.with(getApplication()).load(post.image).into(postImage);
                setUserInfo(post.userID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        refreshList();
    }

    public void setUserInfo(String userId){
        Query userInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(userId);
        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Driver driver = dataSnapshot.getValue(Driver.class);
                posterName.setText(driver.fullName);
                Glide.with(getApplication()).load(driver.photoUrl).into(posterAvatar);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void refreshList(){
        showProgressHUD("");
        Query commentInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_POST).child(vehicleModel).child(postKey).child("comments").orderByChild("time");
        commentInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    commentList.clear();
                    for (DataSnapshot commentItem : dataSnapshot.getChildren()){
                        Comment comment = commentItem.getValue(Comment.class);
                        commentList.add(comment);
                    }
                    commentItemRecyclerAdapter.notifyDataSetChanged();
                }
                hideProgressHUD();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.driver_main_comment_back:
                super.onBackPressed();
                break;
            case R.id.driver_comment_send_button:
                sendComment();
                break;
            case R.id.driver_comment_post_like_icon:
                onClickLikeIcon();
                break;
            case R.id.driver_comment_post_dislike_icon:
                onClickDislikeIcon();
                break;
            default:
                break;
        }
    }

    public void onClickLikeIcon(){
        if(isLike){
            isLike = false;
            likeIcon.setImageResource(R.drawable.ic_like_gray);
            FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_POST).child(vehicleModel).child(postKey).child("likes").child(myId).removeValue();
            like_count = like_count - 1;
            likeCount.setText(String.valueOf(like_count));
        }else {
            isLike = true;
            likeIcon.setImageResource(R.drawable.ic_like);
            FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_POST).child(vehicleModel).child(postKey).child("likes").child(myId).setValue(true);
            like_count = like_count + 1;
            likeCount.setText(String.valueOf(like_count));
            if(isDislike){
                isDislike = false;
                FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_POST).child(vehicleModel).child(postKey).child("dislikes").child(myId).removeValue();
                dislikeIcon.setImageResource(R.drawable.ic_unlike_gray);
                dislike_count = dislike_count - 1;
                dislikeCount.setText(String.valueOf(dislike_count));
            }
        }
    }

    public void onClickDislikeIcon(){
        if(isDislike){
            isDislike = false;
            dislikeIcon.setImageResource(R.drawable.ic_unlike_gray);
            FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_POST).child(vehicleModel).child(postKey).child("dislikes").child(myId).removeValue();
            dislike_count = dislike_count - 1;
            dislikeCount.setText(String.valueOf(dislike_count));
        }else {
            isDislike = true;
            dislikeIcon.setImageResource(R.drawable.ic_unlike);
            FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_POST).child(vehicleModel).child(postKey).child("dislikes").child(myId).setValue(true);
            dislike_count = dislike_count + 1;
            dislikeCount.setText(String.valueOf(dislike_count));
            if(isLike){
                isLike = false;
                FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_POST).child(vehicleModel).child(postKey).child("likes").child(myId).removeValue();
                likeIcon.setImageResource(R.drawable.ic_like_gray);
                like_count = like_count - 1;
                likeCount.setText(String.valueOf(like_count));
            }
        }
    }

    public void sendComment(){
        if(!sendContent.getText().toString().isEmpty()){
            String commentId = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_POST).child(vehicleModel).child("comments").push().getKey();
            final Comment comment = new Comment();
            comment.text = sendContent.getText().toString();
            comment.userID = myId;
            comment.time = System.currentTimeMillis();
            comment.commentID = commentId;
            Map<String, Object> postUpdates = new HashMap<>();
            postUpdates.put("/" + DBInfo.TBL_POST + "/" + vehicleModel + "/" + postKey + "/" + "comments" + "/" + commentId, comment);
            FirebaseDatabase.getInstance().getReference().updateChildren(postUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    hideProgressHUD();
                    comment_count = comment_count + 1;
                    commentCount.setText(String.valueOf(comment_count)+" comments");
                    refreshList();
                }
            });
        }
    }

    @Override
    public void clickReply(int index, String userId, String commentId) {

    }
}
