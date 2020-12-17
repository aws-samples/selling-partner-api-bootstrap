package cn.amazon.aws.rp.spapi.dynamodb.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;


/**
 * Notice:
 *      Due to the java convention the dynaomodb table fields name MUST stat with SMALL letters.
 *      Or the mapping will fail.
 * TODO avoid hard code table name https://github.com/aws/aws-sdk-java/issues/1681
 */
@DynamoDBTable(tableName = "spapi-secrets")
public class SellerSecretsVO {

    @DynamoDBHashKey
    private String seller_id;
    /**
     * Your AWS account AK
     */
    private String aWSAuthenticationCredentials_AK;
    /**
     * Your AWS account SK
     */
    private String aWSAuthenticationCredentials_SK;
    /**
     * The role created at AWS account
     */
    private String aWSAuthenticationCredentialsProvider_roleArn;
    private String lWAAuthorizationCredentials_clientid;
    private String lWAAuthorizationCredentials_refreshToken;
    private String lWAAuthorizationCredentials_clientSecret;

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    @DynamoDBAttribute
    public String getAWSAuthenticationCredentials_AK() {
        return aWSAuthenticationCredentials_AK;
    }

    public void setAWSAuthenticationCredentials_AK(String aWSAuthenticationCredentials_AK) {
        this.aWSAuthenticationCredentials_AK = aWSAuthenticationCredentials_AK;
    }

    @DynamoDBAttribute
    public String getAWSAuthenticationCredentials_SK() {
        return aWSAuthenticationCredentials_SK;
    }

    public void setAWSAuthenticationCredentials_SK(String AWSAuthenticationCredentials_SK) {
        this.aWSAuthenticationCredentials_SK = AWSAuthenticationCredentials_SK;
    }

    @DynamoDBAttribute
    public String getAWSAuthenticationCredentialsProvider_roleArn() {
        return aWSAuthenticationCredentialsProvider_roleArn;
    }

    public void setAWSAuthenticationCredentialsProvider_roleArn(String AWSAuthenticationCredentialsProvider_roleArn) {
        this.aWSAuthenticationCredentialsProvider_roleArn = AWSAuthenticationCredentialsProvider_roleArn;
    }

    @DynamoDBAttribute
    public String getlWAAuthorizationCredentials_clientid() {
        return lWAAuthorizationCredentials_clientid;
    }

    public void setlWAAuthorizationCredentials_clientid(String lWAAuthorizationCredentials_clientid) {
        this.lWAAuthorizationCredentials_clientid = lWAAuthorizationCredentials_clientid;
    }

    @DynamoDBAttribute
    public String getLWAAuthorizationCredentials_refreshToken() {
        return lWAAuthorizationCredentials_refreshToken;
    }

    public void setLWAAuthorizationCredentials_refreshToken(String LWAAuthorizationCredentials_refreshToken) {
        this.lWAAuthorizationCredentials_refreshToken = LWAAuthorizationCredentials_refreshToken;
    }

    @DynamoDBAttribute
    public String getLWAAuthorizationCredentials_clientSecret() {
        return lWAAuthorizationCredentials_clientSecret;
    }

    public void setLWAAuthorizationCredentials_clientSecret(String LWAAuthorizationCredentials_clientSecret) {
        this.lWAAuthorizationCredentials_clientSecret = LWAAuthorizationCredentials_clientSecret;
    }
}
