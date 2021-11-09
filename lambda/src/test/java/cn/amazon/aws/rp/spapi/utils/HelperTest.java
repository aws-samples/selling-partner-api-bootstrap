package cn.amazon.aws.rp.spapi.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class HelperTest {

    @Test
    public void getIso8601Time() {
        String time = "2020-08-01 00:00:00";
        final String iso8601Time = Helper.getIso8601Time(time);
        assertEquals("2020-07-31T16:00:00.000Z", iso8601Time);
    }
}