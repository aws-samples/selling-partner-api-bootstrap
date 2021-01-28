package cn.amazon.aws.rp.spapi.clients.api;

import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.model.ListFinancialEventsResponse;
import cn.amazon.aws.rp.spapi.constants.DateConstants;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.utils.CredentialsHelper;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

@Ignore
class FinancesApiIT {

    @Test
    void buildFinancesApi() throws NoSuchFieldException, IllegalAccessException, ApiException {
        SellerCredentials credentials = CredentialsHelper.getSellerCredentials();

        FinancesApi financesApi = FinancesApi.buildFinancesApi(credentials);
        ListFinancialEventsResponse listFinancialEventsResponse= financesApi.listFinancialEvents(10,
                OffsetDateTime.of(LocalDateTime.parse("2020-08-02 06:34:36", DateTimeFormatter.ofPattern(DateConstants.DATE_TIME_FORMAT)), ZoneOffset.UTC),
                OffsetDateTime.of(LocalDateTime.parse("2020-09-02 06:34:36", DateTimeFormatter.ofPattern(DateConstants.DATE_TIME_FORMAT)), ZoneOffset.UTC),
                "");
        System.out.println(listFinancialEventsResponse.getPayload());
    }
}