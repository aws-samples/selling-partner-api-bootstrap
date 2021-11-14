package cn.amazon.aws.rp.spapi.invoker.seller;

import cn.amazon.aws.rp.spapi.tasks.requestlimiter.Invokable;
import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.ApiResponse;
import cn.amazon.aws.rp.spapi.clients.api.SellersApi;
import cn.amazon.aws.rp.spapi.clients.model.GetMarketplaceParticipationsResponse;

import java.util.Map;

public class SellerGetMarketParticipation
        implements Invokable<GetMarketplaceParticipationsResponse> {

    private final SellersApi api;

    public SellerGetMarketParticipation(SellersApi api) {

        this.api = api;
    }

    @Override
    public ApiResponse<GetMarketplaceParticipationsResponse> invoke(Map<String, Object> input) throws ApiException {

        return api.getMarketplaceParticipationsWithHttpInfo();
    }

    @Override
    public String getRateLimiterNameSuffix() {
        return SellerGetMarketParticipation.class.getCanonicalName();
    }
}
