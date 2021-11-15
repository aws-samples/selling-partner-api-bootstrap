package cn.amazon.aws.rp.spapi.utils;

import cn.amazon.aws.rp.spapi.clients.api.*;
import cn.amazon.aws.rp.spapi.dynamodb.ISpApiSecretDao;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.dynamodb.impl.SpApiSecretDao;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Helper {
    static private final Logger logger = LoggerFactory.getLogger(Helper.class);

    public static void logInput(Logger logger, Object event, Context context, Gson gson) {
        logger.info("Env: " + gson.toJson(System.getenv()));
        logger.info("Ctx: " + gson.toJson(context));
        logger.info("Evt: " + gson.toJson(event));
    }

    /**
     * Support async invoke.
     *
     * @param fName
     * @param jsonInput
     */
    public static void invokeLambda(String fName, String jsonInput, Boolean async) {

        logger.info("invoking: " + fName);
        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(fName)
                .withPayload(jsonInput);
        if (async) {
            invokeRequest.setInvocationType(InvocationType.Event);
        }
        InvokeResult invokeResult = null;

        try {
            AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                    .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                    .build();

            invokeResult = awsLambda.invoke(invokeRequest);
            logger.info("invocation completed");
        } catch (ServiceException e) {
            logger.error("cannot invoke function ", e);
            throw e;
        }
    }

    private static Calendar calendar = Calendar.getInstance();

    /**
     * @param aheadSec if giving 0 then return current time string in iso format. If giving 1 then return the
     *                 time 1 second before current.
     * @return
     */
    public static String getIso8601Time(int aheadSec) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        final Date current = new Date();
        calendar.setTime(current);
        calendar.add(Calendar.SECOND, -aheadSec);

        return df.format(calendar.getTime());
    }

    /**
     * @param time in format like 2020-08-01 00:00:00
     */
    public static String getIso8601Time(String time) {
        logger.info("going to format {}", time);
        DateFormat sourceDf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date result = sourceDf.parse(time);

            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
            df.setTimeZone(tz);
            return df.format(result);
        } catch (ParseException e) {
            logger.error("cannot parse date, is it in format like: 2020-08-01 00:00:00");
        }
        return "";
    }

//    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();


//    /**
//     * First get the seller secrets by sellerID from DB.
//     *
//     * @param sellerId
//     * @return
//     */
//    public static OrdersApi buildOrdersApi(String sellerId) {
//        final SellerCredentials secretsVO = getSellerSecret(sellerId);
//        return buildOrdersApi(secretsVO);
//    }

//    private static SellerCredentials getSellerSecret(String sellerId) {
//        final ISpApiSecretDao ssDao = new SpApiSecretDao();
//        return ssDao.getSecretsVOForSeller(sellerId);
//    }


}
