package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [FragranceEntity::class, SavedCombinationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class OlfactoryDatabase : RoomDatabase() {

    abstract fun olfactoryDao(): OlfactoryDao

    companion object {
        @Volatile
        private var INSTANCE: OlfactoryDatabase? = null

        fun getDatabase(context: Context): OlfactoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OlfactoryDatabase::class.java,
                    "olfactory_ai_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
