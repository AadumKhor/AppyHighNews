package com.aadumkhor.appyhighsubmission;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.aadumkhor.appyhighsubmission.models.Article;
import com.aadumkhor.appyhighsubmission.room.ArticleDao;
import com.aadumkhor.appyhighsubmission.room.ArticlesDatabase;

import java.util.List;

public class ArticleRepository {
    private ArticleDao articleDao;
    private LiveData<List<Article>> articles;

    ArticleRepository(Application application) {
        ArticlesDatabase articlesDatabase = ArticlesDatabase.getInstance(application.getApplicationContext());
        articleDao = articlesDatabase.articleDao();
        articles = articleDao.getSavedArticles();
    }

    LiveData<List<Article>> getArticles() {
        return articles;
    }

    void insert(Article article) {
        ArticlesDatabase.databaseWriteExecutor.execute(() -> {
            articleDao.insertArticle(article);
        });
    }
}
