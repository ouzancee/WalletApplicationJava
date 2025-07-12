package com.example.walletapplication.di;

import com.example.walletapplication.domain.repository.TransactionRepository;
import com.example.walletapplication.domain.repository.CategoryRepository;
import com.example.walletapplication.domain.usecase.transaction.AddTransactionUseCase;
import com.example.walletapplication.domain.usecase.transaction.AdvancedSearchUseCase;
import com.example.walletapplication.domain.usecase.transaction.DeleteTransactionUseCase;
import com.example.walletapplication.domain.usecase.category.GetCategoriesUseCase;
import com.example.walletapplication.domain.usecase.report.GetMonthlyReportUseCase;
import com.example.walletapplication.domain.usecase.transaction.GetTransactionsUseCase;
import com.example.walletapplication.domain.usecase.transaction.UpdateTransactionUseCase;
import com.example.walletapplication.domain.usecase.category.AddCategoryUseCase;
import com.example.walletapplication.domain.usecase.category.UpdateCategoryUseCase;
import com.example.walletapplication.domain.usecase.category.DeleteCategoryUseCase;
import com.example.walletapplication.domain.usecase.category.GetCategoriesByTypeUseCase;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class UseCaseModule {

    @Provides
    public AddTransactionUseCase provideAddTransactionUseCase(TransactionRepository repository) {
        return new AddTransactionUseCase(repository);
    }

    @Provides
    public GetTransactionsUseCase provideGetTransactionsUseCase(TransactionRepository repository) {
        return new GetTransactionsUseCase(repository);
    }

    @Provides
    public GetMonthlyReportUseCase provideGetMonthlyReportUseCase(TransactionRepository repository) {
        return new GetMonthlyReportUseCase(repository);
    }

    @Provides
    public UpdateTransactionUseCase provideUpdateTransactionUseCase(TransactionRepository repository) {
        return new UpdateTransactionUseCase(repository);
    }

    @Provides
    public DeleteTransactionUseCase provideDeleteTransactionUseCase(TransactionRepository repository) {
        return new DeleteTransactionUseCase(repository);
    }

    @Provides
    public GetCategoriesUseCase provideGetCategoriesUseCase(TransactionRepository repository) {
        return new GetCategoriesUseCase(repository);
    }

    @Provides
    public AdvancedSearchUseCase provideAdvancedSearchUseCase(TransactionRepository repository) {
        return new AdvancedSearchUseCase(repository);
    }

    // Category Use Cases
    @Provides
    public AddCategoryUseCase provideAddCategoryUseCase(CategoryRepository repository) {
        return new AddCategoryUseCase(repository);
    }

    @Provides
    public UpdateCategoryUseCase provideUpdateCategoryUseCase(CategoryRepository repository) {
        return new UpdateCategoryUseCase(repository);
    }

    @Provides
    public DeleteCategoryUseCase provideDeleteCategoryUseCase(CategoryRepository repository) {
        return new DeleteCategoryUseCase(repository);
    }

    @Provides
    public GetCategoriesByTypeUseCase provideGetCategoriesByTypeUseCase(CategoryRepository repository) {
        return new GetCategoriesByTypeUseCase(repository);
    }
} 