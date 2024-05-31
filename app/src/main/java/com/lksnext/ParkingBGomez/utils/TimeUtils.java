package com.lksnext.ParkingBGomez.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class TimeUtils {

    public static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;

    private TimeUtils() {
    }

    public static long convertLocalTimeToEpoch(LocalTime localTime) {
        LocalDateTime localDateTime = localTime.atDate(LocalDate.now());
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return instant.getEpochSecond();
    }

    public static LocalDateTime convertStringToDateLocalTime(String localDateTime) {
        return LocalDateTime.parse(localDateTime);
    }

    public static LocalDateTime convertEpochTolocalDateTime(long epoch) {
        Instant instant = Instant.ofEpochSecond(epoch);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static long convertLocalDateTimeStringToEpoch(String localDateTimeString) {
        LocalDateTime localDateTime = LocalDateTime.parse(localDateTimeString);
        return localDateTime.toEpochSecond(ZONE_OFFSET);
    }

    public static long getStartOfTodayEpoch() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        return startOfToday.toEpochSecond(ZONE_OFFSET);
    }
}