package com.brainyapps.motolabz.DriversView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Adapters.CommunityPostRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.PostCommunity;
import com.brainyapps.motolabz.Models.VehicleModel;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.SignupActivity;
import com.brainyapps.motolabz.Utils.ImagePicker;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.walnutlabs.android.ProgressHUD;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DriverPostOnCommunityActivity extends AppCompatActivity implements View.OnClickListener, CommunityPostRecyclerAdapter.OnClickItemListener{
    final Context context = this;

    private ImageView onBack;
    private ImageView openPopup;
    private Bitmap bitmap;
    private TextView title;
    private StorageReference storePhoto;

    private ImageView modelImg;
    private TextView modelDescription;

    private Dialog dialog;
    private ImageView uploadedImg;
    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String vehicleModel = "";

    private ArrayList<PostCommunity> postList = new ArrayList<>();
    private RecyclerView postItemRecyclerView;
    private CommunityPostRecyclerAdapter postItemRecyclerAdapter;

    public static final int REQUEST_UPLOAD_COMMUNITY_IMAGE_CONTENT = 2345;

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
        setContentView(R.layout.activity_driver_post_on_community);

        onBack = (ImageView) findViewById(R.id.driver_post_back);
        onBack.setOnClickListener(this);
        openPopup = (ImageView) findViewById(R.id.driver_post_on_community);
        openPopup.setOnClickListener(this);
        title = (TextView)findViewById(R.id.driver_post_community_title);

        modelImg = (ImageView)findViewById(R.id.driver_community_model_image);
        modelDescription = (TextView)findViewById(R.id.driver_community_model_description);


        storePhoto = FirebaseStorage.getInstance().getReference();
        if(getIntent().getExtras() != null){
            Intent i = getIntent();
            vehicleModel = i.getStringExtra("ModelName");
            title.setText(vehicleModel);
        }

        showProgressHUD("");
        Query userInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_MODELS).child(vehicleModel);
        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    VehicleModel model = dataSnapshot.getValue(VehicleModel.class);
                    modelDescription.setText(model.description);
                    if(!model.photoUrl.isEmpty()){
                        Glide.with(getApplication()).load(model.photoUrl).into(modelImg);
                    }
                }
                hideProgressHUD();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });

        postItemRecyclerAdapter = new CommunityPostRecyclerAdapter(postList);
        postItemRecyclerView = (RecyclerView) findViewById(R.id.driver_community_post_recycler_view);
        postItemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        postItemRecyclerView.setAdapter(postItemRecyclerAdapter);
        postItemRecyclerAdapter.setOnClickItemListener(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        refreshList();
    }

    public void refreshList(){
        showProgressHUD("");
        Query userInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_POST).child(vehicleModel);
        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    postList.clear();
                    for (DataSnapshot postItem : dataSnapshot.getChildren()){
                        PostCommunity post = postItem.getValue(PostCommunity.class);
                        if(!post.banned){
                            postList.add(post);
                        }
                    }
                    postItemRecyclerAdapter.notifyDataSetChanged();
                }
                hideProgressHUD();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });
    }

    public void showWebsiteDlg() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dlg_post_community);
        dialog.show();

        final EditText description = (EditText) dialog.findViewById(R.id.driver_main_dispatch_description);
        RelativeLayout btnAddImage = (RelativeLayout) dialog.findViewById(R.id.driver_main_dispatch_btn_add_img);
        RelativeLayout btnSubmit = (RelativeLayout)dialog.findViewById(R.id.driver_post_community_submit);
        uploadedImg = (ImageView)dialog.findViewById(R.id.driver_main_dispatch_image);
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_UPLOAD_COMMUNITY_IMAGE_CONTENT);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(description.getText().toString().isEmpty()){
                    showAlert("Please write post description.");
                }else if(bitmap==null){
                    showAlert("Please upload post image.");
                }else {
                    uploadImage(description.getText().toString());
                    dialog.dismiss();
                }
            }
        });
    }

    public void showAlert(String content){
        AlertFactory.showAlert(this, "", content);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_UPLOAD_COMMUNITY_IMAGE_CONTENT && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            bitmap = ImagePicker.getImageFromResult(this,resultCode,data);
            uploadedImg.setVisibility(View.VISIBLE);
            uploadedImg.setImageBitmap(bitmap);
        }
    }

    public void uploadImage(final String description){
        showProgressHUD("");
        if(bitmap!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] data = stream.toByteArray();
            Long tsLong = System.currentTimeMillis();
            StorageReference filepath = storePhoto.child("attaches").child(myId).child(tsLong + ".jpg");
            filepath.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    String image_url = downloadUri.toString();
                    postCommunity(description, image_url);
                }
            });
        }
    }

    public void postCommunity(String description, String url){
        String postId = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_POST).child(vehicleModel).push().getKey();
        PostCommunity post = new PostCommunity();
        post.userID = myId;
        post.description = description;
        post.key = postId;
        post.category = vehicleModel;
        post.time = System.currentTimeMillis();
        post.image = url;
        Map<String, Object> postUpdates = new HashMap<>();
        postUpdates.put("/" + DBInfo.TBL_POST + "/" + vehicleModel + "/" + postId, post);
        FirebaseDatabase.getInstance().getReference().updateChildren(postUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressHUD();
                refreshList();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.driver_post_back:
                super.onBackPressed();
                break;
            case R.id.driver_post_on_community:
                showWebsiteDlg();
                break;
            default:
                break;
        }
    }

    @Override
    public void clickComment(int index, String key) {
        Intent comment_intent = new Intent(this, DriverCommentActivity.class);
        comment_intent.putExtra("ModelName", vehicleModel);
        comment_intent.putExtra("PostKey", key);
        startActivity(comment_intent);
    }
}
