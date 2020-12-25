package cn.amazon.aws.rp.spapi.dynamodb.entity;

import cn.amazon.aws.rp.spapi.clients.model.Marketplace;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.List;
import java.util.Objects;


/**
 * Notice:
 *      Due to the java convention the dynaomodb table fields name MUST stat with SMALL letters.
 *      Or the mapping will fail.
 * TODO avoid hard code table name https://github.com/aws/aws-sdk-java/issues/1681
 */
@DynamoDBTable(tableName = "spapi-secrets")
public class SellerCredentials {

    @DynamoDBHashKey
    private String seller_id;

    private String lWAAuthorizationCredentials_refreshToken;

    /*
        us-east-1 NA
        eu-west-1 EU
        us-west-2 FE
         */
    private String region = "us-east-1";

    private List<Marketplace>  marketplaces;

    @DynamoDBAttribute
    public List<Marketplace> getMarketplaces() {
        return marketplaces;
    }

    public void setMarketplaces(List<Marketplace> marketplaces) {
        this.marketplaces = marketplaces;
    }


    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    @DynamoDBAttribute
    public String getLWAAuthorizationCredentials_refreshToken() {
        return lWAAuthorizationCredentials_refreshToken;
    }

    public void setLWAAuthorizationCredentials_refreshToken(String LWAAuthorizationCredentials_refreshToken) {
        this.lWAAuthorizationCredentials_refreshToken = LWAAuthorizationCredentials_refreshToken;
    }

    @DynamoDBAttribute
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SellerCredentials that = (SellerCredentials) o;
        return Objects.equals(seller_id, that.seller_id) &&
                Objects.equals(lWAAuthorizationCredentials_refreshToken, that.lWAAuthorizationCredentials_refreshToken) &&
                Objects.equals(region, that.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seller_id, lWAAuthorizationCredentials_refreshToken, region);
    }


//    @DynamoDBDocument
//    public static class Marketplace {
//        public String getId() {
//            return id;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }
//
//        @DynamoDBAttribute
//        private String id;
//    }
}
