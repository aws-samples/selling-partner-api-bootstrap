package cn.amazon.aws.rp.spapi.dynamodb;

import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerSecretsVO;
import cn.amazon.aws.rp.spapi.dynamodb.impl.SpApiSecretDao;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SpApiSecretDaoDdbTest {

    private static final String K1 = "AWSAuthenticationCredentials_AK";
    private static final String K2 = "AWSAuthenticationCredentials_SK";
    private static final String K3 = "AWSAuthenticationCredentialsProvider_roleArn";
    private static final String K4 = "LWAAuthorizationCredentials_clientid";
    private static final String K5 = "LWAAuthorizationCredentials_refreshToken";
    private static final String K6 = "LWAAuthorizationCredentials_clientSecret";

    private static final SpApiSecretDao spApiSecretReader = new SpApiSecretDao();

    @Test
    public void getSecretsForSeller() {
        final Map<String, AttributeValue> seller_jim = spApiSecretReader.getSecretsForSeller("seller_jim");

        assertNotNull(seller_jim);
        assertNotNull(seller_jim.get(K1));
        assertNotNull(seller_jim.get(K2));
        assertNotNull(seller_jim.get(K3));
        assertNotNull(seller_jim.get(K4));
        assertNotNull(seller_jim.get(K5));
        assertNotNull(seller_jim.get(K6));
    }

    @Test
    public void getSecretsVOForSeller(){
        final SellerSecretsVO seller_jim = spApiSecretReader.getSecretsVOForSeller("seller_jim");

        assertEquals(seller_jim.getSeller_id(), "seller_jim");
        assertNotNull(seller_jim.getAWSAuthenticationCredentials_AK());
    }

    @Test
    public void getSecretsVOForALlSeller(){
        final List<SellerSecretsVO> allSeller = spApiSecretReader.getSecretsVOForAllSeller();
        assertTrue(allSeller.size() > 1);
    }
}