package com.brainyapps.motolabz.DriversView;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.brainyapps.motolabz.MessageActivity;
import com.brainyapps.motolabz.R;

public class DriverDispatchTeamActivity extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout btnContactShop;
    private ImageView onBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dispatch_team);
        onBack = (ImageView) findViewById(R.id.driver_main_dispatch_back);
        onBack.setOnClickListener(this);
        btnContactShop = (RelativeLayout) findViewById(R.id.driver_main_dispatch_contact_repair_shop);
        btnContactShop.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.driver_main_dispatch_back:
                super.onBackPressed();
                break;
            case R.id.driver_main_dispatch_contact_repair_shop:
                Intent message_intent = new Intent(this, MessageActivity.class);
                startActivity(message_intent);
                break;
            default:
                break;
        }
    }
}
