package com.brainyapps.motolabz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.DriversView.DriverMainActivity;
import com.brainyapps.motolabz.MechanicsView.MechanicMainActivity;
import com.brainyapps.motolabz.ShopsView.ShopMainActivity;
import com.brainyapps.motolabz.Utils.FirebaseManager;

public class TermandConditionsActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView btn_back;
    private RelativeLayout btnAgree;
    private TextView terms_content;
    private String extra_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termand_conditions);

        btn_back = (ImageView)findViewById(R.id.terms_and_conditions_back);
        btn_back.setOnClickListener(this);

        terms_content = (TextView)findViewById(R.id.terms_content);
        terms_content.setText(Html.fromHtml(getString(R.string.terms_content)));

        btnAgree = (RelativeLayout)findViewById(R.id.terms_and_conditions_agree);
        btnAgree.setOnClickListener(this);

        if(getIntent().getExtras() != null){
            Intent i = getIntent();
            extra_content = i.getStringExtra("terms_and_conditions");
            if(extra_content.equals("from_settings")){
                btn_back.setVisibility(View.VISIBLE);
                btnAgree.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.terms_and_conditions_agree:
                if(FirebaseManager.getInstance().getUserType().equals("customer")){
                    Intent driver_intent = new Intent(this, DriverMainActivity.class);
                    startActivity(driver_intent);
                }else if(FirebaseManager.getInstance().getUserType().equals("mechanic")){
                    Intent mechanic_intent = new Intent(this, MechanicMainActivity.class);
                    startActivity(mechanic_intent);
                }else if(FirebaseManager.getInstance().getUserType().equals("repairshop")){
                    Intent repairshop_intent = new Intent(this, ShopMainActivity.class);
                    startActivity(repairshop_intent);
                }
                break;
            case R.id.terms_and_conditions_back:
                super.onBackPressed();
                break;
            default:
                break;
        }
    }
}
