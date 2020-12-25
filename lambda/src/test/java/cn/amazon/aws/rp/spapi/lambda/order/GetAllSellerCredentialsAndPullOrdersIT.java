package cn.amazon.aws.rp.spapi.lambda.order;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GetAllSellerCredentialsAndPullOrdersIT {

    @Test
    void handleRequest() {
        GetAllSellerCredentialsAndPullOrders getAllSellerCredentialsAndPullOrders = new GetAllSellerCredentialsAndPullOrders();
        getAllSellerCredentialsAndPullOrders.handleRequest(null,null);
    }
}