package com.aadumkhor.appyhighsubmission;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.aadumkhor.appyhighsubmission.models.Article;

import java.util.List;

public class ArticlesViewModel extends AndroidViewModel {
    private ArticleRepository mRepository;
    private LiveData<List<Article>> mArticles;

    public ArticlesViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ArticleRepository(application);
        mArticles = mRepository.getArticles();
    }

    LiveData<List<Article>> getArticles() {
        return mArticles;
    }

    public void insert(Article article) {
        mRepository.insert(article);
    }
}
