package cn.amazon.aws.rp.spapi.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.amazon.aws.rp.spapi.utils.GlobalThreadPool.SCHEDULED_POOL;
import static java.util.concurrent.Executors.newScheduledThreadPool;

public class GlobalThreadPool {

    // Global Shared
    public static final ScheduledThreadPoolExecutor SCHEDULED_POOL
            = (ScheduledThreadPoolExecutor) newScheduledThreadPool(3);

    private static final ScheduledExecutorService privateScheduledPool = newScheduledThreadPool(1);

    public GlobalThreadPool() {
        // Check the pool status at interval
        val checkPoolStatus = new CheckPoolStatus();
        privateScheduledPool.scheduleAtFixedRate(checkPoolStatus, 10, 10, TimeUnit.SECONDS);
    }
}

class CheckPoolStatus implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(GlobalThreadPool.class);

    @Override
    public void run() {
        printPoolStatus();
    }

    private void printPoolStatus() {
        final int activeCount = SCHEDULED_POOL.getActiveCount();
        final long taskCount = SCHEDULED_POOL.getTaskCount();
        final int poolSize = SCHEDULED_POOL.getPoolSize();
        logger.info(">>> [Thread pool status: active task count - {}, all completed task count - {}, pool size - {}]"
                , activeCount
                , taskCount
                , poolSize);

    }
}
