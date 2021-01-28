package cn.amazon.aws.rp.spapi.lambda.order;

import cn.amazon.aws.rp.spapi.utils.CredentialsHelper;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import org.junit.jupiter.api.Test;

class GetOrderListForOneSellerIT {

    @Test
    void handleRequest() {
        GetOrderListForOneSeller getOrderListForOneSeller = new GetOrderListForOneSeller();
        SellerCredentials credentials = CredentialsHelper.getSellerCredentials();
        getOrderListForOneSeller.handleRequest(credentials,null);

    }
}