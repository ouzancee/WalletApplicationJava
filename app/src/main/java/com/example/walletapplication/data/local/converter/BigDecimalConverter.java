package com.example.walletapplication.data.local.converter;

import androidx.room.TypeConverter;

import java.math.BigDecimal;

public class BigDecimalConverter {
    
    @TypeConverter
    public static BigDecimal fromString(String value) {
        return value == null ? null : new BigDecimal(value);
    }

    @TypeConverter
    public static String bigDecimalToString(BigDecimal bigDecimal) {
        return bigDecimal == null ? null : bigDecimal.toString();
    }
} 