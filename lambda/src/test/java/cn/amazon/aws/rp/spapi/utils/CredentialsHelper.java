package cn.amazon.aws.rp.spapi.utils;

import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;

public class CredentialsHelper {

    public static SellerCredentials getAppCredentials() {
        SellerCredentials credentials = new SellerCredentials();
        credentials.setSeller_id("seller_jim");
        return credentials;
    }

    public static SellerCredentials getSellerCredentials() {
        SellerCredentials credentials = new SellerCredentials();
        credentials.setLWAAuthorizationCredentials_refreshToken("Atzr|IwEBIJ27mtx3w0pHV9Rc8TLfmcGX2yEQnC27-88Ya_uI8FqOdAXrdCTzJucIhj1nc-XHkHNxRbBosXdF33nJtDYOQYvql_FGwYBmPMAmu24YybdD3BblXut81LxL6HKTzfF2Ebgi_lF-KmHSxoz4glZCgH8a-2jbOZJbnvJKb_bAZLxWfsgLawhqlHrhyhpSoCAclVfvFGzWG2Wv1hJDgSV2ggMf-4Y26TJ58rM-gMLuL4ipjeOG7QWb7pLcdgcly5XiMuLJLGNVf8h_1-OznfgFgnroYrORlRRkCQkfdheDO_BT0BNj0GPm3bX5u3wsY9go4To");
        //self
//        credentials.setLWAAuthorizationCredentials_refreshToken("Atzr|IwEBIL-XmeOWqDQFzJHaSEGx_oABDxN6Y0nRbMjfnR3HODBDvQaJfgO6zHEqPSi7f5J9JJhxm4SQ7pW3Z0shU1_s25lHyHtdP4vWhEq2wMdQT0X1A3Xt-cwTt6HN_lywlerfM2cVAhny0Rs2O4VBedM-xOnq0dHP9KNcHxlny4_TmPAg97_BPmfNMXPe4ylXVof-Ux71YisZPYyaKahshN0LlmZhu8c7A2pzQDIs4saJO4h6ZaoIxR0T-jGXe0EWVE8W0pOOnWAMgyWmoxLn6jcIvFoWxmmPsZwBUe4YOY2n6ntCsS6CEMksB1FqU5UNlMUe64U");
        credentials.setSeller_id("seller_jim");
        return credentials;
    }
}
