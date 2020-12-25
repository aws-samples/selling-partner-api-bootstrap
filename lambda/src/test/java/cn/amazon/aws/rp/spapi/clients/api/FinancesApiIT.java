package cn.amazon.aws.rp.spapi.clients.api;

import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.model.GetCatalogItemResponse;
import cn.amazon.aws.rp.spapi.clients.model.ListFinancialEventsResponse;
import cn.amazon.aws.rp.spapi.constants.DateConstants;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import org.junit.jupiter.api.Test;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class FinancesApiIT {

    @Test
    void buildFinancesApi() throws NoSuchFieldException, IllegalAccessException, ApiException {
        SellerCredentials credentials = new SellerCredentials();
        credentials.setLWAAuthorizationCredentials_refreshToken("Atzr|IwEBIJ27mtx3w0pHV9Rc8TLfmcGX2yEQnC27-88Ya_uI8FqOdAXrdCTzJucIhj1nc-XHkHNxRbBosXdF33nJtDYOQYvql_FGwYBmPMAmu24YybdD3BblXut81LxL6HKTzfF2Ebgi_lF-KmHSxoz4glZCgH8a-2jbOZJbnvJKb_bAZLxWfsgLawhqlHrhyhpSoCAclVfvFGzWG2Wv1hJDgSV2ggMf-4Y26TJ58rM-gMLuL4ipjeOG7QWb7pLcdgcly5XiMuLJLGNVf8h_1-OznfgFgnroYrORlRRkCQkfdheDO_BT0BNj0GPm3bX5u3wsY9go4To");
        credentials.setSeller_id("seller_jim");

        FinancesApi financesApi = FinancesApi.buildFinancesApi(credentials);
        ListFinancialEventsResponse listFinancialEventsResponse= financesApi.listFinancialEvents(10,
                OffsetDateTime.of(LocalDateTime.parse("2020-08-02 06:34:36", DateTimeFormatter.ofPattern(DateConstants.DATE_TIME_FORMAT)), ZoneOffset.UTC),
                OffsetDateTime.of(LocalDateTime.parse("2020-09-02 06:34:36", DateTimeFormatter.ofPattern(DateConstants.DATE_TIME_FORMAT)), ZoneOffset.UTC),
                "");
        System.out.println(listFinancialEventsResponse.getPayload());
    }
}