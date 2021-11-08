package cn.amazon.aws.rp.spapi.dynamodb;

import cn.amazon.aws.rp.spapi.common.IdWorker;
import cn.amazon.aws.rp.spapi.constants.TaskConstants;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SpApiTask;
import cn.amazon.aws.rp.spapi.dynamodb.impl.SpApiTaskDao;
import cn.amazon.aws.rp.spapi.enums.StatusEnum;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

/**
 * @description:
 * @className: ApiTaskTest
 * @type: JAVA
 * @date: 2020/11/11 14:41
 * @author: zhangkui
 */
@Ignore
public class ApiTaskIT {
    private static final SpApiTaskDao spApiTaskDaoDdb = new SpApiTaskDao();
    private static final IdWorker idWorker = new IdWorker();

    @Test
    public void getTask() {
        List<SpApiTask> spApiTaskVOS = spApiTaskDaoDdb.getTask("A1HILDQKN3MNZ9_1_fulfilled_shipment_request_report");
        spApiTaskVOS.forEach(spApiTaskVO -> {
            System.out.println(spApiTaskVO.getSellerKey());
            System.out.println(spApiTaskVO.getStartTime());
        });
    }

    @Test
    public void delTask() {
        spApiTaskDaoDdb.delTask("A1HILDQKN3MNZ9_0_fulfilled_shipment_request_report", "A1HILDQKN3MNZ9");
    }

    @Test
    public void addTask() {
        String taskName = TaskConstants.LIST_FINANCIAL_EVENTS;
        String sellerId = "seller_tom";
        SpApiTask task = new SpApiTask();
        task.setSellerKey(sellerId + "_" + taskName);
        task.setSellerId(sellerId);
        task.setStartTime("2020-08-01 00:00:00");
        task.setEndTime("2020-08-02 00:00:00");
        task.setTaskId(idWorker.nextId());
        task.setTaskName(taskName);
        task.setExecuteStatus(StatusEnum.INIT.getStatus());
        spApiTaskDaoDdb.addTask(task);
    }

    @Test
    public void upTask() {
        String taskName = TaskConstants.LIST_FINANCIAL_EVENTS;
        String sellerId = "A1HILDQKN3MNZ9";
        spApiTaskDaoDdb.updateTaskStatus(sellerId + "_" + taskName, sellerId, StatusEnum.INIT.getStatus());
    }
}
