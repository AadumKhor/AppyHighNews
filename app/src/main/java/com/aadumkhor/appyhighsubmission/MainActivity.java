package com.aadumkhor.appyhighsubmission;
//**
// https://github.com/firebase/firebase-android-sdk/issues/1662
// The issue with using ad mob is similar to mentioned on this link, refer
// to it before using it in this app.
// **//

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aadumkhor.appyhighsubmission.api.ApiClient;
import com.aadumkhor.appyhighsubmission.api.ApiInterface;
import com.aadumkhor.appyhighsubmission.models.Article;
import com.aadumkhor.appyhighsubmission.models.News;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    // main recycler view
    private RecyclerView recyclerView;

    // pull to refresh
    private SwipeRefreshLayout swipeRefreshLayout;

    // headlines for the news
    private TextView topHeadlines;

    // layout manager for our recycler view
    private RecyclerView.LayoutManager layoutManager;

    // articles and add that fill up the recycler view
    private List<Object> articles = new ArrayList<>();

    // adapter for the recycler view
    private NewsAdapter adapter;

    // for logging in general
    private String TAG = MainActivity.class.getSimpleName();

    // The number of native ads to load and display.
    public static final int NUMBER_OF_ADS = 5;

    // The AdLoader used to load ads.
    private AdLoader adLoader;

    // List of native ads that have been successfully loaded.
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init mobile ads using the key stored in the resources
        MobileAds.initialize(this,
                getString(R.string.admob_app_id));

        // init UI elements and recycler view
        topHeadlines = findViewById(R.id.main_headline);
        topHeadlines.setVisibility(View.INVISIBLE);
        recyclerView = findViewById(R.id.main_recycler);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        swipeRefreshLayout = findViewById(R.id.main_swipe_refresh);

        swipeRefreshLayout.setOnRefreshListener(MainActivity.this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        // invoke method to populate list
        loadJson();
//        loadNativeAds();


//        if (savedInstanceState == null) {
//            // Create new fragment to display a progress spinner while the data set for the
//            // RecyclerView is populated.
//            Fragment loadingScreenFragment = new LoadingScreenFragment();
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.add(R.id.fragment_container, loadingScreenFragment);
//
//            // Commit the transaction.
//            transaction.commit();
//
//            // Update the RecyclerView item's list with menu items.
//            loadJson();
//            // Update the RecyclerView item's list with native ads.
////            loadNativeAds();
//        }
    }

    public void loadJson() {
        swipeRefreshLayout.setRefreshing(true);
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        String country = Utils.getCountry();
        Call<News> call;
        call = apiInterface.getNews(country, ApiClient.NEWS_API_KEY);

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                assert response.body() != null;
                if (response.isSuccessful() && response.body().getArticles() != null) {
                    if (!articles.isEmpty()) {
                        articles.clear();
                    }
                    articles.addAll(response.body().getArticles());
                    adapter = new NewsAdapter(articles, MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    initListener();
                } else {
                    Toast.makeText(MainActivity.this, "No Results!", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
                topHeadlines.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                topHeadlines.setVisibility(View.GONE);
            }
        });
    }

    private void insertAdsInMenuItems() {
        if (mNativeAds.size() <= 0) {
            return;
        }

        int offset = (articles.size() / mNativeAds.size()) + 1;
        int index = 0;
        for (UnifiedNativeAd ad : mNativeAds) {
            articles.add(index, ad);
            index = index + offset;
        }
//        loadMenu();
    }

    private void loadNativeAds() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.admob_app_id));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        mNativeAds.add(unifiedNativeAd);
                        if (!adLoader.isLoading()) {
                            insertAdsInMenuItems();
                            loadMenu();
                        }
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                        if (!adLoader.isLoading()) {
                            insertAdsInMenuItems();
                            loadMenu();
                        }
                    }
                }).build();

        // Load the Native Express ad.
        adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
    }

    private void loadMenu() {
        // Create new fragment and transaction
        Fragment newFragment = new RecyclerViewFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    public List<Object> getRecyclerViewItems() {
        return articles;
    }

    @Override
    public void onRefresh() {
        loadJson();
    }

    private void initListener() {
        adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, NewsDetails.class);

                Article article = (Article) articles.get(position);
                intent.putExtra("url", article.getUrl());

                startActivity(intent);
            }
        });
    }
}