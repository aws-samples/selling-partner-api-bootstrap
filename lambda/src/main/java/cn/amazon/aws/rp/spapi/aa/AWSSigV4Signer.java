package cn.amazon.aws.rp.spapi.aa;



import cn.amazon.aws.rp.spapi.dynamodb.impl.SpApiSecretDao;
import cn.amazon.aws.rp.spapi.utils.Utils;
import com.amazonaws.SignableRequest;
import com.amazonaws.auth.*;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.squareup.okhttp.Request;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * AWS Signature Version 4 Signer
 */
public class AWSSigV4Signer {
    private static final String SERVICE_NAME = "execute-api";

    private static final Logger logger = LoggerFactory.getLogger(AWSSigV4Signer.class);

    @Setter(AccessLevel.PACKAGE)
    @Getter(AccessLevel.PACKAGE)
    private AWS4Signer aws4Signer;

    private AWSCredentials awsCredentials;

    @Setter(AccessLevel.PACKAGE)
    @Getter(AccessLevel.PACKAGE)
    private AWSCredentialsProvider awsCredentialsProvider;

//    /**
//     *
//     * @param awsAuthenticationCredentials AWS Developer Account Credentials
//     */
//    public AWSSigV4Signer(AWSAuthenticationCredentials awsAuthenticationCredentials) {
//        aws4Signer = new AWS4Signer();
//        aws4Signer.setServiceName(SERVICE_NAME);
//        aws4Signer.setRegionName(awsAuthenticationCredentials.getRegion());
//        awsCredentials = new BasicAWSCredentials(awsAuthenticationCredentials.getAccessKeyId(),
//                awsAuthenticationCredentials.getSecretKey());
//    }

//    /**
//     *
//     * @param awsAuthenticationCredentials and awsAuthenticationCredentialsProvider AWS Developer Account Credentials
//     */
//    public AWSSigV4Signer(AWSAuthenticationCredentials awsAuthenticationCredentials,
//                          AWSAuthenticationCredentialsProvider awsAuthenticationCredentialsProvider) {
//        aws4Signer = new AWS4Signer();
//        aws4Signer.setServiceName(SERVICE_NAME);
//        aws4Signer.setRegionName(awsAuthenticationCredentials.getRegion());
//        BasicAWSCredentials awsBasicCredentials = new BasicAWSCredentials(awsAuthenticationCredentials.getAccessKeyId(),
//                awsAuthenticationCredentials.getSecretKey());
//        awsCredentialsProvider = new STSAssumeRoleSessionCredentialsProvider.Builder(
//                awsAuthenticationCredentialsProvider.getRoleArn(),
//                awsAuthenticationCredentialsProvider.getRoleSessionName())
//                .withStsClient(AWSSecurityTokenServiceClientBuilder.standard()
//                        .withRegion(awsAuthenticationCredentials.getRegion())
//                        .withCredentials(new AWSStaticCredentialsProvider(awsBasicCredentials)).build())
//                .build();
//    }

    public AWSSigV4Signer(String region) throws NoSuchFieldException, IllegalAccessException {

        aws4Signer = new AWS4Signer();
        aws4Signer.setServiceName(SERVICE_NAME);
        aws4Signer.setRegionName(region);

        String roleArn = Utils.getEnv("Role");
        logger.info(">>>  role is >>> {}", roleArn);

        AWSCredentialsProvider awsCredentialsProvider = DefaultAWSCredentialsProviderChain.getInstance();
        AWSCredentialsProvider assumeCredentialsProvider =
                (new STSAssumeRoleSessionCredentialsProvider.Builder(roleArn, "sessionName"))
                        .withStsClient(AWSSecurityTokenServiceClientBuilder.standard().withRegion(region)
                                .withCredentials(awsCredentialsProvider).build()).build();
        this.awsCredentialsProvider = assumeCredentialsProvider;
    }

    /**
     *  Signs a Request with AWS Signature Version 4
     *
     * @param originalRequest Request to sign (treated as immutable)
     * @return Copy of originalRequest with AWS Signature
     */
    public Request sign(Request originalRequest) {
        SignableRequest<Request> signableRequest = new SignableRequestImpl(originalRequest);
        if (awsCredentialsProvider != null) {
            aws4Signer.sign(signableRequest, awsCredentialsProvider.getCredentials());
        } else {
            aws4Signer.sign(signableRequest, awsCredentials);
        }
        return (Request) signableRequest.getOriginalRequestObject();
    }
}
