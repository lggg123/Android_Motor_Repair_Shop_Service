package com.brainyapps.motolabz.Utils;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by HappyBear on 12/25/2018.
 */

public interface ApiInterface {
    @POST("api/send_invite_email_to_mechanic")
    @FormUrlEncoded
    Call<String> sendEmail(@Field("shop_id") String shop_id,
                           @Field("mechanic_email") String mechanic_email,
                           @Field("message") String message,
                           @Field("signup_code") String signup_code);
}
