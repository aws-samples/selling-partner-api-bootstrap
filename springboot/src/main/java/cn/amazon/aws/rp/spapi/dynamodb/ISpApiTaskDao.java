package cn.amazon.aws.rp.spapi.dynamodb;

import cn.amazon.aws.rp.spapi.dynamodb.entity.SpApiTask;

import java.util.List;

/**
 * @description:
 * @className: SpApiTaskDao
 * @type: JAVA
 * @date: 2020/11/11 14:22
 * @author: zhangkui
 */
public interface ISpApiTaskDao {
	List<SpApiTask> getTaskList(String sellerKey);
	SpApiTask getTask(String sellerKey);

	void delTask(String sellerKey, String sellerId);

	void updateTaskStatus(String sellerKey, String sellerId, int status);

	void updateNextToken(String sellerKey, String sellerId, String nextToken);

	void addTask(SpApiTask spApiTaskVO);

	void addNewTask(SpApiTask spApiTask,String dateType,long space);
}
