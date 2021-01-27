package cn.amazon.aws.rp.spapi.clients.api;

import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.model.*;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

@Ignore
class NotificationsApiIT {

    @Test
    void createDestinationEventBridge() throws NoSuchFieldException, IllegalAccessException, ApiException {
        NotificationsApi notificationsApi = initWithOutCredentials();

        DestinationResourceSpecification spec = new DestinationResourceSpecification();
        EventBridgeResourceSpecification eventBridgeResourceSpecification = new EventBridgeResourceSpecification();
        eventBridgeResourceSpecification.setAccountId("716414967168");
        eventBridgeResourceSpecification.setRegion("us-west-2");
        spec.setEventBridge(eventBridgeResourceSpecification);
        CreateDestinationRequest createDestinationRequest = new CreateDestinationRequest();
        createDestinationRequest.setName("Integration test 02");
        createDestinationRequest.setResourceSpecification(spec);
        CreateDestinationResponse response = notificationsApi.createDestination(createDestinationRequest);
        System.out.println(response.getPayload());

    }

    @Test
    void createDestinationSQS() throws NoSuchFieldException, IllegalAccessException, ApiException {
        NotificationsApi notificationsApi = initWithOutCredentials();

        DestinationResourceSpecification spec = new DestinationResourceSpecification();

        SqsResource sqsResource = new SqsResource();
        sqsResource.setArn("arn:aws:sqs:us-west-2:716414967168:sp-api-self");
        spec.setSqs(sqsResource);
        CreateDestinationRequest createDestinationRequest = new CreateDestinationRequest();
        createDestinationRequest.setName("SQS Notice");
        createDestinationRequest.setResourceSpecification(spec);
        CreateDestinationResponse response = notificationsApi.createDestination(createDestinationRequest);
        System.out.println(response.getPayload());
//        01e471b0-a0bc-4627-8854-cb597f812da2

        // self f5f34d5a-4731-4fc2-90cf-81b25c385416
    }

    private NotificationsApi initWithOutCredentials() throws NoSuchFieldException, IllegalAccessException {
        SellerCredentials credentials = new SellerCredentials();
        credentials.setSeller_id("seller_jim");

        return NotificationsApi.buildNotificationGrantLessApi(credentials);
    }

    private NotificationsApi initWithCredentials() throws NoSuchFieldException, IllegalAccessException {
        SellerCredentials credentials = new SellerCredentials();
//        credentials.setLWAAuthorizationCredentials_refreshToken("Atzr|IwEBIJ27mtx3w0pHV9Rc8TLfmcGX2yEQnC27-88Ya_uI8FqOdAXrdCTzJucIhj1nc-XHkHNxRbBosXdF33nJtDYOQYvql_FGwYBmPMAmu24YybdD3BblXut81LxL6HKTzfF2Ebgi_lF-KmHSxoz4glZCgH8a-2jbOZJbnvJKb_bAZLxWfsgLawhqlHrhyhpSoCAclVfvFGzWG2Wv1hJDgSV2ggMf-4Y26TJ58rM-gMLuL4ipjeOG7QWb7pLcdgcly5XiMuLJLGNVf8h_1-OznfgFgnroYrORlRRkCQkfdheDO_BT0BNj0GPm3bX5u3wsY9go4To");
        //self
        credentials.setLWAAuthorizationCredentials_refreshToken("Atzr|IwEBIBOvQp_eXz1bdpUVtjNX7C_cEp50Z6uQ5iQbv-VQOY7H5AYlB5DR87K73AH_oNJc0DSD0wDqNaDFNxVjVbv0forJR4ti8XVZF7VXUQJxNAWjlByAERRO3QXqItyaetkBLhlLdGdVHQ3bzXtVkvRZmYqOivnSz9gRNxkUMdMQu9vPRZWNgitX-oVgSTfS-mJzeeEpiynWfviMwKRB9sfcGvQLM17NME6lFjA0-OnQgVh-8wQiolrNRzoeupVeud7eXbgB17YaM5Bk6XhtLAWSJCL5oYqN6dS6OVYWQiqrPShlCMu2ot1mOf9uRwfVhXOfy-Y");
        credentials.setSeller_id("seller_jim");

        return NotificationsApi.buildNotificationApi(credentials);
    }


    @Test
    void createSubscription() throws NoSuchFieldException, IllegalAccessException {
        NotificationsApi notificationsApi = initWithCredentials();


        CreateSubscriptionRequest request = new CreateSubscriptionRequest();
        request.setDestinationId("01e471b0-a0bc-4627-8854-cb597f812da2");
        request.setPayloadVersion("1.0");
        CreateSubscriptionResponse response = null;
        try {
            System.out.println("ANY_OFFER_CHANGED");
            response = notificationsApi.createSubscription(request,"ANY_OFFER_CHANGED");
        }catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println("FEED_PROCESSING_FINISHED");
        try {
            response = notificationsApi.createSubscription(request,"FEED_PROCESSING_FINISHED");
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println("FBA_OUTBOUND_SHIPMENT_STATUS");
        try {
            response = notificationsApi.createSubscription(request,"FBA_OUTBOUND_SHIPMENT_STATUS");
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println("FEE_PROMOTION");
        try {
            response = notificationsApi.createSubscription(request,"FEE_PROMOTION");
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println("FULFILLMENT_ORDER_STATUS");
        try {
            response = notificationsApi.createSubscription(request,"FULFILLMENT_ORDER_STATUS");
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println("REPORT_PROCESSING_FINISHED");
        try {
            response = notificationsApi.createSubscription(request,"REPORT_PROCESSING_FINISHED");
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println("BRANDED_ITEM_CONTENT_CHANGE");
        try {
            response = notificationsApi.createSubscription(request,"BRANDED_ITEM_CONTENT_CHANGE");
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println("ITEM_PRODUCT_TYPE_CHANGE");
        try {
            response = notificationsApi.createSubscription(request,"ITEM_PRODUCT_TYPE_CHANGE");
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println("MFN_ORDER_STATUS_CHANGE");
        try {
            response = notificationsApi.createSubscription(request,"MFN_ORDER_STATUS_CHANGE");
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println("B2B_ANY_OFFER_CHANGED");
        try {
            response = notificationsApi.createSubscription(request,"B2B_ANY_OFFER_CHANGED");
        } catch (ApiException e) {
            e.printStackTrace();
        }
        if(response != null) {
            System.out.println(response.getPayload());
//        response = notificationsApi.createSubscription(request,"ITEM_PRODUCT_TYPE_CHANGE");
//        System.out.println(response.getPayload());
        }

    }


    @Test
    void createSubscriptionSQS() throws NoSuchFieldException, IllegalAccessException {
        NotificationsApi notificationsApi = initWithCredentials();


        CreateSubscriptionRequest request = new CreateSubscriptionRequest();
        request.setDestinationId("f5f34d5a-4731-4fc2-90cf-81b25c385416");
        request.setPayloadVersion("1.0");
        CreateSubscriptionResponse response = null;

        System.out.println("REPORT_PROCESSING_FINISHED");
        try {
            response = notificationsApi.createSubscription(request,"REPORT_PROCESSING_FINISHED");
        } catch (ApiException e) {
            e.printStackTrace();
        }


        if(response != null) {
            System.out.println(response.getPayload());
//        response = notificationsApi.createSubscription(request,"ITEM_PRODUCT_TYPE_CHANGE");
//        System.out.println(response.getPayload());
        }

    }

    @Test
    void getDestination() {
    }

    @Test
    void getDestinations() throws NoSuchFieldException, IllegalAccessException, ApiException {
        NotificationsApi notificationsApi = initWithOutCredentials();
        GetDestinationsResponse response = notificationsApi.getDestinations();
        System.out.println(response.getPayload());

    }

    @Test
    void getSubscription() throws NoSuchFieldException, IllegalAccessException, ApiException {
        NotificationsApi notificationsApi = initWithCredentials();
        GetSubscriptionResponse response = notificationsApi.getSubscription("REPORT_PROCESSING_FINISHED");
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
        NotificationsApi notificationsApi = initWithCredentials();
        DeleteSubscriptionByIdResponse response =notificationsApi.deleteSubscriptionById("57eba68e-285b-487e-a174-be2878615456","REPORT_PROCESSING_FINISHED");
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