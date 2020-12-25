package cn.amazon.aws.rp.spapi.dynamodb.impl;

import cn.amazon.aws.rp.spapi.clients.model.FinancialEvents;
import cn.amazon.aws.rp.spapi.clients.model.Marketplace;
import cn.amazon.aws.rp.spapi.clients.model.MarketplaceParticipation;
import cn.amazon.aws.rp.spapi.clients.model.ShipmentEvent;
import cn.amazon.aws.rp.spapi.dynamodb.IFinancesDao;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SpApiTask;
import cn.amazon.aws.rp.spapi.enums.ShipmentType;
import cn.amazon.aws.rp.spapi.utils.Utils;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @description:
 * @className: FinancesDaoMdb
 * @type: JAVA
 * @date: 2020/11/10 11:18
 * @author: zhangkui
 */
public class FinancesDao implements IFinancesDao {

	private static final Logger logger = LoggerFactory.getLogger(SpApiSecretDao.class);
	private static final AmazonDynamoDB DDB = AmazonDynamoDBClientBuilder.standard().build();
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static String TABLE_NAME = "amz_sp_api_financial_shipment_event";
	public static String TABLE_NAME_REFUND = "amz_sp_api_financial_refund_event";
	private static String TABLE_P_KEY = "amazonOrderId";
	private static String TABLE_SORT_KEY = "sellerId";

	private DynamoDB dynamoDB;
	private Table table;
	private Table tableRefund;

	public FinancesDao() {
		updateTableName();
		dynamoDB = new DynamoDB(DDB);
		table = dynamoDB.getTable(TABLE_NAME);
		tableRefund = dynamoDB.getTable(TABLE_NAME_REFUND);
	}

	@Override
	public void put(List<FinancialEvents> financialEventsList, Marketplace marketplace, SellerCredentials sellerCredentials, SpApiTask apiTask) {
		financialEventsList.forEach(financialEvents -> {
			// 1, 保存shipmentEvents
			financialEvents.getShipmentEventList().forEach(shipmentEvent -> {
				putShipmentEvent(shipmentEvent, marketplace, sellerCredentials.getSeller_id(), ShipmentType.shipment.name(), apiTask);
			});
			// 2, 保存refundEvents
			financialEvents.getRefundEventList().forEach(refundEvent -> {
				putShipmentEvent(refundEvent, marketplace, sellerCredentials.getSeller_id(), ShipmentType.refund.name(), apiTask);
			});
			// 3, 保存guaranteeClaimEvents
			financialEvents.getGuaranteeClaimEventList().forEach(guaranteeClaimEvent -> {
				putShipmentEvent(guaranteeClaimEvent, marketplace, sellerCredentials.getSeller_id(), ShipmentType.guaranteeClaim.name(), apiTask);
			});
			// 4, 保存chargebackEvents
			financialEvents.getChargebackEventList().forEach(chargebackEvent -> {
				putShipmentEvent(chargebackEvent, marketplace, sellerCredentials.getSeller_id(), ShipmentType.chargeback.name(), apiTask);
			});
		});
	}

	private void putShipmentEvent(ShipmentEvent shipmentEvent, Marketplace marketplace, String sellerId, String shipmentType, SpApiTask apiTask) {
		try {
			final PutItemOutcome putResult = table.putItem(new Item()
					.withPrimaryKey(TABLE_P_KEY, shipmentEvent.getAmazonOrderId(), TABLE_SORT_KEY, sellerId)
					.withString("countryCode", marketplace.getCountryCode())
					.withString("marketplaceId", marketplace.getId())
					.withString("shipmentType", shipmentType)
					.withString("queryStartTime", apiTask.getStartTime())
					.withString("queryEndTime", apiTask.getEndTime())
					.withJSON("details", gson.toJson(shipmentEvent)));
			logger.info("ShipmentEvent PutItem succeeded: {}", gson.toJson(putResult));
		} catch (Exception e) {
			logger.error("Cannot save order to dynamodb! ", e);
		}
	}


	public static String updateTableName() {
		// Update the table name from environment. It is expected to be set by CDK script on Lambda.
		final String tableName = Utils.getEnv("DYNAMODB_FINANCES_TABLE");
		if (tableName != null) {
			TABLE_NAME = tableName;
		}
		return TABLE_NAME;
	}
}
