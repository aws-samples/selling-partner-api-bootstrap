package cn.amazon.aws.rp.spapi.lambda.requestlimiter;

/**
 * If SP API return 429 in HTTP status code then we have to throw this exception.
 * API Proxy will catch this exception and retry.
 */
public class RateException extends Exception{

}
