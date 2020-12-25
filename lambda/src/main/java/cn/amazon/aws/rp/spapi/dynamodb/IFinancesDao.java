package cn.amazon.aws.rp.spapi.dynamodb;

import cn.amazon.aws.rp.spapi.clients.model.FinancialEvents;
import cn.amazon.aws.rp.spapi.clients.model.Marketplace;
import cn.amazon.aws.rp.spapi.clients.model.MarketplaceParticipation;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SpApiTask;

import java.util.List;

/**
 * @description:
 * @className: IFinancesDao
 * @type: JAVA
 * @date: 2020/11/10 11:19
 * @author: zhangkui
 */
public interface IFinancesDao {
	void put(List<FinancialEvents> financialEventsList, Marketplace marketplace, SellerCredentials sellerCredentials, SpApiTask apiTask);
}
