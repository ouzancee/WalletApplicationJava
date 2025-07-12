package com.example.walletapplication.di;

import android.content.Context;

import com.example.walletapplication.data.repository.BackupRepositoryImpl;
import com.example.walletapplication.data.service.FileStorageService;
import com.example.walletapplication.domain.repository.BackupRepository;
import com.example.walletapplication.domain.repository.CategoryRepository;
import com.example.walletapplication.domain.repository.TransactionRepository;
import com.example.walletapplication.domain.usecase.backup.ExportDataUseCase;
import com.example.walletapplication.domain.usecase.backup.ImportDataUseCase;

import java.util.concurrent.Executor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class BackupModule {

    @Provides
    @Singleton
    public FileStorageService provideFileStorageService(@ApplicationContext Context context, Executor executor) {
        return new FileStorageService(context, executor);
    }

    @Provides
    @Singleton
    public BackupRepository provideBackupRepository(FileStorageService fileStorageService) {
        return new BackupRepositoryImpl(fileStorageService);
    }

    @Provides
    public ExportDataUseCase provideExportDataUseCase(TransactionRepository transactionRepository,
                                                     CategoryRepository categoryRepository,
                                                     @ApplicationContext Context context) {
        return new ExportDataUseCase(transactionRepository, categoryRepository, context);
    }

    @Provides
    public ImportDataUseCase provideImportDataUseCase(TransactionRepository transactionRepository,
                                                     CategoryRepository categoryRepository,
                                                     @ApplicationContext Context context) {
        return new ImportDataUseCase(transactionRepository, categoryRepository, context);
    }
} 