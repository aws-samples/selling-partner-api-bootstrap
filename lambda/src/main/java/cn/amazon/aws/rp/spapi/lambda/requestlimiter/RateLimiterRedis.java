package cn.amazon.aws.rp.spapi.lambda.requestlimiter;

import cn.amazon.aws.rp.spapi.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateLimiterRedis {
    private static final Logger logger = LoggerFactory.getLogger(RateLimiterRedis.class);
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static RedissonClient redisson = null;

    /**
     * TODO properly shutdown the redisson session.
     * https://docs.aws.amazon.com/lambda/latest/dg/runtimes-modify.html
     *
     * TODO - Use the HA redis server.
     */
    static {
        Config config = new Config();
        String redisURL = "redis://" + Utils.getEnv("REDIS_URL") + ":6379";
        logger.info("Connecting to redis: " + redisURL);
        config.useSingleServer()
                .setAddress(redisURL)
                .setConnectTimeout(1000)
                .setConnectionPoolSize(1)
                .setConnectionMinimumIdleSize(1);
        redisson = Redisson.create(config);
        logger.info("Client created");
    }

    /**
     * Blocking call.
     * Check and create the limiter if not crated before.
     */
    public static void acquirePermit(String limiterName, Integer rate, Integer interval) {

        // Ensure the limiter is created for this name.
        ensureLimiterExist(limiterName, rate, interval);
        logger.info("Get limiter- " + limiterName);
        RRateLimiter limiter = redisson.getRateLimiter(limiterName);
        logger.info(">> Acquiring permit");
        limiter.acquire();
        logger.info("<< Acquired permit at interval - " + limiter.getConfig().getRateInterval());
    }

    public static void updateRateLimiter(String limiterName, Integer rate, Integer interval) {
        ensureLimiterExist(limiterName, rate, interval);

        RRateLimiter limiter = redisson.getRateLimiter(limiterName);
        final RateLimiterConfig rrConfig = limiter.getConfig();
        if(((long) interval + 3L) == rrConfig.getRateInterval()){
            logger.info(String.format("Update rate [%s], interval [%s] ", rate, interval));
            limiter.setRate(RateType.OVERALL, (long) rate, (long) interval + 3L, RateIntervalUnit.SECONDS);
        }else{
            logger.info("SKIP - Interval doesn't change.");
        }
    }

    // *************** Helpers ***************************

    private static void createRateLimiter(String limiterName, Integer rate, Integer interval) {
        logger.info("Acquire for " + limiterName);

        // Ensure the limiter is created for this name.
        RRateLimiter limiter = redisson.getRateLimiter(limiterName);
        logger.info("Try set rate");
        limiter.trySetRate(RateType.OVERALL, (long) rate, (long) interval, RateIntervalUnit.SECONDS);
        logger.info("Created: " + limiterName);
    }

    private static void ensureLimiterExist(String limiterName, Integer rate, Integer interval) {
        logger.info("Ensure limiter exist for - " + limiterName);
        final long isCreated = redisson.getKeys().countExists(limiterName);
        if (isCreated == 0) { // Not created.
            logger.info("Limiter not created, going to create: " + limiterName);
            createRateLimiter(limiterName, rate, interval);
        } else {
            logger.info("limiter found.");
        }
    }

}
