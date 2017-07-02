package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;

/**
 * Created by An Lee on 6/28/2017.
 */

public class User extends RealmObject {
    String id;
    String name;
    String profileImageUrl;
    String screenName;

    public User() {
        super();
    }

    public User(String id, String name, String profileImageUrl, String screenName) {
        this.id = id;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.screenName = screenName;
    }

    public User(JSONObject object) {
        super();
        try {
            this.id = object.getString("id_str");
            this.name = object.getString("name");
            this.screenName = object.getString("screen_name");
            this.profileImageUrl = object.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        profileImageUrl = in.readString();
        screenName = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getScreenName() {
        return '@' + screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
}
