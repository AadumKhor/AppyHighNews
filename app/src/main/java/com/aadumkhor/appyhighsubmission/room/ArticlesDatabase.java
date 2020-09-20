package com.aadumkhor.appyhighsubmission.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.aadumkhor.appyhighsubmission.models.Article;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = Article.class, exportSchema = false, version = 1)
public abstract class ArticlesDatabase extends RoomDatabase {
    private static final String DB_NAME = "articles_db";
    private static volatile ArticlesDatabase instance;
    private static final int numberOfThreads = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(numberOfThreads);

    // this method is also seen as getDatabase
    public static synchronized ArticlesDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (ArticlesDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), ArticlesDatabase.class, DB_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }

    public abstract ArticleDao articleDao();
}
