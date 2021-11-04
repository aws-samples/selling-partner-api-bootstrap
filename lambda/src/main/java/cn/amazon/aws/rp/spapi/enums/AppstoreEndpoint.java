package cn.amazon.aws.rp.spapi.enums;

public enum AppstoreEndpoint {
//    NA("us-east-1","https://sandbox.sellingpartnerapi-na.amazon.com"),
//    EU("eu-west-1","https://sandbox.sellingpartnerapi-eu.amazon.com"),
//    FE("us-west-2","https://sandbox.sellingpartnerapi-fe.amazon.com");
    NA("us-east-1","https://sellingpartnerapi-na.amazon.com"),
    EU("eu-west-1","https://sellingpartnerapi-eu.amazon.com"),
    FE("us-west-2","https://sellingpartnerapi-fe.amazon.com");

    String endpoint;
    String region;
    AppstoreEndpoint(String region, String endpoint){
        this.endpoint = endpoint;
        this.region = region;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getRegion() {
        return region;
    }

    public static AppstoreEndpoint fromRegion(String region) {
        for (AppstoreEndpoint b : AppstoreEndpoint.values()) {
            if (b.region.equalsIgnoreCase(region)) {
                return b;
            }
        }
        throw new RuntimeException("The Region of appstore is not support");
    }
}
