package cn.amazon.aws.rp.spapi.utils;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helper {
    static private final Logger logger = LoggerFactory.getLogger(Helper.class);

    public static void logInput(Logger logger, Object event, Gson gson) {
        logger.info("Env: " + gson.toJson(System.getenv()));
        logger.info("Evt: " + gson.toJson(event));
    }

    /**
     * Support async invoke.
     *
     * @param fName
     * @param jsonInput
     */
//    public static void invokeLambda(String fName, String jsonInput, Boolean async) {
//
//        logger.info("invoking: " + fName);
//        InvokeRequest invokeRequest = new InvokeRequest()
//                .withFunctionName(fName)
//                .withPayload(jsonInput);
//        if (async) {
//            invokeRequest.setInvocationType(InvocationType.Event);
//        }
//        InvokeResult invokeResult = null;
//
//        try {
//            AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
//                    .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
//                    .build();
//
//            invokeResult = awsLambda.invoke(invokeRequest);
//            logger.info("invocation completed");
//        } catch (ServiceException e) {
//            logger.error("cannot invoke function ", e);
//            throw e;
//        }
//    }

}
