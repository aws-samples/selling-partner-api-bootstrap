package cn.amazon.aws.rp.spapi.dynamodb.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.io.Serializable;

/**
 * @description:
 * @className: SpApiTaskVO
 * @type: JAVA
 * @date: 2020/11/11 14:23
 * @author: zhangkui
 */
@DynamoDBTable(tableName = "sp_api_task")
public class SpApiTask implements Serializable {

	private static final long serialVersionUID = -1733076134344051030L;
	@DynamoDBHashKey
	private String sellerKey;
	@DynamoDBRangeKey
	private String sellerId;
	@DynamoDBAttribute
	private Long taskId;
	@DynamoDBAttribute
	private String taskName;
	@DynamoDBAttribute
	private Integer executeStatus;
	@DynamoDBAttribute
	private String startTime;
	@DynamoDBAttribute
	private String endTime;

	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Integer getExecuteStatus() {
		return executeStatus;
	}

	public void setExecuteStatus(Integer executeStatus) {
		this.executeStatus = executeStatus;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getSellerKey() {
		return sellerKey;
	}

	public void setSellerKey(String sellerKey) {
		this.sellerKey = sellerKey;
	}
}
