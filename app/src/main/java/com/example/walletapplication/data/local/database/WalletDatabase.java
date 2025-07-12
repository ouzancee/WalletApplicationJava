package com.example.walletapplication.data.local.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import android.content.Context;

import com.example.walletapplication.data.local.converter.BigDecimalConverter;
import com.example.walletapplication.data.local.converter.DateConverter;
import com.example.walletapplication.data.local.dao.TransactionDao;
import com.example.walletapplication.data.local.dao.CategoryDao;
import com.example.walletapplication.data.local.entity.TransactionEntity;
import com.example.walletapplication.data.local.entity.CategoryEntity;

@Database(
    entities = {TransactionEntity.class, CategoryEntity.class},
    version = 2,
    exportSchema = false
)
@TypeConverters({DateConverter.class, BigDecimalConverter.class})
public abstract class WalletDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "wallet_database";
    private static volatile WalletDatabase INSTANCE;
    
    public abstract TransactionDao transactionDao();
    public abstract CategoryDao categoryDao();
    
    public static WalletDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (WalletDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        WalletDatabase.class,
                        DATABASE_NAME
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
} 