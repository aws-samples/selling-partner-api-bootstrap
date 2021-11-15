package cn.amazon.aws.rp.spapi.lambda.finances;

import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.utils.Utils;
import org.junit.jupiter.api.Test;

//@Ignore
class ExecuteFinanceTaskForOneSellerIT {

    @Test
    void handleRequest() {
        ExecuteFinanceTaskForOneSeller executeFinanceTaskForOneSeller = new ExecuteFinanceTaskForOneSeller();
        SellerCredentials credentials = new SellerCredentials();
        credentials.setLWAAuthorizationCredentials_refreshToken(Utils.getEnv("refresh_key"));
        credentials.setSeller_id("seller_unit_test");
        executeFinanceTaskForOneSeller.handleRequest(credentials,null);
    }
}