package cn.amazon.aws.rp.spapi.lambda.finances;

import cn.amazon.aws.rp.spapi.utils.CredentialsHelper;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import org.junit.jupiter.api.Test;

//@Ignore
class ExecuteTaskForOneSellerIT {

    @Test
    void handleRequest() {
        ExecuteTaskForOneSeller executeTaskForOneSeller = new ExecuteTaskForOneSeller();
        SellerCredentials credentials = CredentialsHelper.getSellerCredentials();
        executeTaskForOneSeller.handleRequest(credentials,null);
    }
}