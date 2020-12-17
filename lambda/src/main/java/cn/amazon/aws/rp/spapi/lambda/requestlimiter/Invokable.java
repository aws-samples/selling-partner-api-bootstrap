package cn.amazon.aws.rp.spapi.lambda.requestlimiter;


import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.ApiResponse;

import java.util.Map;

/**
 * All SP APIs need to be wrapped with the interface and use ApiProxy to invoke.
 * @param <R>
 */
public interface Invokable<R> {

    /**
     * Convention for input - The key in map is the name of the parameter of the api call.
     * Handle multiple params  https://stackoverflow.com/a/8716845
     * @param input
     * @return
     */
    ApiResponse<R> invoke(Map<String, Object> input) throws ApiException;

    /**
     * More invokables can share one rate limiter if they return the same limiter name.
     * @return
     */
    String getRateLimiterNameSuffix();

}
