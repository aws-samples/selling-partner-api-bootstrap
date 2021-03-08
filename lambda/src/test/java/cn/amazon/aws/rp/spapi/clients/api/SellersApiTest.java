package cn.amazon.aws.rp.spapi.clients.api;

import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.model.GetMarketplaceParticipationsResponse;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.utils.CredentialsHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SellersApiTest {

    @Test
    void getMarketplaceParticipationsCall() {
    }

    @Test
    void getMarketplaceParticipations() throws NoSuchFieldException, IllegalAccessException, ApiException {
        SellerCredentials credentials = CredentialsHelper.getSellerCredentials();

        SellersApi catalogApi = SellersApi.buildSellerApi(credentials);
        GetMarketplaceParticipationsResponse response = catalogApi.getMarketplaceParticipations();
        System.out.println(response);

    }

    @Test
    void getMarketplaceParticipationsWithHttpInfo() {
    }

    @Test
    void getMarketplaceParticipationsAsync() {
    }

    @Test
    void testGetMarketplaceParticipations() {
    }
}