package cn.amazon.aws.rp.spapi.dynamodb;

import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerSecretsVO;

import java.util.List;

/**
 * This interface is try to easy the future effort of switching db.
 */
public interface ISpApiSecretDao {
    SellerSecretsVO getSecretsVOForSeller(String sellerId);

    List<SellerSecretsVO> getSecretsVOForAllSeller();
}
