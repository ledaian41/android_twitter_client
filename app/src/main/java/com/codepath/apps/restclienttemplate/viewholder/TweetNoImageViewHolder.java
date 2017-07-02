package com.codepath.apps.restclienttemplate.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.api.RestApplication;
import com.codepath.apps.restclienttemplate.api.RestClient;
import com.codepath.apps.restclienttemplate.databinding.ItemNoImageTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by An Lee on 6/30/2017.
 */

public class TweetNoImageViewHolder extends RecyclerView.ViewHolder {

    public final ItemNoImageTweetBinding binding;
    private Tweet tweet;

    public TweetNoImageViewHolder(ItemNoImageTweetBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Tweet tweet, Context context) {
        this.tweet = tweet;
        binding.setTweet(tweet);
        bindAva(tweet.getUser().getProfileImageUrl(), binding.ivAvatar, context);
        bindFavoriteBtn(tweet);
        setUpFavoriteBtn();
        binding.executePendingBindings();
    }

    private void bindAva(String path, CircleImageView view, Context context) {
        Glide.with(context)
                .load(path)
                .placeholder(R.drawable.progress_animation)
                .into(view);
    }

    private void bindFavoriteBtn(Tweet tweet) {
        if (isFavorited(tweet)) {
            binding.heartBtn.setLiked(true);
        }else {
            binding.heartBtn.setLiked(false);
        }
    }

    private boolean isFavorited(Tweet tweet) {
        return tweet.isFavorited();
    }

    private void setUpFavoriteBtn() {
        binding.heartBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                RestClient client = RestApplication.getRestClient();
                client.favoriteTweet(tweet.getId().toString(), new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        tweet.setFavoriteCount(String.valueOf(Integer.parseInt(tweet.getFavoriteCount()) + 1));
                        binding.setTweet(tweet);
                        binding.executePendingBindings();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        binding.heartBtn.setLiked(false);
                        binding.executePendingBindings();
                    }
                });
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                RestClient client = RestApplication.getRestClient();
                client.unfavorite(tweet.getId().toString(), new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        tweet.setFavoriteCount(String.valueOf(Integer.parseInt(tweet.getFavoriteCount()) - 1));
                        binding.setTweet(tweet);
                        binding.executePendingBindings();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        binding.heartBtn.setLiked(true);
                        binding.executePendingBindings();
                    }
                });
            }
        });
    }
}
