package cn.amazon.aws.rp.spapi.lambda.requestlimiter;

import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.ApiResponse;
import cn.amazon.aws.rp.spapi.utils.AbPair;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;


/**
 * Provide AOP like behavior to warp a API call with rateLimiter and retry functions to respect API dynamic rate limit.
 */
public class ApiProxy<R> {

    private static final Logger logger = LoggerFactory.getLogger(ApiProxy.class);
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Dynamic rate limit returned from server.
     */
    public static final String X_AMZN_RATE_LIMIT_LIMIT = "x-amzn-RateLimit-Limit";
    /**
     * API return the code when it start to reject the request.
     */
    public static int OVER_REQUEST_CODE = 429;
    private Invokable<R> ivk = null;

    public ApiProxy(Invokable<R> ivk) {
        this.ivk = ivk;
    }

    /**
     * FIXME - implement updating the limiter.
     *
     * @param input      - API parameters will be put to this map first.
     * @param sellerName - is part of limiter name. It is per seller and per API so the naming convention would
     *                   be - sellerid-apiname
     * @return - API response.
     */
    public ApiResponse<R> invkWithToken(Map<String, Object> input, String sellerName) throws Throwable {

        ApiResponse<R> result = null;
        try {
            final String limiterName = String.format("%s:%s", sellerName, ivk.getRateLimiterNameSuffix());

            // Blocking here.
            acquireToken(limiterName);

            logger.info("request parameters: " + gson.toJson(input));
            try {
                result = ivk.invoke(input);
            } catch (ApiException e) {
                // Retry
                if (result.getStatusCode() == OVER_REQUEST_CODE) {
                    logger.warn("SERVER SIDE LIMIT: going to retry due to request too frequently.");
                    result = retryInvk(input, sellerName);
                }else{
                    throw e;
                }
            }
            logger.info("result is: " + gson.toJson(result)); // TODO remove it.


            // Everything is good here, so we reset delay
            delayTime = 0.0;

            // Update rate limiter
            final List<String> updateLimit = result.getHeaders().get(X_AMZN_RATE_LIMIT_LIMIT);
            if (updateLimit != null) {
                logger.info("server current limit is " + gson.toJson(updateLimit));
                final String limit = updateLimit.get(0);
                if (limit != null) {
                    updateLimiter(limiterName, limit);
                }
            }
        } catch (ApiException throwable) {
            logger.error("Other error found in API Call ", throwable);
            logger.error("Details: " + gson.toJson(throwable));
            throw throwable;
        }
        return result;
    }

    void updateLimiter(String limiterName, String limit) {
        final AbPair<Integer, Integer> rateAndInterval = getRateAndInterval(limit);
        RateLimiterRedis.updateRateLimiter(limiterName, rateAndInterval.getA() , rateAndInterval.getB());
        logger.info("completed update rate limiter for "+ limiterName);
    }

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
     * @return Pair rate/interval
     */
    AbPair<Integer, Integer> getRateAndInterval(String token) {
        if ("0".equals(token)) {
            logger.error("No token from server. ");
            throw new RuntimeException("No token from server");
        }
        final double originLimit = Double.valueOf(token);
        final double intervalDouble = 1d / originLimit;
        int interval = 0;
        int rate = 0;
        if (intervalDouble > 1) {
            interval = (int) Math.ceil(intervalDouble);
            rate = 1;
        } else {
            double rateDouble = 1d / intervalDouble;
            rate = (int) Math.floor(rateDouble);
            interval = 1;
        }
        logger.info("Converted rate: " + rate + ", interval: " + interval);
        return new AbPair((Integer) rate, (Integer) interval);
    }

    private static final Double DELAY_FACTOR = 1.4;
    private Double delayTime = 0.0;

    /**
     * Delay and invoke.
     * <br>Plot the delay here: https://www.wolframalpha.com/input/?i=plot+pow%281.4%2C+x%29
     */
    private ApiResponse<R> retryInvk(Map<String, Object> input, String apiName) throws Throwable {
        delayTime += 1000;
        try {
            final long delay = (long) Math.pow(delayTime, DELAY_FACTOR);
            logger.info("Seconds to wait - " + delay);
            Thread.sleep(delay);
            logger.info("Run again after - " + delay);
        } catch (InterruptedException e) {
            logger.error("CANCEL RETRY BECAUSE OF ERROR ", e);
            throw e;
        }
        return invkWithToken(input, apiName);
    }

    /**
     * @param limiterName
     */
    private void acquireToken(String limiterName) {

        // TODO need to create a table here for default limiter settings
        RateLimiterRedis.acquirePermit(limiterName, 1, 1);

    }
}
