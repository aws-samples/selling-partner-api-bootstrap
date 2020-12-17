package cn.amazon.aws.rp.spapi.dynamodb;

import cn.amazon.aws.rp.spapi.clients.model.MarketplaceParticipation;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerSecretsVO;

public interface IReportsDao {
    void put(String reportId, MarketplaceParticipation marketplace, SellerSecretsVO sellerSecretsVO);
}
