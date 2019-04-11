package com.brainyapps.motolabz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutAppActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView btn_back;
    private TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        btn_back = (ImageView)findViewById(R.id.about_app_back);
        btn_back.setOnClickListener(this);

        content = (TextView)findViewById(R.id.privacy_policy_content);
        content.setText(Html.fromHtml(getString(R.string.about_us)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.about_app_back:
                super.onBackPressed();
                break;
            default:
                break;
        }
    }
}
