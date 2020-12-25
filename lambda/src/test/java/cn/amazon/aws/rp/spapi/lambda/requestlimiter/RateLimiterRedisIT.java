package cn.amazon.aws.rp.spapi.lambda.requestlimiter;

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
        for (int i = 0; i < 10; i++) {
            RateLimiterRedis.acquirePermit("Test9", 1, 1);
        }
    }

    @Test
    public void updateRateLimiter() {
        RateLimiterRedis.updateRateLimiter("Test9", 1, 2);
    }

    @Test
    public void acquirePermitsTakeLonger() {
        for (int i = 0; i < 5; i++) {
            RateLimiterRedis.acquirePermit("Test9", 1, 1);
        }
    }
}