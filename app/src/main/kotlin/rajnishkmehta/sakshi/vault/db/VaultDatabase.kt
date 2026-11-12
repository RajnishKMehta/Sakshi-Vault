/*
 * Copyright 2026 Rajnish Kumar
 * SPDX-License-Identifier: Apache-2.0
 */
package rajnishkmehta.sakshi.vault.db

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import kotlinx.coroutines.Dispatchers

/**
 * The Room database that stores all media synchronization metadata and deletion schedules for the Sakshi Vault.
 *
 * Uses Room 3 ([androidx.room3]) with the [AndroidSQLiteDriver].
 */
@Database(entities = [MediaRecord::class], version = 1, exportSchema = true)
abstract class VaultDatabase : RoomDatabase() {

    /** Provides access to [MediaRecord] database operations. */
    abstract fun mediaRecordDao(): MediaRecordDao

    companion object {
        const val DATABASE_NAME = "sakshi_vault_db"

        @Volatile
        private var INSTANCE: VaultDatabase? = null

        /**
         * Retrieves the singleton instance of [VaultDatabase], creating it if necessary.
         *
         * @param context Application context used to build the database.
         * @return The singleton [VaultDatabase] instance.
         */
        fun getDatabase(context: Context): VaultDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VaultDatabase::class.java,
                    DATABASE_NAME
                )
                    .setDriver(AndroidSQLiteDriver())
                    .setQueryCoroutineContext(Dispatchers.IO)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
