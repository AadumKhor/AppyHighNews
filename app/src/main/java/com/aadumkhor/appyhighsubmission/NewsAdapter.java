package com.aadumkhor.appyhighsubmission;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.aadumkhor.appyhighsubmission.models.Article;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> articles;
    private Context context;
    private OnItemClickListener onItemClickListener;

    // A menu item view type.
    private static final int MENU_ITEM_VIEW_TYPE = 0;

    // The unified native ad view type.
    private static final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;

    public NewsAdapter(List<Object> articleList, Context context1) {
        this.articles = articleList;
        this.context = context1;
    }

    @Override
    public int getItemViewType(int position) {
        Object recyclerViewItems = articles.get(position);
        if (recyclerViewItems instanceof UnifiedNativeAd) {
            return UNIFIED_NATIVE_AD_VIEW_TYPE;
        }
        return MENU_ITEM_VIEW_TYPE;
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, author, date, time, source, description;
        ImageView image;
        ProgressBar progressBar;
        OnItemClickListener onItemClickListener;

        public NewsViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);

            title = itemView.findViewById(R.id.news_title);
            description = itemView.findViewById(R.id.description);
            author = itemView.findViewById(R.id.author);
            date = itemView.findViewById(R.id.published_at);
            time = itemView.findViewById(R.id.time);
            source = itemView.findViewById(R.id.source);
            image = itemView.findViewById(R.id.item_image);
            progressBar = itemView.findViewById(R.id.image_progress);

            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                View unifiedNativeAdLayout = LayoutInflater.from(context)
                        .inflate(R.layout.ad_unified, parent, false);
                return new UnifiedNativeAdViewHolder(unifiedNativeAdLayout);
            case MENU_ITEM_VIEW_TYPE:
                // go to default;
            default:
                View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
                return new NewsViewHolder(view, onItemClickListener);
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) articles.get(position);
                populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder).getAdView());
                break;
            case MENU_ITEM_VIEW_TYPE:
                final NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
                Article model = (Article) articles.get(position);

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(Utils.getRandomDrawableColor());
                requestOptions.error(Utils.getRandomDrawableColor());
                requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                requestOptions.centerCrop();
                requestOptions.timeout(3000);

                Glide.with(context)
                        .load(model.getUrl())
                        .apply(requestOptions)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                newsViewHolder.progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                newsViewHolder.progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(newsViewHolder.image);

                newsViewHolder.title.setText(model.getTitle());
//                newsViewHolder.time.setText(" \u2022 " + Utils.DateFormat(model.getDate()));
                newsViewHolder.source.setText(model.getSource().getName());
                newsViewHolder.description.setText(model.getDescription());
                newsViewHolder.author.setText(model.getAuthor());
                newsViewHolder.date.setText(model.getDate());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
