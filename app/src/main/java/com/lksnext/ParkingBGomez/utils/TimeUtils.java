package com.lksnext.ParkingBGomez.utils;

import com.google.firebase.Timestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class TimeUtils {

    public static final ZoneId ZONE_ID = ZoneId.systemDefault();
    public static final ZoneOffset ZONE_OFFSET = ZONE_ID.getRules().getOffset(Instant.now());

    private TimeUtils() {
    }

    public static long convertLocalTimeToEpoch(LocalTime localTime) {
        LocalDateTime localDateTime = localTime.atDate(LocalDate.now());
        Instant instant = localDateTime.atZone(ZONE_ID).toInstant();
        return instant.getEpochSecond();
    }

    public static long convertLocalDateTimeToEpoch(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZONE_ID).toInstant();
        return instant.getEpochSecond();
    }

    public static Timestamp convertLocalDateTimeToTimestamp(LocalDateTime localDateTime) {
        return new Timestamp(localDateTime.toEpochSecond(ZONE_OFFSET), 0);
    }

    public static LocalDateTime convertTimestampToLocalDateTime(Timestamp timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp.getSeconds()), ZONE_ID);
    }

    public static long convertLocalTimeToEpochWithLocalDate(LocalTime localTime, LocalDate localDate) {
        LocalDateTime localDateTime = localTime.atDate(localDate);
        Instant instant = localDateTime.atZone(ZONE_ID).toInstant();
        return instant.getEpochSecond();
    }

    public static LocalDateTime convertStringToDateLocalTime(String localDateTime) {
        return LocalDateTime.parse(localDateTime);
    }

    public static LocalDateTime convertEpochTolocalDateTime(long epoch) {
        Instant instant = Instant.ofEpochSecond(epoch);
        return LocalDateTime.ofInstant(instant, ZONE_ID);
    }

    public static long convertLocalDateTimeStringToEpoch(String localDateTimeString) {
        LocalDateTime localDateTime = LocalDateTime.parse(localDateTimeString);
        return localDateTime.toEpochSecond(ZONE_OFFSET);
    }

    public static long getStartOfTodayEpoch() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        return startOfToday.toEpochSecond(ZONE_OFFSET);
    }

    public static long getNowEpoch() {
        return LocalDateTime.now().toEpochSecond(ZONE_OFFSET);
    }
}