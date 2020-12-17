package cn.amazon.aws.rp.spapi.dynamodb.impl;

import cn.amazon.aws.rp.spapi.dynamodb.ISpApiSecretDao;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerSecretsVO;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Samples for more operations - https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/java/example_code/dynamodb/src/main/java/aws/example/dynamodb
 */
public class SpApiSecretDao implements ISpApiSecretDao {

    private static final Logger logger = LoggerFactory.getLogger(SpApiSecretDao.class);
    private static final AmazonDynamoDB DDB = AmazonDynamoDBClientBuilder.standard().build();
    public static String TABLE_NAME = "spapi-secrets";
    private static String TABLE_PKEY = "seller_id";


    @Deprecated()
    /**
     * User getSecretsVOForSeller()
     */
    public Map<String, AttributeValue> getSecretsForSeller(String sellerId) {

        logger.debug("enter");
        getSellerSecretsTableName();

        HashMap<String, AttributeValue> key_to_get = new HashMap<String, AttributeValue>();

        key_to_get.put(TABLE_PKEY, new AttributeValue(sellerId));

        final GetItemRequest request = new GetItemRequest().withKey(key_to_get).withTableName(TABLE_NAME);

        try {
            Map<String, AttributeValue> returned_item = DDB.getItem(request).getItem();
            if (returned_item != null) {
                Set<String> keys = returned_item.keySet();
                for (String key : keys) {
                    logger.debug("%s: %s\n", key, returned_item.get(key).toString());
                }
                return returned_item;
            } else {
                logger.debug("No item found with the key %s!\n", sellerId);
                return new HashMap<String, AttributeValue>();
            }
        } catch (AmazonServiceException e) {
            logger.error("cannot read seller secrets", e);
        }
        return null; // Should be an exception....
    }

    @Override
    public SellerSecretsVO getSecretsVOForSeller(String sellerId) {
        logger.info("enter");
        final DynamoDBMapper dbm = new DynamoDBMapper(DDB);
        return dbm.load(SellerSecretsVO.class, sellerId);
    }

    @Override
    public List<SellerSecretsVO> getSecretsVOForAllSeller() {
        logger.info("enter");
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        final DynamoDBMapper dbm = new DynamoDBMapper(DDB);
        return dbm.scan(SellerSecretsVO.class, scanExpression);
    }

    public static String getSellerSecretsTableName() {
        // Update the table name from environment. It is expected to be set by CDK script on Lambda.
        final String tableName = System.getenv("DYNAMODB_SECRETS_TABLE");
        if (tableName != null) {
            TABLE_NAME = tableName;
        }
        return TABLE_NAME;
    }

}
