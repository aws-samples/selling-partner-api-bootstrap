package cn.amazon.aws.rp.spapi.lambda.vo;

import cn.amazon.aws.rp.spapi.clients.model.OrderList;

public class OrdersWithSeller {

    private OrderList orders;
    private String sellerId;

    public OrdersWithSeller(OrderList orders, String sellerId) {
        this.orders = orders;
        this.sellerId = sellerId;
    }

    public OrderList getOrders() {
        return orders;
    }

    public void setOrders(OrderList orders) {
        this.orders = orders;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
}
