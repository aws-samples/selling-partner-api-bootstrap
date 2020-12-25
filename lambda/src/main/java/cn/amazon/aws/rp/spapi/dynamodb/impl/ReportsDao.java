package cn.amazon.aws.rp.spapi.dynamodb.impl;

import cn.amazon.aws.rp.spapi.clients.model.MarketplaceParticipation;
import cn.amazon.aws.rp.spapi.dynamodb.IReportsDao;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.enums.ReportStatusEnum;
import cn.amazon.aws.rp.spapi.utils.Utils;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportsDao implements IReportsDao {

    private static final Logger logger = LoggerFactory.getLogger(ReportsDao.class);
    private static final AmazonDynamoDB DDB = AmazonDynamoDBClientBuilder.standard().build();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String TABLE_NAME = "amz_sp_api_request_report";
    private static String TABLE_P_KEY = "reportId";
    private static String TABLE_SORT_KEY = "sellerId";

    private DynamoDB dynamoDB;
    private Table table;

    public ReportsDao() {
        updateTableName();
        dynamoDB = new DynamoDB(DDB);
        table = dynamoDB.getTable(TABLE_NAME);
    }

    /**
     * Use the document API for DDB
     * - ref https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Programming.SDKs.Interfaces.Document.html
     */

    @Override
    public void put(String reportId, MarketplaceParticipation marketplace, SellerCredentials sellerCredentials) {
        try {
            final PutItemOutcome putResult = table.putItem(new Item()
                    .withPrimaryKey(TABLE_P_KEY, reportId, TABLE_SORT_KEY, sellerCredentials.getSeller_id())
                    .withString("countryCode", marketplace.getMarketplace().getCountryCode())
                    .withString("marketplaceId", marketplace.getMarketplace().getId())
                    .withString("reportStatus", ReportStatusEnum.IN_PROGRESS.name())
            );
            logger.info("report putItem succeeded: {}", gson.toJson(putResult));
        } catch (Exception e) {
            logger.error("Cannot save order to dynamodb! ", e);
        }
    }

    private static String updateTableName() {
        // Update the table name from environment. It is expected to be set by CDK script on Lambda.
        final String tableName = Utils.getEnv("DYNAMODB_REPORTS_TABLE");
        if (tableName != null) {
            TABLE_NAME = tableName;
        }
        return TABLE_NAME;
    }

}
