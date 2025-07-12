package com.example.walletapplication.di;

import android.content.Context;

import com.example.walletapplication.data.local.dao.TransactionDao;
import com.example.walletapplication.data.local.dao.CategoryDao;
import com.example.walletapplication.data.local.database.WalletDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public WalletDatabase provideWalletDatabase(@ApplicationContext Context context) {
        return WalletDatabase.getInstance(context);
    }

    @Provides
    public TransactionDao provideTransactionDao(WalletDatabase database) {
        return database.transactionDao();
    }

    @Provides
    public CategoryDao provideCategoryDao(WalletDatabase database) {
        return database.categoryDao();
    }
} 