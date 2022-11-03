package com.liteloapps.readwhat.db

import android.content.Context
import android.provider.ContactsContract.Data
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.liteloapps.readwhat.models.Article

@Database(entities = [Article::class], version = 1)
@TypeConverters(Converters::class)
abstract class ArticleDatabase: RoomDatabase() {

    abstract fun getArticleDao() : ArticleDao

    companion object {

        @Volatile
        private var instance : ArticleDatabase? = null

        fun getDatabase (context: Context): ArticleDatabase {
            if(instance == null) {
                synchronized(this) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ArticleDatabase::class.java,
                        "article_db.db"
                    ).build()
                }
            }
            return instance!!
        }

    }
}