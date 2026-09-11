package com.unitedpay.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.unitedpay.core.database.dao.TransactionDao
import com.unitedpay.core.database.entity.TransactionEntity
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

/**
 * Encrypted Room Database backed by SQLCipher for fintech-grade local storage.
 */
@Database(
    entities = [TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class UnitedPayDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    companion object {
        private const val DB_NAME = "unitedpay_encrypted.db"

        fun buildEncryptedDatabase(context: Context, passphrase: ByteArray): UnitedPayDatabase {
            val factory = SupportFactory(passphrase)
            return Room.databaseBuilder(context, UnitedPayDatabase::class.java, DB_NAME)
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
