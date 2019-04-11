package com.brainyapps.motolabz;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.brainyapps.motolabz.Utils.Utils;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.brainyapps.motolabz.Views.AlertFactoryClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.walnutlabs.android.ProgressHUD;

import java.util.HashMap;
import java.util.Map;

public class SecuritySettingActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView btn_back;
    private EditText old_pwd;
    private EditText new_pwd;
    private EditText confirm_pwd;
    private FirebaseUser user;
    private String myEmail;

    private RelativeLayout change_pwd;
    Context mContext = this;

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
        setContentView(R.layout.activity_security_setting);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myEmail = user.getEmail();
        btn_back = (ImageView)findViewById(R.id.security_settings_back);
        btn_back.setOnClickListener(this);

        old_pwd = (EditText)findViewById(R.id.settings_old_password);
        new_pwd = (EditText)findViewById(R.id.settings_new_password);
        confirm_pwd = (EditText)findViewById(R.id.settings_confirm_password);

        change_pwd = (RelativeLayout)findViewById(R.id.security_setting_save);
        change_pwd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.security_settings_back:
                goBackPage();
                break;
            case R.id.security_setting_save:
                if(checkValidation()){
                    changePassword();
                }
                break;
        }
    }

    public void goBackPage(){
        super.onBackPressed();
    }

    public boolean checkValidation(){
        if(old_pwd.getText().toString().isEmpty()){
            old_pwd.requestFocus();
            AlertFactory.showAlert(this, "", "Please input old password.");
            return false;
        }else if(new_pwd.getText().toString().isEmpty()){
            new_pwd.requestFocus();
            AlertFactory.showAlert(this, "", "Please input new password.");
            return false;
        }else if(confirm_pwd.getText().toString().isEmpty()){
            confirm_pwd.requestFocus();
            AlertFactory.showAlert(this, "", "Please input confirm password.");
            return false;
        }else if(!Utils.overLength(new_pwd.getText().toString()) || !Utils.containsCharacter(new_pwd.getText().toString()) || !Utils.containsNumber(old_pwd.getText().toString())){
            new_pwd.requestFocus();
            new_pwd.setText("");
            AlertFactory.showAlert(this, "", "Password must contains more than 6 letters with at least one character and one number");
            return false;
        }else if(!new_pwd.getText().toString().equals(confirm_pwd.getText().toString())){
            confirm_pwd.requestFocus();
            confirm_pwd.setText("");
            AlertFactory.showAlert(this, "", "Password is not matching! Please try again");
            return false;
        }
        return true;
    }

    private void changePassword(){
        showProgressHUD("");
        AuthCredential credential = EmailAuthProvider
                .getCredential(myEmail, old_pwd.getText().toString());

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(new_pwd.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        hideProgressHUD();
                                        AlertFactory.showAlert(SecuritySettingActivity.this, "", "Password Successfully Changed.", "OKAY", "", new AlertFactoryClickListener() {
                                            @Override
                                            public void onClickYes(AlertDialog dialog) {

                                            }
                                            @Override
                                            public void onClickNo(AlertDialog dialog) {

                                            }
                                            @Override
                                            public void onClickDone(AlertDialog dialog) {
                                                dialog.dismiss();
                                                goBackPage();
                                            }
                                        });
                                    } else {
                                        old_pwd.setText("");
                                        old_pwd.requestFocus();
                                        AlertFactory.showAlert(mContext, "", "Reset Password Failed. Input correct Old Password and try again.");
                                    }
                                    hideProgressHUD();
                                }
                            });
                        }else {
                            old_pwd.setText("");
                            old_pwd.requestFocus();
                            AlertFactory.showAlert(mContext, "", "Reset Password Failed. Input correct Old Password and try again.");
                            hideProgressHUD();
                        }
                    }
                });
    }
}
