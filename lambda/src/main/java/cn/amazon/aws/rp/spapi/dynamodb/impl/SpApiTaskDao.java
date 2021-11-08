package cn.amazon.aws.rp.spapi.dynamodb.impl;

import cn.amazon.aws.rp.spapi.common.IdWorker;
import cn.amazon.aws.rp.spapi.constants.DateConstants;
import cn.amazon.aws.rp.spapi.dynamodb.ISpApiTaskDao;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SpApiTask;
import cn.amazon.aws.rp.spapi.enums.DateType;
import cn.amazon.aws.rp.spapi.enums.StatusEnum;
import cn.amazon.aws.rp.spapi.utils.DateUtil;
import cn.amazon.aws.rp.spapi.utils.Utils;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @description:
 * @className: SpApiTaskDao
 * @type: JAVA
 * @date: 2020/11/11 14:22
 * @author: zhangkui
 */
public class SpApiTaskDao implements ISpApiTaskDao {

	private static final AmazonDynamoDB DDB = AmazonDynamoDBClientBuilder.standard().build();
	public static String TABLE_NAME = "sp_api_task";
	private static String TABLE_P_KEY = "sellerKey";

	private final IdWorker idWorker;

	public SpApiTaskDao(){
		idWorker = new IdWorker();
	}

	@Override
	public List<SpApiTask> getTaskList(String sellerKey) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":sellerKey", new AttributeValue().withS(sellerKey));
		DynamoDBQueryExpression<SpApiTask> queryExpression = new DynamoDBQueryExpression<SpApiTask>()
				.withKeyConditionExpression("sellerKey = :sellerKey")
				.withExpressionAttributeValues(eav);
		final DynamoDBMapper dbm = new DynamoDBMapper(DDB);
		return dbm.query(SpApiTask.class, queryExpression);
	}

	@Override
	public SpApiTask getTask(String sellerKey) {
		val spApiTaskList = this.getTaskList(sellerKey);
		if (spApiTaskList.isEmpty()) {
			return null;
		} else {
			return spApiTaskList.stream().findFirst().get();
		}
	}

	@Override
	public void delTask(String sellerKey, String sellerId) {
		DynamoDB dynamoDB = new DynamoDB(DDB);
		final Table table = dynamoDB.getTable(TABLE_NAME);
		PrimaryKey primaryKey = new PrimaryKey();
		primaryKey.addComponent("sellerKey", sellerKey);
		primaryKey.addComponent("sellerId", sellerId);
		table.deleteItem(primaryKey);
	}

	@Override
	public void updateTaskStatus(String sellerKey, String sellerId, int status) {
		DynamoDB dynamoDB = new DynamoDB(DDB);
		final Table table = dynamoDB.getTable(TABLE_NAME);
		PrimaryKey primaryKey = new PrimaryKey();
		primaryKey.addComponent("sellerKey", sellerKey);
		primaryKey.addComponent("sellerId", sellerId);
		AttributeUpdate attributeUpdate = new AttributeUpdate("executeStatus");
		attributeUpdate.put(status);
		table.updateItem(primaryKey,attributeUpdate);
	}

	@Override
	public void updateNextToken(String sellerKey, String sellerId, String nextToken){
		DynamoDB dynamoDB = new DynamoDB(DDB);
		final Table table = dynamoDB.getTable(TABLE_NAME);
		PrimaryKey primaryKey = new PrimaryKey();
		primaryKey.addComponent("sellerKey", sellerKey);
		primaryKey.addComponent("sellerId", sellerId);
		AttributeUpdate attributeUpdate = new AttributeUpdate("nextToken");
		attributeUpdate.put(nextToken);
		table.updateItem(primaryKey,attributeUpdate);
	}

	@Override
	public void addTask(SpApiTask spApiTaskVO) {
		final DynamoDBMapper dbm = new DynamoDBMapper(DDB);
		dbm.batchSave(spApiTaskVO);
	}

	@Override
	public void addNewTask(SpApiTask oldSpApiTask, String dateType, long space) {
		SpApiTask apiTask = new SpApiTask();

		// Old task and new task are sharing the same key and ID so it will update the old task on DDB.
		apiTask.setSellerKey(oldSpApiTask.getSellerId() + "_" + oldSpApiTask.getTaskName());
		apiTask.setSellerId(oldSpApiTask.getSellerId());
		if(Objects.nonNull(oldSpApiTask.getEndTime())) {
			// The new task starts from the end of old task.
			apiTask.setStartTime(oldSpApiTask.getEndTime());
			LocalDateTime localDateTime = DateUtil.getLocalDateTime(oldSpApiTask.getEndTime());
			if (DateType.NANOS.name().equalsIgnoreCase(dateType)) {
				apiTask.setEndTime(DateUtil.getDateFormat(localDateTime.plusNanos(space)));
			} else if (DateType.SECONDS.name().equalsIgnoreCase(dateType)) {
				apiTask.setEndTime(DateUtil.getDateFormat(localDateTime.plusSeconds(space)));
			} else if (DateType.MINUTES.name().equalsIgnoreCase(dateType)) {
				apiTask.setEndTime(DateUtil.getDateFormat(localDateTime.plusMinutes(space)));
			} else if (DateType.HOURS.name().equalsIgnoreCase(dateType)) {
				apiTask.setEndTime(DateUtil.getDateFormat(localDateTime.plusHours(space)));
			} else if (DateType.DAYS.name().equalsIgnoreCase(dateType)) {
				apiTask.setEndTime(DateUtil.getDateFormat(localDateTime.plusDays(space)));
			} else if (DateType.WEEKS.name().equalsIgnoreCase(dateType)) {
				apiTask.setEndTime(DateUtil.getDateFormat(localDateTime.plusWeeks(space)));
			} else if (DateType.MONTHS.name().equalsIgnoreCase(dateType)) {
				apiTask.setEndTime(DateUtil.getDateFormat(localDateTime.plusMonths(space)));
			} else if (DateType.YEARS.name().equalsIgnoreCase(dateType)) {
				apiTask.setEndTime(DateUtil.getDateFormat(localDateTime.plusYears(space)));
			}
			// 判断endTime是否大于当前时间
			if(LocalDateTime.now().isBefore(DateUtil.getLocalDateTime(apiTask.getEndTime()))){
				apiTask.setEndTime(DateUtil.getDateFormat(LocalDateTime.now().plusMinutes(-2L)));
			}
		}
		apiTask.setTaskId(idWorker.nextId());
		apiTask.setTaskName(oldSpApiTask.getTaskName());
		apiTask.setExecuteStatus(StatusEnum.INIT.getStatus());
		this.addTask(apiTask);
	}

	public static String getTaskTableName() {
		// Update the table name from environment. It is expected to be set by CDK script on Lambda.
		final String tableName = Utils.getEnv("DYNAMODB_TASK_TABLE");
		if (tableName != null) {
			TABLE_NAME = tableName;
		}
		return TABLE_NAME;
	}
}
