package com.codepath.apps.restclienttemplate.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ItemNoImageTweetBinding;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.viewholder.TweetNoImageViewHolder;
import com.codepath.apps.restclienttemplate.viewholder.TweetViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by An Lee on 6/28/2017.
 */

public class TweetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int NO_IMAGE = 0;
    private static final int NORMAL = 1;
    private ArrayList<Tweet> tweets;
    private final Context context;

    private Listener listener;

    public interface Listener {
        void onLoadMore();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public TweetAdapter(Context context) {
        this.tweets = new ArrayList<>();
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (viewType == NO_IMAGE) {
            return new TweetNoImageViewHolder((ItemNoImageTweetBinding) DataBindingUtil
                    .inflate(layoutInflater, R.layout.item_no_image_tweet, parent, false));
        } else {
            return new TweetViewHolder((ItemTweetBinding) DataBindingUtil
                    .inflate(layoutInflater, R.layout.item_tweet, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Tweet tweet = tweets.get(position);
        if (holder instanceof TweetViewHolder) {
            ((TweetViewHolder) holder).bind(tweet, context);
        } else if (holder instanceof TweetNoImageViewHolder) {
            ((TweetNoImageViewHolder) holder).bind(tweet, context);
        }
        //LoadMore
        if (position == tweets.size() - 1 && listener != null) {
            listener.onLoadMore();
        }
        //On Click an item.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (hasImageAt(position)) {
            return NORMAL;
        }
        return NO_IMAGE;
    }

    private boolean hasImageAt(int position) {
        Tweet tweet = tweets.get(position);
        return tweet.getPhotoUrl() != null;
    }

    public void setData(List<Tweet> data) {
        tweets.clear();
        tweets.addAll(data);
        notifyDataSetChanged();
    }

    public void appendData(List<Tweet> newTweets) {
        int nextPos = tweets.size();
        tweets.addAll(nextPos, newTweets);
        notifyItemRangeChanged(nextPos, newTweets.size());
    }
}
