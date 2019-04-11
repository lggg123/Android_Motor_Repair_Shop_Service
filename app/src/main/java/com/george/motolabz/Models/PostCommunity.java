package com.brainyapps.motolabz.Models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HappyBear on 8/24/2018.
 */

public class PostCommunity {
    public static final String TABLE_NAME = "users";
    public String userID = "";
    public String category = "";
    public String image = "customer";
    public String key = "";
    public Long time = 0L;
    public String description = "";
    public Boolean banned = false;

    public Map<String, Object> comments = new HashMap<>();
    public Map<String, Boolean> likes = new HashMap<>();
    public Map<String, Boolean> dislikes = new HashMap<>();

    public PostCommunity(){
        userID = "";
        category = "";
        image = "";
        key = "";
        time = 0L;
        description = "";
        banned = false;

        comments = new HashMap<>();
        likes = new HashMap<>();
        dislikes = new HashMap<>();
    }

    public int likeCount(){
        return likes.size();
    }

    public int unlikeCount(){
        return dislikes.size();
    }

    public int commentCount(){
        return comments.size();
    }

}
