package com.vraj.spendwise.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The [Room] database for this app.
 */
@Database(
    entities = [],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        private const val DATABASE_NAME = "app-database"

        @Volatile private var instance: AppDatabase? = null

        /**
         * Build and return [RoomDatabase] instance of the app.
         *
         * @param [context] application context
         *
         * @return [AppDatabase] instance
         */
        fun buildDatabase(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: let {
                instance = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                    .build()

                instance!!
            }
        }
    }
}