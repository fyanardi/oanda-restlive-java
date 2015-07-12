package com.yanardi.oanda.data;

public enum Granularity {
    // Top of the minute alignment
    S5, // 5 seconds
    S10, // 10 seconds
    S15, // 15 seconds
    S30, // 30 seconds
    M1, // 1 minute
    // Top of the hour alignment
    M2, // 2 minutes
    M3, // 3 minutes
    M4, // 4 minutes
    M5, // 5 minutes
    M10, // 10 minutes
    M15, // 15 minutes
    M30, // 30 minutes
    H1, // 1 hour
    // Start of day alignment (default 17:00, Timezone/New York)
    H2, // 2 hours
    H3, // 3 hours
    H4, // 4 hours
    H6, // 6 hours
    H8, // 8 hours
    H12, // 12 hours
    D, // 1 Day
    // Start of week alignment (default Friday)
    W, // 1 Week
    // Start of month alignment (First day of the month)
    M, // 1 Month

}
