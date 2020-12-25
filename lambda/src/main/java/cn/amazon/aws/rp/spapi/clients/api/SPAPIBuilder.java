package cn.amazon.aws.rp.spapi.clients.api;

import cn.amazon.aws.rp.spapi.aa.*;
import cn.amazon.aws.rp.spapi.clients.ApiClient;
import cn.amazon.aws.rp.spapi.clients.StringUtil;
import cn.amazon.aws.rp.spapi.dynamodb.entity.LWACredentials;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.enums.AppstoreEndpoint;
import cn.amazon.aws.rp.spapi.utils.Utils;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SPAPIBuilder<T> {

    private static final Logger logger = LoggerFactory.getLogger(SPAPIBuilder.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private LWAAuthorizationCredentials lwaAuthorizationCredentials;
    private String endpoint;
    private Boolean disableAccessTokenCache = false;
    private AWSAuthenticationCredentialsProvider awsAuthenticationCredentialsProvider;
    // To share the cache.
    private static final LWAAccessTokenCacheImpl lwaAccessTokenCache = new LWAAccessTokenCacheImpl();

    protected ApiClient apiClient;
    static final LWACredentials lwaCredentials;
    static {
        AWSSimpleSystemsManagement simpleSystemsManagementClient = AWSSimpleSystemsManagementClientBuilder.standard().build();
        String lwaCredentialsJson = simpleSystemsManagementClient.getParameter(new GetParameterRequest()
                .withName(Utils.getEnv("SELLER_CENTRAL_APP_CREDENTIALS","seller_central_app_credentials")).
                        withWithDecryption(true)).getParameter().getValue();
        lwaCredentials = gson.fromJson(lwaCredentialsJson, LWACredentials.class);

    }

    protected LWAAuthorizationCredentials getLwaAuthorizationCredentials(SellerCredentials jsonSellerSecrets) {

        return LWAAuthorizationCredentials
                .builder()
                .clientId(lwaCredentials.getClientId())
                .clientSecret(lwaCredentials.getClientSecret())
                .refreshToken(jsonSellerSecrets.getLWAAuthorizationCredentials_refreshToken())
                .endpoint("https://api.amazon.com/auth/o2/token")
                .build();
    }

    private SPAPIBuilder lwaAuthorizationCredentials(LWAAuthorizationCredentials lwaAuthorizationCredentials) {
        this.lwaAuthorizationCredentials = lwaAuthorizationCredentials;
        return this;
    }

    private SPAPIBuilder endpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    private SPAPIBuilder buildCredentials(SellerCredentials sellerCredentials) {
        return this
                .lwaAuthorizationCredentials(getLwaAuthorizationCredentials(sellerCredentials))
                .endpoint(AppstoreEndpoint.fromRegion(sellerCredentials.getRegion()).getEndpoint());
    }

//    private SPAPIBuilder lwaAccessTokenCache(LWAAccessTokenCache lwaAccessTokenCache) {
//        this.lwaAccessTokenCache = lwaAccessTokenCache;
//        return this;
//    }

    public SPAPIBuilder disableAccessTokenCache() {
        this.disableAccessTokenCache = true;
        return this;
    }

    protected void buildAuth(SellerCredentials sellerCredentials) throws NoSuchFieldException, IllegalAccessException {
        buildCredentials(sellerCredentials);

        if (lwaAuthorizationCredentials == null) {
            throw new RuntimeException("LWAAuthorizationCredentials not set");
        }

        if (StringUtil.isEmpty(endpoint)) {
            throw new RuntimeException("Endpoint not set");
        }

        AWSSigV4Signer awsSigV4Signer = new AWSSigV4Signer(sellerCredentials.getRegion());

//        try {
//            awsSigV4Signer = new AWSRoleSigV4Signer(jsonSellerSecrets.getRegion());
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//            logger.error(e.getMessage());
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//            logger.error(e.getMessage());
//        }
        LWAAuthorizationSigner lwaAuthorizationSigner = new LWAAuthorizationSigner(lwaAuthorizationCredentials, lwaAccessTokenCache);

//        LWAAuthorizationSigner lwaAuthorizationSigner = null;
//        if (disableAccessTokenCache) {
//            lwaAuthorizationSigner = new LWAAuthorizationSigner(lwaAuthorizationCredentials);
//        } else {
////            if (lwaAccessTokenCache == null) {
////                lwaAccessTokenCache = new LWAAccessTokenCacheImpl();
////            }
//            lwaAuthorizationSigner = new LWAAuthorizationSigner(lwaAuthorizationCredentials, lwaAccessTokenCache);
//        }

        apiClient = new ApiClient()
                .setAWSSigV4Signer(awsSigV4Signer)
                .setLWAAuthorizationSigner(lwaAuthorizationSigner)
                .setBasePath(endpoint);
    }

    public abstract T build(SellerCredentials jsonSellerSecrets) throws NoSuchFieldException, IllegalAccessException;


}
