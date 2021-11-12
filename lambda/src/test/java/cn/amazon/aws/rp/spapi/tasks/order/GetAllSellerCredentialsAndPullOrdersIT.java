package cn.amazon.aws.rp.spapi.tasks.order;

import org.junit.jupiter.api.Test;

class GetAllSellerCredentialsAndPullOrdersIT {

    @Test
    void handleRequest() {
        GetAllSellerCredentialsAndPullOrders getAllSellerCredentialsAndPullOrders = new GetAllSellerCredentialsAndPullOrders();
        getAllSellerCredentialsAndPullOrders.handleRequest(null,null);
    }
}