package cn.amazon.aws.rp.spapi.clients.api;

import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.model.*;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.enums.NotificationType;
import cn.amazon.aws.rp.spapi.utils.CredentialsHelper;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
class NotificationsApiIT {

    static private final Logger logger = LoggerFactory.getLogger(NotificationsApiIT.class);

    private NotificationsApi initWithAppCredentials() throws NoSuchFieldException, IllegalAccessException {
        SellerCredentials credentials = CredentialsHelper.getAppCredentials();

        return NotificationsApi.buildNotificationGrantLessApi(credentials);
    }

    private NotificationsApi initWithSellerCredentials() throws NoSuchFieldException, IllegalAccessException {
        SellerCredentials credentials = CredentialsHelper.getSellerCredentials();
        return NotificationsApi.buildNotificationApi(credentials);
    }

    @Test
    void createDestinationEventBridge() throws NoSuchFieldException, IllegalAccessException, ApiException {
        NotificationsApi notificationsApi = initWithAppCredentials();

        DestinationResourceSpecification spec = new DestinationResourceSpecification();
        EventBridgeResourceSpecification eventBridgeResourceSpecification = new EventBridgeResourceSpecification();
        eventBridgeResourceSpecification.setAccountId("716414967168");
        eventBridgeResourceSpecification.setRegion("us-east-2");
        spec.setEventBridge(eventBridgeResourceSpecification);
        CreateDestinationRequest createDestinationRequest = new CreateDestinationRequest();
        createDestinationRequest.setName("Integration test 02");
        createDestinationRequest.setResourceSpecification(spec);
        CreateDestinationResponse response = notificationsApi.createDestination(createDestinationRequest);
        logger.info("payload is {}",response.getPayload());

    }

    @Test
    void createDestinationSQS() throws NoSuchFieldException, IllegalAccessException, ApiException {
        NotificationsApi notificationsApi = initWithAppCredentials();

        DestinationResourceSpecification spec = new DestinationResourceSpecification();

        SqsResource sqsResource = new SqsResource();
        sqsResource.setArn("arn:aws:sqs:us-west-2:716414967168:sp-api-self");
        spec.setSqs(sqsResource);
        CreateDestinationRequest createDestinationRequest = new CreateDestinationRequest();
        createDestinationRequest.setName("SQS Notice");
        createDestinationRequest.setResourceSpecification(spec);
        CreateDestinationResponse response = notificationsApi.createDestination(createDestinationRequest);
        System.out.println(response.getPayload());
    }

    @Test
    void createSubscription_BRANDED_ITEM_CONTENT_CHANGE() throws NoSuchFieldException, IllegalAccessException {
        NotificationsApi notificationsApi = initWithSellerCredentials();


        CreateSubscriptionRequest request = new CreateSubscriptionRequest();
        request.setDestinationId("6da94999-4b4d-4a11-b1e7-a951953648db");
        request.setPayloadVersion("1.0");
        CreateSubscriptionResponse response = null;

        logger.info("REPORT_PROCESSING_FINISHED");
        try {
            response = notificationsApi.createSubscription(request,NotificationType.BRANDED_ITEM_CONTENT_CHANGE.name());
            logger.info("payload is {}", response.getPayload());
        } catch (ApiException e) {
            logger.error("error is {}",e);
        }
    }

    @Test
    void createSubscription_REPORT_PROCESSING_FINISHED() throws NoSuchFieldException, IllegalAccessException {
        NotificationsApi notificationsApi = initWithSellerCredentials();


        CreateSubscriptionRequest request = new CreateSubscriptionRequest();
        request.setDestinationId("f0b4bd2d-7cec-42c8-815a-1e62f53732ee");
        request.setPayloadVersion("1.0");
        CreateSubscriptionResponse response = null;

        logger.info("REPORT_PROCESSING_FINISHED");
        try {
            response = notificationsApi.createSubscription(request,NotificationType.REPORT_PROCESSING_FINISHED.name());
            logger.info("payload is {}", response.getPayload());
        } catch (ApiException e) {
            logger.error("error is {}",e);
        }
    }

    @Test
    void getDestination() {
    }

    @Test
    void getDestinations() throws NoSuchFieldException, IllegalAccessException, ApiException {
        NotificationsApi notificationsApi = initWithAppCredentials();
        GetDestinationsResponse response = notificationsApi.getDestinations();
        System.out.println(response.getPayload());

    }

    @Test
    void getSubscription() throws NoSuchFieldException, IllegalAccessException, ApiException {
        NotificationsApi notificationsApi = initWithSellerCredentials();
        GetSubscriptionResponse response = notificationsApi.getSubscription(NotificationType.REPORT_PROCESSING_FINISHED.name());
        System.out.println(response.getPayload());
//        f0fe4b72-41ee-4acb-80c6-5ba78a2dca64
    }

    @Test
    void getSubscriptionById() {
    }

    @Test
    void createDestinationCall() {
    }


    @Test
    void createDestinationWithHttpInfo() {
    }

    @Test
    void createDestinationAsync() {
    }

    @Test
    void createSubscriptionCall() {
    }

    @Test
    void createSubscriptionWithHttpInfo() {
    }

    @Test
    void createSubscriptionAsync() {
    }

    @Test
    void deleteDestinationCall() {
    }

    @Test
    void deleteDestination() {
    }

    @Test
    void deleteDestinationWithHttpInfo() {
    }

    @Test
    void deleteDestinationAsync() {
    }

    @Test
    void deleteSubscriptionByIdCall() {
    }

    @Test
    void deleteSubscriptionById() throws NoSuchFieldException, IllegalAccessException, ApiException {
        NotificationsApi notificationsApi = initWithSellerCredentials();
        DeleteSubscriptionByIdResponse response =notificationsApi.deleteSubscriptionById("cf1095ca-f4b1-4ad3-9a87-06fc692aac52","REPORT_PROCESSING_FINISHED");
        System.out.println(response);
    }

    @Test
    void deleteSubscriptionByIdWithHttpInfo() {
    }

    @Test
    void deleteSubscriptionByIdAsync() {
    }

    @Test
    void getDestinationCall() {
    }


    @Test
    void getDestinationWithHttpInfo() {
    }

    @Test
    void getDestinationAsync() {
    }

    @Test
    void getDestinationsCall() {
    }

    @Test
    void getDestinationsWithHttpInfo() {
    }

    @Test
    void getDestinationsAsync() {
    }

    @Test
    void getSubscriptionCall() {
    }


    @Test
    void getSubscriptionWithHttpInfo() {
    }

    @Test
    void getSubscriptionAsync() {
    }

    @Test
    void getSubscriptionByIdCall() {
    }


    @Test
    void getSubscriptionByIdWithHttpInfo() {
    }

    @Test
    void getSubscriptionByIdAsync() {
    }
}