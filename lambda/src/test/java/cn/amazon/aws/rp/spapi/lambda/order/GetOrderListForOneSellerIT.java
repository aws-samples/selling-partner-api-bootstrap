package cn.amazon.aws.rp.spapi.lambda.order;

import cn.amazon.aws.rp.spapi.clients.ApiResponse;
import cn.amazon.aws.rp.spapi.clients.api.SellersApi;
import cn.amazon.aws.rp.spapi.clients.model.GetMarketplaceParticipationsResponse;
import cn.amazon.aws.rp.spapi.clients.model.Marketplace;
import cn.amazon.aws.rp.spapi.clients.model.MarketplaceParticipation;
import cn.amazon.aws.rp.spapi.clients.model.MarketplaceParticipationList;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.dynamodb.impl.SpApiSecretDao;
import cn.amazon.aws.rp.spapi.utils.Utils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GetOrderListForOneSellerIT {

    @Test
    void handleRequest() throws Throwable {
        String refreshToken = Utils.getEnv("refresh_token");

        GetOrderListForOneSeller getOrderListForOneSeller = new GetOrderListForOneSeller();
        SellerCredentials credentials = new SellerCredentials();
        credentials.setLWAAuthorizationCredentials_refreshToken(refreshToken);
        credentials.setSeller_id("seller_unit_test");

        if(Utils.isNullOrEmpty(credentials.getMarketplaces())) {
            final ApiResponse<GetMarketplaceParticipationsResponse> marketplaceParticipations = SellersApi.getMarketplaceParticipations(credentials);
            final MarketplaceParticipationList payloadList = marketplaceParticipations.getData().getPayload();
            final List<Marketplace> mktList = payloadList.stream().map(MarketplaceParticipation::getMarketplace).collect(Collectors.toList());
            credentials.setMarketplaces(mktList);
            SpApiSecretDao.updateMarketplace(credentials);

        }

        getOrderListForOneSeller.handleRequest(credentials,null);

    }
}