package com.codepath.apps.restclienttemplate.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.api.RestApplication;
import com.codepath.apps.restclienttemplate.api.RestClient;
import com.codepath.apps.restclienttemplate.adapter.TweetAdapter;
import com.codepath.apps.restclienttemplate.dao.TweetDAO;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.NetworkUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import io.realm.Realm;

import static android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL;

public class TimelineActivity extends AppCompatActivity {
    private static final int DEFAULT_PAGE = 1;
    private List<Tweet> tweets;
    private static int page = DEFAULT_PAGE;
    private TweetAdapter tweetAdapter;
    private final TweetDAO tweetDAO = new TweetDAO();
    private SearchView searchView;

    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.pbLoading)
    RotateLoading pbLoading;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.rvTweet)
    RecyclerView rvTweet;
    @BindView(R.id.pbLoadMore)
    RotateLoading pbLoadMore;

    EditText etTweet;

    private interface Listener {
        void onResult(List<Tweet> tweets);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        Realm.init(this);
        setUpView();
        loadTweets();
    }

    private void setUpView() {
        // Set up toolbar.
        toolBar.setTitle(R.string.Twitter);
        setSupportActionBar(toolBar);
        /*---------------------------------------*/
        tweetAdapter = new TweetAdapter(this);
        rvTweet.setAdapter(tweetAdapter);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, VERTICAL);
        rvTweet.setLayoutManager(layoutManager);

        //LoadMore
        tweetAdapter.setListener(new TweetAdapter.Listener() {
            @Override
            public void onLoadMore() {
                loadMore();
            }
        });
        //Refresh
        setUpPullToRefresh();
    }

    private void Logout() {
        RestClient client = RestApplication.getRestClient();
        client.clearAccessToken();
    }

    private void loadTweets() {
        resetPage();
        if (isOnline()) {
            pbLoading.start();
            fetchTweets(new Listener() {
                @Override
                public void onResult(List<Tweet> tweets) {
                    tweetAdapter.setData(tweets);
                }
            });
            handleComplete();
        } else {
            getDataOffline();
            tweetAdapter.setData(tweets);
        }
    }

    private void getDataOffline() {
        tweets = tweetDAO.getAll();
    }

    private void loadMore() {
        if (isOnline()) {
            pbLoadMore.start();
            nextPage();
            fetchTweets(new Listener() {
                @Override
                public void onResult(List<Tweet> tweets) {
                    tweetAdapter.appendData(tweets);
                }
            });
            handleComplete();
        } else {
            handleComplete();
            Toast.makeText(this, "NO INTERNET !!", Toast.LENGTH_LONG).show();
        }

    }

    private void fetchTweets(final Listener listener) {
        RestClient client = RestApplication.getRestClient();
        client.getHomeTimeline(page, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                Log.d("DEBUG", "timeline: " + jsonArray.toString());
                // Load json array into model classes
                tweets = Tweet.fromJson(jsonArray);
                tweetDAO.clearAll();
                tweetDAO.storeTweets(tweets);
                Log.d("DEBUG", "DONE");
                listener.onResult(tweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                Toast.makeText(TimelineActivity.this, "API !! Limited request...", Toast.LENGTH_SHORT).show();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void handleComplete() {
        pbLoading.stop();
        pbLoadMore.stop();
    }

    private void nextPage() {
        page += 1;
    }

    private void resetPage() {
        page = DEFAULT_PAGE;
    }

    private boolean isOnline() {
        return NetworkUtils.isOnline() && NetworkUtils.isNetworkAvailable(this);
    }

    private void setUpPullToRefresh() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTweets();
                swipeRefresh.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
//        setUpSearchView(menu.findItem(R.id.action_search));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_compose) {
            showComposeDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    //Show Sort Dialog
    private void showComposeDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .positiveText("Tweet")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        RestClient client = RestApplication.getRestClient();
                        String content = etTweet.getText().toString();
                        client.postTweet(content, new JsonHttpResponseHandler() {
                            @Override
                            public void onFinish() {
                                super.onFinish();
                                loadTweets();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Toast.makeText(TimelineActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                            }
                        });
                    }
                })
                .customView(R.layout.tweet_dialog, true)
                .cancelable(true)
                .build();
        etTweet = (EditText) dialog.findViewById(R.id.etTweet);
        etTweet.setHint("What's on your mind?");
        dialog.show();
    }
}
