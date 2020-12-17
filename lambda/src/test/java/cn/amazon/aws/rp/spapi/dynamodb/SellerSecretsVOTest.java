package cn.amazon.aws.rp.spapi.dynamodb;

import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerSecretsVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;

import static org.junit.Assert.*;

public class SellerSecretsVOTest {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Test
    public void testGsonConvert() {
        final SellerSecretsVO sellerSecretsVO = new SellerSecretsVO();
        sellerSecretsVO.setSeller_id("test");
        sellerSecretsVO.setAWSAuthenticationCredentials_AK("ak");
        sellerSecretsVO.setAWSAuthenticationCredentials_SK("sk");
        sellerSecretsVO.setAWSAuthenticationCredentialsProvider_roleArn("arn");
        final SellerSecretsVO sellerSecretsVO1 = gson.fromJson(gson.toJson(sellerSecretsVO), SellerSecretsVO.class);

        assertEquals(sellerSecretsVO.getSeller_id(), sellerSecretsVO1.getSeller_id());
        assertEquals(sellerSecretsVO.getAWSAuthenticationCredentials_AK(), sellerSecretsVO1.getAWSAuthenticationCredentials_AK());
        assertEquals(sellerSecretsVO.getAWSAuthenticationCredentials_SK(), sellerSecretsVO1.getAWSAuthenticationCredentials_SK());
    }

    @Test
    public void testSplit() {

        int size =17;
        final int batch = size / 10;
        final int left = size % 10;
        for (int i = 0; i < batch; i++) {
            System.out.println(">>> from " + i * 10 + ",to " + (i + 1) * 10);
        }
        System.out.println(">>> left "+ left);
        System.out.println(">>> exit");
    }

}