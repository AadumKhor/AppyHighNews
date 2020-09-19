package com.aadumkhor.appyhighsubmission;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<Article> articles;
    private Context context;
    private OnItemClickListener onItemClickListener;

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
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new NewsViewHolder(view, onItemClickListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final NewsViewHolder holder, int position) {
        Article model = articles.get(position);

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
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.image);

        holder.title.setText(model.getTitle());
        holder.time.setText(" \u2022 "+ Utils.DateToTimeFormat(model.getDate()));
        holder.source.setText(model.getSource().getName());
        holder.description.setText(model.getDescription());
        holder.author.setText(model.getAuthor());
        holder.date.setText(model.getDate());

    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
