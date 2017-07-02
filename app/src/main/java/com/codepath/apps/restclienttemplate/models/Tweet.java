package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by An Lee on 6/28/2017.
 */

public class Tweet implements Parcelable {
    Long id;
    String content;
    String timeStamp;
    User user;
    boolean favorited;
    String photoUrl;
    String favoriteCount;
    String retweetCount;

    // Add a constructor that creates an object from the JSON response

    public Tweet(Long id, String content, String timeStamp, User user, boolean favorited, String photoUrl, String favoriteCount, String retweetCount) {
        this.id = id;
        this.content = content;
        this.timeStamp = timeStamp;
        this.user = user;
        this.favorited = favorited;
        this.photoUrl = photoUrl;
        this.favoriteCount = favoriteCount;
        this.retweetCount = retweetCount;
    }

    public Tweet() {
        super();
    }

    public Tweet(JSONObject object) {
        super();

        try {
            this.id = object.getLong("id");
            this.content = object.getString("text");
            this.timeStamp = getRelativeTimeAgo(object.getString("created_at"));
            this.photoUrl = getPhotoUrl(object);
            this.user = new User(object.getJSONObject("user"));
            this.favorited = object.getBoolean("favorited");
            this.favoriteCount = object.getString("favorite_count");
            this.retweetCount = object.getString("retweet_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected Tweet(Parcel in) {
        content = in.readString();
        timeStamp = in.readString();
        favorited = in.readByte() != 0;
        photoUrl = in.readString();
        favoriteCount = in.readString();
        retweetCount = in.readString();
    }


    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel in) {
            return new Tweet(in);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };

    private String getPhotoUrl(JSONObject object) {
        try {
            return object.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url_https");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tweetJson = null;
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = new Tweet(tweetJson);
            tweets.add(tweet);
        }

        return tweets;
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(String favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public String getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(String retweetCount) {
        this.retweetCount = retweetCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeString(timeStamp);
        dest.writeByte((byte) (favorited ? 1 : 0));
        dest.writeString(photoUrl);
        dest.writeString(favoriteCount);
        dest.writeString(retweetCount);
    }
}
