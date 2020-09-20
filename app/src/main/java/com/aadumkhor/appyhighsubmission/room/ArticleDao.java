package com.aadumkhor.appyhighsubmission.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.aadumkhor.appyhighsubmission.models.Article;

import java.util.List;

@Dao
public interface ArticleDao {
    @Query("SELECT * FROM articles")
    LiveData<List<Article>> getSavedArticles();

    @Insert
    void insertArticle(Article article);

    @Delete
    void deleteArticle(Article article);

    @Query("DELETE FROM articles")
    void deleteAll();
}
