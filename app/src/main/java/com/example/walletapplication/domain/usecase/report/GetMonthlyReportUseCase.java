package com.example.walletapplication.domain.usecase.report;

import com.example.walletapplication.domain.common.AppError;
import com.example.walletapplication.domain.common.Result;
import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GetMonthlyReportUseCase {
    private final TransactionRepository transactionRepository;

    public GetMonthlyReportUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public CompletableFuture<Result<MonthlyReport>> getMonthlyReport(YearMonth yearMonth) {
        if (yearMonth == null) {
            return CompletableFuture.completedFuture(
                Result.error(AppError.validation("yearMonth", "Yıl-ay bilgisi boş olamaz"))
            );
        }

        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        CompletableFuture<BigDecimal> totalIncome = transactionRepository.getTotalIncomeByDateRange(startDate, endDate);
        CompletableFuture<BigDecimal> totalExpense = transactionRepository.getTotalExpenseByDateRange(startDate, endDate);
        CompletableFuture<List<Transaction>> transactions = transactionRepository.getTransactionsByDateRange(startDate, endDate);

        CompletableFuture<Result<MonthlyReport>> result = new CompletableFuture<>();
        
        CompletableFuture.allOf(totalIncome, totalExpense, transactions)
                .whenComplete((v, throwable) -> {
                    if (throwable != null) {
                        if (throwable.getCause() instanceof Exception) {
                            result.complete(Result.error(AppError.fromException((Exception) throwable.getCause())));
                        } else {
                            result.complete(Result.error(AppError.unknown(throwable)));
                        }
                    } else {
                        try {
                            BigDecimal income = totalIncome.join();
                            BigDecimal expense = totalExpense.join();
                            BigDecimal balance = income.subtract(expense);
                            List<Transaction> transactionList = transactions.join();

                            MonthlyReport report = new MonthlyReport(
                                    yearMonth,
                                    income,
                                    expense,
                                    balance,
                                    transactionList
                            );
                            
                            result.complete(Result.success(report));
                        } catch (Exception e) {
                            result.complete(Result.error(AppError.fromException(e)));
                        }
                    }
                });
        
        return result;
    }

    public static class MonthlyReport {
        private final YearMonth yearMonth;
        private final BigDecimal totalIncome;
        private final BigDecimal totalExpense;
        private final BigDecimal balance;
        private final List<Transaction> transactions;

        public MonthlyReport(YearMonth yearMonth, BigDecimal totalIncome, BigDecimal totalExpense, 
                           BigDecimal balance, List<Transaction> transactions) {
            this.yearMonth = yearMonth;
            this.totalIncome = totalIncome;
            this.totalExpense = totalExpense;
            this.balance = balance;
            this.transactions = transactions;
        }

        public YearMonth getYearMonth() {
            return yearMonth;
        }

        public BigDecimal getTotalIncome() {
            return totalIncome;
        }

        public BigDecimal getTotalExpense() {
            return totalExpense;
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }
    }
} 