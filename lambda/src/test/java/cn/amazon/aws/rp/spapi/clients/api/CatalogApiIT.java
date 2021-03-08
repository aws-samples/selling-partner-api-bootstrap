package cn.amazon.aws.rp.spapi.clients.api;

import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.model.GetCatalogItemResponse;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.utils.CredentialsHelper;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

@Ignore
class CatalogApiIT {

    @Test
    void buildCatalogApi() throws NoSuchFieldException, IllegalAccessException, ApiException {
        SellerCredentials credentials = CredentialsHelper.getSellerCredentials();

        CatalogApi catalogApi = CatalogApi.buildCatalogApi(credentials);

        GetCatalogItemResponse getCatalogItemResponse = catalogApi.getCatalogItem("ATVPDKIKX0DER","B08LKFB6RC"); //relationships: null

//        GetCatalogItemResponse getCatalogItemResponse = catalogApi.getCatalogItem("ATVPDKIKX0DER","B08LKFB6RC"); //relationships: null
//        GetCatalogItemResponse getCatalogItemResponse = catalogApi.getCatalogItem("ATVPDKIKX0DER","B08KSWC82P");

        //Japan

//        GetCatalogItemResponse getCatalogItemResponse = catalogApi.getCatalogItem("A39IBJ37TRP1C6","B07NS381GK"); //relationships: null



        System.out.println(getCatalogItemResponse.getPayload().getRelationships());
    }
}