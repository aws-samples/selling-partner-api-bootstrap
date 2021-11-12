package cn.amazon.aws.rp.spapi.tasks.requestlimiter;

import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.ApiResponse;
import cn.amazon.aws.rp.spapi.utils.AbPair;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class ApiProxyTest {

    /**
     * Server respond would be like "0.016", "1" - meaning the token per second.
     * if token is 0.016
     * 1 / 0.016 = 62 => interval 62, rate 1
     *
     * <p>
     *     If token is 2
     * 1 / 2   = 0.5  => interval 0.5, rate 1 => interval 1, rate 2
     * <p>
     * 1 / 1  =1  => interval 1, rate 1
     */
    @Test
    public void getRateAndInterval() {
        final ApiProxy<Object> apiProxy = new ApiProxy<>(new Invokable<Object>() {
            @Override
            public ApiResponse<Object> invoke(Map<String, Object> input) throws ApiException {
                return null;
            }

            @Override
            public String getRateLimiterNameSuffix() {
                return null;
            }
        });

        AbPair<Integer, Integer> rateInterval = apiProxy.getRateAndInterval("0.016");
        Integer rate = rateInterval.getA(); // Rate
        Integer interval = rateInterval.getB(); // Interval
        Assert.assertEquals(rate, Integer.valueOf(1));
        Assert.assertEquals(interval, Integer.valueOf(63));

        rateInterval = apiProxy.getRateAndInterval("2");
        rate = rateInterval.getA(); // Rate
        interval = rateInterval.getB(); // Interval
        Assert.assertEquals(rate, Integer.valueOf(2));
        Assert.assertEquals(interval, Integer.valueOf(1));
    }

    @Test
    public void testDived() {
        System.out.println((1d / 0.016));
        System.out.println(Math.ceil(1d / 0.016));
    }
}