package cn.amazon.aws.rp.spapi.tasks.requestlimiter;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Require to set environment variable REDIS_URL=127.0.0.1 and stat a redis.
 */
@Ignore
public class RateLimiterRedisIT {

    @Test
    public void acquirePermit() {
        RateLimiterRedis.acquirePermit("Test9", 1, 1);
    }

    @Test
    public void acquirePermits() {
        RateLimiterRedis.updateRateLimiter("Test9", 1, 1);
        for (int i = 0; i < 5; i++) {
            RateLimiterRedis.acquirePermit("Test9", 1, 1);
        }
    }

    @Test
    public void acquirePermitsTakeLonger() {
        RateLimiterRedis.updateRateLimiter("Test9", 1, 5);
        for (int i = 0; i < 2; i++) {
            RateLimiterRedis.acquirePermit("Test9", 1, 1);
        }
    }

    @Test
    public void updateRateLiter() {
        RateLimiterRedis.updateRateLimiter("Test9", 1, 5);
        RateLimiterRedis.updateRateLimiter("Test9", 1, 9);
        RateLimiterRedis.updateRateLimiter("Test9", 1, 5);
        RateLimiterRedis.updateRateLimiter("Test9", 1, 5);
        RateLimiterRedis.updateRateLimiter("Test9", 1, 9);
    }
}