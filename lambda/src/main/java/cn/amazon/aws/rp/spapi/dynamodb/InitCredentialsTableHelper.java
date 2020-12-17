package cn.amazon.aws.rp.spapi.dynamodb;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;

import java.util.HashMap;

/**
 * This class will save default secrets to DDB. It can optional done by adding from console.
 */
public class InitCredentialsTableHelper {

    private static final AmazonDynamoDB DDB = AmazonDynamoDBClientBuilder.standard().build();
    private static final String TABLE_NAME = "spapi-secrets";

    public void putItem(String tableName, String sellerID) {

        HashMap<String, AttributeValue> item_values = new HashMap<String, AttributeValue>();

        item_values.put("seller_id",
                new AttributeValue(sellerID));

        item_values.put("aWSAuthenticationCredentials_AK",
                new AttributeValue(""));
        item_values.put("aWSAuthenticationCredentials_SK",
                new AttributeValue(""));
        item_values.put("aWSAuthenticationCredentialsProvider_roleArn",
                new AttributeValue(""));
        item_values.put("lWAAuthorizationCredentials_clientid",
                new AttributeValue(""));
        item_values.put("lWAAuthorizationCredentials_clientSecret",
                new AttributeValue(""));
        item_values.put("lWAAuthorizationCredentials_refreshToken",
                new AttributeValue(""));

        try {
            DDB.putItem(tableName, item_values);
        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The table \"%s\" can't be found.\n", tableName);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
        } catch (AmazonServiceException e) {
            System.err.println("ERROR!!");
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR!");
            System.err.println(e.getMessage());
        }
        System.out.println("Done!");
    }

    public static void main(String[] args) {
        final InitCredentialsTableHelper ddb = new InitCredentialsTableHelper();
        ddb.putItem(TABLE_NAME, "seller_jim");
        ddb.putItem(TABLE_NAME, "seller_tom");
        System.out.println("hello");
    }
}
