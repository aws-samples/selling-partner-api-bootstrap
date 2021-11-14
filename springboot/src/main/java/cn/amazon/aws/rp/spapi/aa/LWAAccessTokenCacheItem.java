package cn.amazon.aws.rp.spapi.aa;

import lombok.Data;

@Data
class LWAAccessTokenCacheItem {

    private String accessToken;
    private long accessTokenExpiredTime;

}
