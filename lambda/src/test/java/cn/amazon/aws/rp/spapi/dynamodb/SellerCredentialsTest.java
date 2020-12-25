package cn.amazon.aws.rp.spapi.dynamodb;

import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;

import static org.junit.Assert.*;

public class SellerCredentialsTest {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Test
    public void testGsonConvert() {
        final SellerCredentials sellerCredentials = new SellerCredentials();
        sellerCredentials.setSeller_id("test");
//        sellerCredentials.setAWSAuthenticationCredentials_AK("ak");
//        sellerCredentials.setAWSAuthenticationCredentials_SK("sk");
//        sellerCredentials.setAWSAuthenticationCredentialsProvider_roleArn("arn");
        final SellerCredentials sellerCredentials1 = gson.fromJson(gson.toJson(sellerCredentials), SellerCredentials.class);

        assertEquals(sellerCredentials.getSeller_id(), sellerCredentials1.getSeller_id());
//        assertEquals(sellerCredentials.getAWSAuthenticationCredentials_AK(), sellerCredentials1.getAWSAuthenticationCredentials_AK());
//        assertEquals(sellerCredentials.getAWSAuthenticationCredentials_SK(), sellerCredentials1.getAWSAuthenticationCredentials_SK());
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