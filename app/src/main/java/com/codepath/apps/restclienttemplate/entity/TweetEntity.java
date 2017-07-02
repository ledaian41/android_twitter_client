package com.codepath.apps.restclienttemplate.entity;

import com.codepath.apps.restclienttemplate.models.User;

import io.realm.RealmObject;

/**
 * Created by An Lee on 7/1/2017.
 */

public class TweetEntity extends RealmObject {
    public Long id;
    public String content;
    public String timeStamp;
    public User user;
    public boolean favorited;
    public String photoUrl;
    public String favoriteCount;
    public String retweetCount;

    public TweetEntity() {
    }

    public TweetEntity(Long id, String content, String timeStamp, User user, boolean favorited, String photoUrl, String favoriteCount, String retweetCount) {
        this.id = id;
        this.content = content;
        this.timeStamp = timeStamp;
        this.user = user;
        this.favorited = favorited;
        this.photoUrl = photoUrl;
        this.favoriteCount = favoriteCount;
        this.retweetCount = retweetCount;
    }
}
