package cn.amazon.aws.rp.spapi.dynamodb.impl;

import cn.amazon.aws.rp.spapi.dynamodb.ISpApiSecretDao;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.utils.Utils;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Samples for more operations - https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/java/example_code/dynamodb/src/main/java/aws/example/dynamodb
 */
public class SpApiSecretDao implements ISpApiSecretDao {

    private static final Logger logger = LoggerFactory.getLogger(SpApiSecretDao.class);
    private static final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
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
            Map<String, AttributeValue> returned_item = client.getItem(request).getItem();
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
    public SellerCredentials getSecretsVOForSeller(String sellerId) {
        logger.info("enter");
        final DynamoDBMapper dbm = new DynamoDBMapper(client);
        return dbm.load(SellerCredentials.class, sellerId);
    }

    @Override
    public List<SellerCredentials> getSecretsVOForAllSeller() {
        logger.info("enter");
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        final DynamoDBMapper dbm = new DynamoDBMapper(client);
        return dbm.scan(SellerCredentials.class, scanExpression);
    }
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void updateSellerCredentials(SellerCredentials sellerCredentials) {
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        mapper.save(sellerCredentials);
    }

    public static String getSellerSecretsTableName() {
        // Update the table name from environment. It is expected to be set by CDK script on Lambda.
        final String tableName = Utils.getEnv("DYNAMODB_SECRETS_TABLE");
        logger.info("dynamodb secrets table name is {}", tableName);
        if (tableName != null) {
            TABLE_NAME = tableName;
        }
        return TABLE_NAME;
    }

    private static final ISpApiSecretDao secretsDao = new SpApiSecretDao();

    public static List<SellerCredentials> getSellerCredentials() {
        // Concurrent invocation for all sellers - still constrained by underline number of threads.
        final List<SellerCredentials> secretsVOForAllSeller = secretsDao.getSecretsVOForAllSeller();
        logger.info("found seller size: " + secretsVOForAllSeller.size());
        return secretsVOForAllSeller;
    }

    public static void updateMarketplace(SellerCredentials sellerCredentials) {
        // Concurrent invocation for all sellers - still constrained by underline number of threads.
         secretsDao.updateSellerCredentials(sellerCredentials);
    }

}
