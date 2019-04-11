package com.brainyapps.motolabz.MechanicsView;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.brainyapps.motolabz.R;

public class MechanicReplyReviewActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView onBack;
    private RelativeLayout onPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_reply_review);

        onBack = (ImageView)findViewById(R.id.mechanic_reply_back);
        onBack.setOnClickListener(this);
        onPost = (RelativeLayout) findViewById(R.id.mechanic_reply_btn_post);
        onPost.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mechanic_reply_back:
                onGoBack();
                break;
            case R.id.mechanic_reply_btn_post:
                onGoBack();
                break;
            default:
                break;
        }
    }

    public void onGoBack(){
        super.onBackPressed();
    }
}
