package com.vraj.spendwise.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vraj.spendwise.data.local.dao.ExpenseDao
import com.vraj.spendwise.data.local.entity.ExpenseEntity
import com.vraj.spendwise.util.converter.DateConverter

/**
 * The [Room] database for this app.
 */
@Database(
    entities = [ExpenseEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

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