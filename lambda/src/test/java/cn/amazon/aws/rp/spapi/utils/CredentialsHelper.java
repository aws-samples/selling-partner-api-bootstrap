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
//        credentials.setLWAAuthorizationCredentials_refreshToken("Atzr|IwEBIPFJsdC9Ib6wk9BcIz7QtnK2TbV7hbxE_Kxqc59Z-gKrA5EVONOIz0Ic8mz7-W2tkt0RDI3GUDuNqvfSJKqLNJAMYHnUj-hOJcA5TIORDVbSAw1hXgoWRMd1rS9ofvT0CYjps9yxReFHPVaV08JKPI94XfHZlRwggqaFq8tjky7h8bHQiSgl2hWNxOViJ7Q6zQPebRFzDMortHTZY0T-RdBJIk1O_iITipfRp5VSVen2M_OIxBJa5BreX-f68TsrC2SvRVp1x2Zq7vCis9i1HF1DYs4FTVpq7qrMglrWoNVysdJAm6JMZsFnQBOFeUH_GEU");

        //UK
//        credentials.setLWAAuthorizationCredentials_refreshToken("Atzr|IwEBII9D19dfIQVmSe3WOCDPJbd9KC9A93czfvpyozejGkLmDn73br3lFLyldhfwA1_rbtXgtoTC50WGD8vp1jzXbeDb6M7HrPCGX-ZE5VuG3AoswrW7XNK1zi1G4AxU6ai75m1ali3X2LVsKv4sHV8EvoWKWKEIwxcd7ETC3Do834K3Ke9WCL-k0_TcFlAjU4nLY8uO3yAz7oAZDWCS-EIz6tsOKUWDqsv_Y7GJ5fy75k7l7-WkjbWHp7eG0vKiz1JBwkwpG3TC64WKuDrWjyN1h-mt2GoxQsNcXKcHRMwghsetlcC29wUQSt0L2BQXURT50zo");

        //Japan
//        credentials.setLWAAuthorizationCredentials_refreshToken("Atzr|IwEBIFCpo_rmbg4ZSsggTB0MwMysj_QMqe4qUPKuka9Ihel6Lc9mh0QQkVous-MAcTCGNG1apFgIsmPEMFx5ksiPRN8D8IbwZrpUQbS2-h-c3aMV0FKBN7Ecrq2d4qwZRf6XYgOAXbOKHro0zaE512s8oIfa6EKqUwLdeUET48HUUSNpAsihPoVVxvScC4Xn-SPfw08VPpY7d1YK_WFDwIvUSP20Qxriif2JLMs7hA60E7bJe2jANz0fr-3gZUADgLq6ZiHx2zzGieRGcnTmO1OnEcCZKiNuEikn3GgaLhUo5GcgeWgGLt4dnu12Qvnyat69eJ4");
        credentials.setSeller_id("seller_jim");
//        credentials.setRegion("eu-west-1");
        return credentials;
    }
}
