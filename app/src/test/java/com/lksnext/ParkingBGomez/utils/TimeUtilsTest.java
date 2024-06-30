package com.lksnext.ParkingBGomez.utils;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.Assert.assertEquals;

public class TimeUtilsTest {

    @Test
    public void testConvertLocalDateTimeToEpoch() {
        LocalDateTime now = LocalDateTime.now();
        long epoch = TimeUtils.convertLocalDateTimeToEpoch(now);
        assertEquals(now.atZone(ZoneId.systemDefault()).toEpochSecond(), epoch);
    }

    @Test
    public void testConvertLocalDateTimeToTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        com.google.firebase.Timestamp timestamp = TimeUtils.convertLocalDateTimeToTimestamp(now);
        assertEquals(now.atZone(ZoneId.systemDefault()).toEpochSecond(), timestamp.getSeconds());
    }

    @Test
    public void testConvertTimestampToLocalDateTime() {
        com.google.firebase.Timestamp timestamp = com.google.firebase.Timestamp.now();
        LocalDateTime dateTime = TimeUtils.convertTimestampToLocalDateTime(timestamp);
        assertEquals(timestamp.getSeconds(), dateTime.atZone(ZoneId.systemDefault()).toEpochSecond());
    }

    @Test
    public void testConvertLocalTimeToEpochWithLocalDate() {
        LocalDateTime now = LocalDateTime.now();
        long epoch = TimeUtils.convertLocalTimeToEpochWithLocalDate(now.toLocalTime(), now.toLocalDate());
        assertEquals(now.atZone(ZoneId.systemDefault()).toEpochSecond(), epoch);
    }

    @Test
    public void testConvertEpochToLocalDateTime() {
        long epoch = System.currentTimeMillis() / 1000;
        LocalDateTime dateTime = TimeUtils.convertEpochTolocalDateTime(epoch);
        assertEquals(epoch, dateTime.atZone(ZoneId.systemDefault()).toEpochSecond());
    }

    @Test
    public void testGetStartOfTodayEpoch() {
        long startOfTodayEpoch = TimeUtils.getStartOfTodayEpoch();
        LocalDateTime startOfToday = LocalDateTime.ofInstant(java.time.Instant.ofEpochSecond(startOfTodayEpoch), ZoneId.systemDefault());
        assertEquals(0, startOfToday.getHour());
        assertEquals(0, startOfToday.getMinute());
        assertEquals(0, startOfToday.getSecond());
    }

    @Test
    public void testGetNowEpoch() {
        long nowEpoch = TimeUtils.getNowEpoch();
        long currentEpoch = System.currentTimeMillis() / 1000;
        assertEquals(currentEpoch, nowEpoch, 1);
    }
}