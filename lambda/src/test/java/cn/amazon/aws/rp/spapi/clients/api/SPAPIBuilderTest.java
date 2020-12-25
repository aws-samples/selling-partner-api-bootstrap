package cn.amazon.aws.rp.spapi.clients.api;

import cn.amazon.aws.rp.spapi.dynamodb.entity.LWACredentials;
import cn.amazon.aws.rp.spapi.utils.Utils;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SPAPIBuilderTest {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    @Test
    @Ignore
    public void testStatic() {
        String lwaCredentialsJson = "{\n" +
                "  \"clientId\": \"amzn1.application-oa2-client.XXX\",\n" +
                "  \"clientSecret\": \"XXX\"\n" +
                "}";
        LWACredentials lwaCredentials = gson.fromJson(lwaCredentialsJson, LWACredentials.class);
        assertNotNull(lwaCredentials);

//        AWSSimpleSystemsManagement simpleSystemsManagementClient = AWSSimpleSystemsManagementClientBuilder.defaultClient();
//
//         lwaCredentialsJson = simpleSystemsManagementClient.getParameter(new GetParameterRequest()
//                .withName(Utils.getEnv("SELLER_CENTRAL_APP_CREDENTIALS","seller_central_app_credentials")).
//                        withWithDecryption(true)).getParameter().getValue();
//         lwaCredentials = gson.fromJson(lwaCredentialsJson, LWACredentials.class);
//        assertNotNull(lwaCredentials);
    }
}