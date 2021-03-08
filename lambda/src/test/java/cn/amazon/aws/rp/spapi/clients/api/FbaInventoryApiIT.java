package cn.amazon.aws.rp.spapi.clients.api;

import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.model.GetInventorySummariesResponse;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.utils.CredentialsHelper;
import com.sun.tools.javac.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FbaInventoryApiIT {

    @Test
    void getInventorySummariesCall() {

    }

    @Test
    void getInventorySummaries() throws NoSuchFieldException, IllegalAccessException, ApiException {
        SellerCredentials credentials = CredentialsHelper.getSellerCredentials();
        FbaInventoryApi fbaInventoryApi = FbaInventoryApi.buildFbaInventoryApi(credentials);
        GetInventorySummariesResponse getInventorySummariesResponse = fbaInventoryApi.getInventorySummaries("Marketplace","ATVPDKIKX0DER", List.of("ATVPDKIKX0DER"),null,null,null,null);
        //japan
//        GetInventorySummariesResponse getInventorySummariesResponse = fbaInventoryApi.getInventorySummaries("Marketplace","A1VC38T7YXB528", List.of("A1VC38T7YXB528"),null,null,null,null);
        //EU
//        GetInventorySummariesResponse getInventorySummariesResponse = fbaInventoryApi.getInventorySummaries("Marketplace","A1F83G8C2ARO7P", List.of("A1F83G8C2ARO7P"),null,null,null,null);


        System.out.println(getInventorySummariesResponse);
    }

    @Test
    void getInventorySummariesWithHttpInfo() {
    }

    @Test
    void getInventorySummariesAsync() {
    }
}