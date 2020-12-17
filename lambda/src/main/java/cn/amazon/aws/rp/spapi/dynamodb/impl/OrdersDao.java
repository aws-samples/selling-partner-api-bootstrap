package cn.amazon.aws.rp.spapi.dynamodb.impl;

import cn.amazon.aws.rp.spapi.dynamodb.IOrdersDao;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cn.amazon.aws.rp.spapi.clients.model.Order;
import cn.amazon.aws.rp.spapi.clients.model.OrderList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrdersDao implements IOrdersDao {

    private static final Logger logger = LoggerFactory.getLogger(SpApiSecretDao.class);
    private static final AmazonDynamoDB DDB = AmazonDynamoDBClientBuilder.standard().build();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String TABLE_NAME = "amz_sp_api_orders";
    private static String TABLE_P_KEY = "amazonOrderId";
    private static String TABLE_SORT_KEY = "orderSellerId";

    private DynamoDB dynamoDB;
    private Table table;

    public OrdersDao() {
        updateTableName();
        dynamoDB = new DynamoDB(DDB);
        table = dynamoDB.getTable(TABLE_NAME);
    }

    /**
     * Use the document API for DDB
     * - ref https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Programming.SDKs.Interfaces.Document.html
     *
     * @param orders
     * @param sellerId
     */
    @Override
    public void put(OrderList orders, String sellerId) {

        orders.forEach(
                order -> putOne(order, sellerId)
        );
    }

    private void putOne(Order order, String sellerId) {
        try {
            final PutItemOutcome putResult = table.putItem(new Item()
                    .withPrimaryKey(TABLE_P_KEY, order.getAmazonOrderId(), TABLE_SORT_KEY, sellerId)
                    .withJSON("orderDetails", gson.toJson(order)));
            logger.info("PutItem succeeded: ");
        } catch (Exception e) {
            logger.error("Cannot save order to dynamodb! ", e);
        }
    }

    private static String updateTableName() {
        // Update the table name from environment. It is expected to be set by CDK script on Lambda.
        final String tableName = System.getenv("DYNAMODB_ORDERS_TABLE");
        if (tableName != null) {
            TABLE_NAME = tableName;
        }
        return TABLE_NAME;
    }
}
