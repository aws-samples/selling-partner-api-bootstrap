package cn.amazon.aws.rp.spapi.utils;

import cn.amazon.aws.rp.spapi.clients.api.*;
import cn.amazon.aws.rp.spapi.dynamodb.ISpApiSecretDao;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerSecretsVO;
import cn.amazon.aws.rp.spapi.dynamodb.impl.SpApiSecretDao;
import com.amazon.SellingPartnerAPIAA.*;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Helper {
    static private final Logger logger = LoggerFactory.getLogger(Helper.class);

    public static void logInput(Logger logger, Object event, Context context, Gson gson) {
        logger.info("Env: " + gson.toJson(System.getenv()));
        logger.info("Ctx: " + gson.toJson(context));
        logger.info("Evt: " + gson.toJson(event));
    }

    /**
     * Support async invoke.
     *
     * @param fName
     * @param jsonInput
     */
    public static void invokeLambda(String fName, String jsonInput, Boolean async) {

        logger.info("invoking: " + fName);
        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(fName)
                .withPayload(jsonInput);
        if (async) {
            invokeRequest.setInvocationType(InvocationType.Event);
        }
        InvokeResult invokeResult = null;

        try {
            AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                    .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                    .build();

            invokeResult = awsLambda.invoke(invokeRequest);
            logger.info("invocation completed");
        } catch (ServiceException e) {
            logger.error("cannot invoke function ", e);
            throw e;
        }
    }

    private static Calendar calendar = Calendar.getInstance();

    /**
     * @param aheadSec if giving 0 then return current time string in iso format. If giving 1 then return the
     *                 time 1 second before current.
     * @return
     */
    public static String getIso8601Time(int aheadSec) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        final Date current = new Date();
        calendar.setTime(current);
        calendar.add(Calendar.SECOND, -aheadSec);

        return df.format(calendar.getTime());
    }

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // To share the cache.
    public static final LWAAccessTokenCacheImpl LWA_ACCESS_TOKEN_CACHE = new LWAAccessTokenCacheImpl();

    /**
     * Authorize for the API call.
     * TODO - share the token cache with other functions.
     *
     * @param jsonSellerSecrets
     * @return
     */
    public static SellersApi buildSellerApi(SellerSecretsVO jsonSellerSecrets) {

        SellersApi.Builder builder = new SellersApi.Builder();
        return builder.awsAuthenticationCredentials(getAwsAuthenticationCredentials(jsonSellerSecrets))
                .awsAuthenticationCredentialsProvider(getAwsAuthenticationCredentialsProvider(jsonSellerSecrets))
                .lwaAuthorizationCredentials(getLwaAuthorizationCredentials(jsonSellerSecrets))
                .endpoint("https://sellingpartnerapi-na.amazon.com")
                .lwaAccessTokenCache(LWA_ACCESS_TOKEN_CACHE)
                .build();
    }

    public static OrdersApi buildOrdersApi(SellerSecretsVO jsonSellerSecrets) {

        OrdersApi.Builder builder = new OrdersApi.Builder();
        final AWSAuthenticationCredentials awsAuthenticationCredentials = getAwsAuthenticationCredentials(jsonSellerSecrets);
        final AWSAuthenticationCredentialsProvider awsAuthenticationCredentialsProvider = getAwsAuthenticationCredentialsProvider(jsonSellerSecrets);
        final LWAAuthorizationCredentials lwaAuthorizationCredentials = getLwaAuthorizationCredentials(jsonSellerSecrets);
        return builder.awsAuthenticationCredentials(awsAuthenticationCredentials)
                .awsAuthenticationCredentialsProvider(awsAuthenticationCredentialsProvider)
                .lwaAuthorizationCredentials(lwaAuthorizationCredentials)
                .endpoint("https://sellingpartnerapi-na.amazon.com")
                .lwaAccessTokenCache(LWA_ACCESS_TOKEN_CACHE)
                .build();
    }

    public static FinancesApi buildFinancesApi(SellerSecretsVO jsonSellerSecrets) {

        FinancesApi.Builder builder = new FinancesApi.Builder();
        final AWSAuthenticationCredentials awsAuthenticationCredentials = getAwsAuthenticationCredentials(jsonSellerSecrets);
        final AWSAuthenticationCredentialsProvider awsAuthenticationCredentialsProvider = getAwsAuthenticationCredentialsProvider(jsonSellerSecrets);
        final LWAAuthorizationCredentials lwaAuthorizationCredentials = getLwaAuthorizationCredentials(jsonSellerSecrets);
        return builder.awsAuthenticationCredentials(awsAuthenticationCredentials)
                .awsAuthenticationCredentialsProvider(awsAuthenticationCredentialsProvider)
                .lwaAuthorizationCredentials(lwaAuthorizationCredentials)
                .endpoint("https://sellingpartnerapi-na.amazon.com")
                .lwaAccessTokenCache(LWA_ACCESS_TOKEN_CACHE)
                .build();
    }


    public static ReportsApi buildReportsApi(SellerSecretsVO jsonSellerSecrets) {

        ReportsApi.Builder builder = new ReportsApi.Builder();
        final AWSAuthenticationCredentials awsAuthenticationCredentials = getAwsAuthenticationCredentials(jsonSellerSecrets);
        final AWSAuthenticationCredentialsProvider awsAuthenticationCredentialsProvider = getAwsAuthenticationCredentialsProvider(jsonSellerSecrets);
        final LWAAuthorizationCredentials lwaAuthorizationCredentials = getLwaAuthorizationCredentials(jsonSellerSecrets);
        return builder.awsAuthenticationCredentials(awsAuthenticationCredentials)
                .awsAuthenticationCredentialsProvider(awsAuthenticationCredentialsProvider)
                .lwaAuthorizationCredentials(lwaAuthorizationCredentials)
                .endpoint("https://sellingpartnerapi-na.amazon.com")
                .lwaAccessTokenCache(LWA_ACCESS_TOKEN_CACHE)
                .build();
    }

    private static AWSAuthenticationCredentials getAwsAuthenticationCredentials(SellerSecretsVO jsonSellerSecrets) {
        return AWSAuthenticationCredentials
                .builder()
                .accessKeyId(jsonSellerSecrets.getAWSAuthenticationCredentials_AK())
                .secretKey(jsonSellerSecrets.getAWSAuthenticationCredentials_SK())
                .region("us-east-1") // TODO make this configurable
                .build();
    }

    private static LWAAuthorizationCredentials getLwaAuthorizationCredentials(SellerSecretsVO jsonSellerSecrets) {
        return LWAAuthorizationCredentials
                .builder()
                .clientId(jsonSellerSecrets.getlWAAuthorizationCredentials_clientid())
                .clientSecret(jsonSellerSecrets.getLWAAuthorizationCredentials_clientSecret())
                .refreshToken(jsonSellerSecrets.getLWAAuthorizationCredentials_refreshToken())
                .endpoint("https://api.amazon.com/auth/o2/token")
                .build();
    }

    public static AWSAuthenticationCredentialsProvider getAwsAuthenticationCredentialsProvider(SellerSecretsVO jsonSellerSecrets) {
        return AWSAuthenticationCredentialsProvider
                .builder()
                .roleArn(jsonSellerSecrets.getAWSAuthenticationCredentialsProvider_roleArn())
                .roleSessionName(jsonSellerSecrets.getSeller_id())
                .build();
    }

    /**
     * Notification API uses the scope instead of the authorization code.
     * The Selling Partner API scopes can be retrieved from the ScopeConstants class and passed as argument(s) to either the withScope(String scope) or withScopes(String... scopes) method during lwaAuthorizationCredentials object instantiation.
     * <p>
     * import static com.amazon.SellingPartnerAPIAA.ScopeConstants.SCOPE_NOTIFICATIONS_API;
     * <p>
     * LWAAuthorizationCredentials lwaAuthorizationCredentials = LWAAuthorizationCredentials.builder()
     * .clientId("...")
     * .clientSecret("...")
     * .withScopes("...")
     * .endpoint("...")
     * .build();
     */
    public static NotificationsApi buildNotificationGrantLessApi(SellerSecretsVO jsonSellerSecrets) {

        NotificationsApi.Builder builder = new NotificationsApi.Builder();
        return builder.awsAuthenticationCredentials(AWSAuthenticationCredentials
                .builder()
                .accessKeyId(jsonSellerSecrets.getAWSAuthenticationCredentials_AK())
                .secretKey(jsonSellerSecrets.getAWSAuthenticationCredentials_SK())
                .region("us-east-1") // TODO make this configurable
                .build()
        )
                .awsAuthenticationCredentialsProvider(AWSAuthenticationCredentialsProvider
                        .builder()
                        .roleArn(jsonSellerSecrets.getAWSAuthenticationCredentialsProvider_roleArn())
                        .roleSessionName(jsonSellerSecrets.getSeller_id())
                        .build()
                )
                .lwaAuthorizationCredentials(LWAAuthorizationCredentials
                        .builder()
                        .clientId(jsonSellerSecrets.getlWAAuthorizationCredentials_clientid())
                        .clientSecret(jsonSellerSecrets.getLWAAuthorizationCredentials_clientSecret())
                        .withScope(ScopeConstants.SCOPE_NOTIFICATIONS_API) // No refresh token is needed.
                        .endpoint("https://api.amazon.com/auth/o2/token")
                        .build()
                )
                .endpoint("https://sellingpartnerapi-na.amazon.com")
                .lwaAccessTokenCache(LWA_ACCESS_TOKEN_CACHE)
                .build();
    }

    public static NotificationsApi buildNotificationApi(SellerSecretsVO jsonSellerSecrets) {
        NotificationsApi.Builder builder = new NotificationsApi.Builder();
        return builder.awsAuthenticationCredentials(getAwsAuthenticationCredentials(jsonSellerSecrets))
                .awsAuthenticationCredentialsProvider(getAwsAuthenticationCredentialsProvider(jsonSellerSecrets))
                .lwaAuthorizationCredentials(getLwaAuthorizationCredentials(jsonSellerSecrets))
                .endpoint("https://sellingpartnerapi-na.amazon.com")
                .lwaAccessTokenCache(LWA_ACCESS_TOKEN_CACHE)
                .build();
    }

    /**
     * First get the seller secrets by sellerID from DB.
     *
     * @param sellerId
     * @return
     */
    public static OrdersApi buildOrdersApi(String sellerId) {
        final SellerSecretsVO secretsVO = getSellerSecret(sellerId);
        return buildOrdersApi(secretsVO);
    }

    private static SellerSecretsVO getSellerSecret(String sellerId) {
        final ISpApiSecretDao ssDao = new SpApiSecretDao();
        return ssDao.getSecretsVOForSeller(sellerId);
    }

    private static final ISpApiSecretDao secretsDao = new SpApiSecretDao();

    public static List<SellerSecretsVO> getSellerSecretsVOS() {
        // Concurrent invocation for all sellers - still constrained by underline number of threads.
        final List<SellerSecretsVO> secretsVOForAllSeller = secretsDao.getSecretsVOForAllSeller();
        logger.info("found seller size: " + secretsVOForAllSeller.size());
        return secretsVOForAllSeller;
    }

}
