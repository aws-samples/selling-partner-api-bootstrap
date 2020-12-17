package cn.amazon.aws.rp.spapi.dynamodb;

import cn.amazon.aws.rp.spapi.clients.model.OrderList;

public interface IOrdersDao {
     void put(OrderList orders, String sellerId);
}
