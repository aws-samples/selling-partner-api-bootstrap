package cn.amazon.aws.rp.spapi.clients.api;

import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.model.GetCatalogItemResponse;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Ignore
class CatalogApiIT {

    @Test
    void buildCatalogApi() throws NoSuchFieldException, IllegalAccessException, ApiException {
        SellerCredentials credentials = new SellerCredentials();
        credentials.setLWAAuthorizationCredentials_refreshToken("Atzr|IwEBIJ27mtx3w0pHV9Rc8TLfmcGX2yEQnC27-88Ya_uI8FqOdAXrdCTzJucIhj1nc-XHkHNxRbBosXdF33nJtDYOQYvql_FGwYBmPMAmu24YybdD3BblXut81LxL6HKTzfF2Ebgi_lF-KmHSxoz4glZCgH8a-2jbOZJbnvJKb_bAZLxWfsgLawhqlHrhyhpSoCAclVfvFGzWG2Wv1hJDgSV2ggMf-4Y26TJ58rM-gMLuL4ipjeOG7QWb7pLcdgcly5XiMuLJLGNVf8h_1-OznfgFgnroYrORlRRkCQkfdheDO_BT0BNj0GPm3bX5u3wsY9go4To");
        credentials.setSeller_id("seller_jim");

        CatalogApi catalogApi = CatalogApi.buildCatalogApi(credentials);
        GetCatalogItemResponse getCatalogItemResponse = catalogApi.getCatalogItem("ATVPDKIKX0DER","B0892F2531");
        System.out.println(getCatalogItemResponse.getPayload());
    }
}