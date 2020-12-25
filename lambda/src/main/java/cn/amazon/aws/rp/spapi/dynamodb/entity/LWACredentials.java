package cn.amazon.aws.rp.spapi.dynamodb.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class LWACredentials {
    @NonNull
    private String clientId;
    @NonNull
    private String clientSecret;
}
