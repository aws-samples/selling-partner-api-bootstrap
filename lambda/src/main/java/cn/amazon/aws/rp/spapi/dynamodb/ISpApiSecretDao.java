package cn.amazon.aws.rp.spapi.dynamodb;

import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;

import java.util.List;

/**
 * This interface is try to easy the future effort of switching db.
 */
public interface ISpApiSecretDao {
    SellerCredentials getSecretsVOForSeller(String sellerId);

    List<SellerCredentials> getSecretsVOForAllSeller();

    void updateSellerCredentials(SellerCredentials sellerCredentials);
}
