package cn.amazon.aws.rp.spapi.dynamodb;

import cn.amazon.aws.rp.spapi.clients.model.Order;
import cn.amazon.aws.rp.spapi.clients.model.OrderList;
import cn.amazon.aws.rp.spapi.dynamodb.impl.OrdersDao;
import org.junit.Ignore;
import org.junit.Test;

public class OrdersDaoDdbIT {

    @Test
    @Ignore
    public void put() {
        final OrdersDao ordersDaoDdb = new OrdersDao();
        Order o1 = new Order();
        Order o2 = new Order();
        Order o3 = new Order();
        Order o4 = new Order();
        Order o5 = new Order();

        o1 = o1.amazonOrderId("1");
        o2 = o2.amazonOrderId("2");
        o3 = o3.amazonOrderId("3");
        o4 = o4.amazonOrderId("4");
        o5 = o5.amazonOrderId("5");

        final OrderList orders = new OrderList();

        orders.add(o1);
        orders.add(o2);
        orders.add(o3);
        orders.add(o4);
        orders.add(o5);
        ordersDaoDdb.put(orders, "sellerJim");
    }
}