package com.example.walletapplication.di;

import com.example.walletapplication.data.local.dao.TransactionDao;
import com.example.walletapplication.data.local.dao.CategoryDao;
import com.example.walletapplication.data.repository.TransactionRepositoryImpl;
import com.example.walletapplication.data.repository.CategoryRepositoryImpl;
import com.example.walletapplication.domain.repository.TransactionRepository;
import com.example.walletapplication.domain.repository.CategoryRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class RepositoryModule {

    @Provides
    @Singleton
    public Executor provideExecutor() {
        return Executors.newFixedThreadPool(4);
    }

    @Provides
    @Singleton
    public TransactionRepository provideTransactionRepository(
            TransactionDao transactionDao,
            Executor executor
    ) {
        return new TransactionRepositoryImpl(transactionDao, executor);
    }

    @Provides
    @Singleton
    public CategoryRepository provideCategoryRepository(
            CategoryDao categoryDao,
            Executor executor
    ) {
        return new CategoryRepositoryImpl(categoryDao, executor);
    }
} 