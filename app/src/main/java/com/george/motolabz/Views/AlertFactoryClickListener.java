package com.brainyapps.motolabz.Views;

import android.support.v7.app.AlertDialog;

/**
 * Created by HappyBear on 8/16/2018.
 */

public interface AlertFactoryClickListener {
    void onClickYes(AlertDialog dialog);

    void onClickNo(AlertDialog dialog);

    void onClickDone(AlertDialog dialog);
}
