//package cn.amazon.aws.rp.spapi.lambda.notification;
//
//import cn.amazon.aws.rp.spapi.clients.api.NotificationsApi;
//import cn.amazon.aws.rp.spapi.clients.model.*;
//import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
//import cn.amazon.aws.rp.spapi.dynamodb.impl.SpApiSecretDao;
//import cn.amazon.aws.rp.spapi.utils.Helper;
//import cn.amazon.aws.rp.spapi.invoker.notification.EventCreateDestination;
//import cn.amazon.aws.rp.spapi.invoker.notification.EventCreateSubscription;
//import cn.amazon.aws.rp.spapi.lambda.requestlimiter.ApiProxy;
//import cn.amazon.aws.rp.spapi.utils.Utils;
//import com.amazonaws.services.lambda.runtime.Context;
//import com.amazonaws.services.lambda.runtime.RequestHandler;
//import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
//import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import cn.amazon.aws.rp.spapi.clients.ApiResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.HashMap;
//import java.util.List;
//
///**
// * CDK is already prepared the sqs and this is triggered by APIGateway for now.
// */
//public class EventSubscription implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
//
//    private static final Logger logger = LoggerFactory.getLogger(EventSubscription.class);
//    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
//    // From cdk.
//    public static final String SQS_ARN = Utils.getEnv("SQS_ARN");
//
//    @Override
//    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context){
//        Helper.logInput(logger, input, context, gson);
//        final APIGatewayProxyResponseEvent resp = new APIGatewayProxyResponseEvent();
//        final List<SellerCredentials> sellerCredentials = SpApiSecretDao.getSellerCredentials();
//
//        logger.info("going to subscribe for number of sellers: " + sellerCredentials.size());
//
//        final Boolean[] isError = {false};
//        sellerCredentials.forEach(
//                secretsVO -> {
//                    try {
//                        // If there is error for one seller we still try for other sellers.
//                        subscribe(secretsVO);
//                    } catch (Throwable e) {
//                        isError[0] = true;
//                    }
//                }
//        );
//        resp.setStatusCode(200);
//        if (isError[0]) {
//            resp.setStatusCode(500);
//        }
//        return resp;
//    }
//
//    /**
//     * Ref - <BR>
//     * https://github.com/amzn/selling-partner-api-docs/blob/main/guides/use-case-guides/notifications-use-case-guide-v1.md#step-3-create-a-subscription
//     */
//    private static void subscribe(SellerCredentials secretsVO) throws Throwable {
//
//        logger.info("going to subscribe for seller: " + secretsVO.getSeller_id());
//        final CreateDestinationRequest createDestinationRequest = new CreateDestinationRequest();
//
//        // Prepare parameters for create destination
//        createDestinationRequest.setName(secretsVO.getSeller_id());
//        final DestinationResourceSpecification spec = new DestinationResourceSpecification();
//        final SqsResource sqsRes = new SqsResource();
//        sqsRes.arn(SQS_ARN);
//        spec.setSqs(sqsRes);
//        createDestinationRequest.setResourceSpecification(spec);
//
//        // Prepare parameters for create subscription
//        final CreateSubscriptionRequest createSubscriptionRequest = new CreateSubscriptionRequest();
//        final String payloadVersion = "1.0"; // Specified by API
//        createSubscriptionRequest.setPayloadVersion(payloadVersion);
//
//        // prepare to invoke createDestination
//        final ApiProxy<CreateDestinationResponse> createDestinationApi =
//                new ApiProxy<>(new EventCreateDestination(NotificationsApi.buildNotificationGrantLessApi(secretsVO)));
//        final HashMap<String, Object> inputForCreateDestination = new HashMap<>();
//        inputForCreateDestination.put("createDestinationRequest", createDestinationRequest);
//        final ApiResponse<CreateDestinationResponse> destinationResponse;
//        destinationResponse = createDestinationApi.invkWithToken(inputForCreateDestination, secretsVO.getSeller_id());
//        logger.info("sqs destination is created. - " + gson.toJson(destinationResponse));
//
//        // prepare to invoke subscription
//        final String destinationId = destinationResponse.getData().getPayload().getDestinationId();
//        final HashMap<String, Object> subscriptionRequest = new HashMap<>();
//        createSubscriptionRequest.setDestinationId(destinationId);
//
//        subscriptionRequest.put("createSubscriptionRequest", createSubscriptionRequest);
//        subscriptionRequest.put("notificationType", "ANY_OFFER_CHANGED");
//
//        // If you want to subscribe more event type you need to request subscription one by one.
//        final ApiProxy<CreateSubscriptionResponse> createSubscriptionResponseApiProxy =
//                new ApiProxy<>(new EventCreateSubscription(NotificationsApi.buildNotificationApi(secretsVO)));
//        final ApiResponse<CreateSubscriptionResponse> subscriptionResponse =
//                createSubscriptionResponseApiProxy.invkWithToken(subscriptionRequest, secretsVO.getSeller_id());
//        logger.info("subscription response is: " + gson.toJson(subscriptionResponse));
//
//    }
//}
