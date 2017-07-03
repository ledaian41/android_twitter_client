package com.codepath.apps.restclienttemplate.dao;

import com.codepath.apps.restclienttemplate.entity.TweetEntity;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by An Lee on 7/1/2017.
 */

public class TweetDAO {
    public void storeTweets(List<Tweet> tweets) {
        final List<TweetEntity> entities = new ArrayList<>();
        for (Tweet tweet : tweets) {
            entities.add(new TweetEntity(
                    tweet.getId(),
                    tweet.getContent(),
                    tweet.getTimeStamp(),
                    tweet.getUser(),
                    tweet.isFavorited(),
                    tweet.getPhotoUrl(),
                    tweet.getFavoriteCount(),
                    tweet.getRetweetCount()));
        }
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm1.copyToRealm(entities));
    }

    public void clearAll() {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<TweetEntity> results = realm.where(TweetEntity.class).findAll();
        realm.executeTransaction(realm1 -> results.deleteAllFromRealm());
    }

    public List<Tweet> getAll() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<TweetEntity> entities = realm.where(TweetEntity.class)
                .findAll();
        List<Tweet> tweets = new ArrayList<>();
        for (TweetEntity entity : entities) {
            tweets.add(new Tweet(
                    entity.id,
                    entity.content,
                    entity.timeStamp,
                    entity.user,
                    entity.favorited,
                    entity.photoUrl,
                    entity.favoriteCount,
                    entity.retweetCount));
        }
        return tweets;
    }
}