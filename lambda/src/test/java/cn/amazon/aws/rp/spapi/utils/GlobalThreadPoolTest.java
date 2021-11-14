package cn.amazon.aws.rp.spapi.utils;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class GlobalThreadPoolTest {

    @Test
    void testPrintPoolStatus() throws InterruptedException {
        new GlobalThreadPool();
        Thread.sleep(60 *1000);
    }
}