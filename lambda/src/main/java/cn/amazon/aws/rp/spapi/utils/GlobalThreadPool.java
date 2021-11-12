package cn.amazon.aws.rp.spapi.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class GlobalThreadPool {

    // Global Shared
    public static final ScheduledThreadPoolExecutor SCHEDULED_POOL
            = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(3);

}
