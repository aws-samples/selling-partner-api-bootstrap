package cn.amazon.aws.rp.spapi.tasks.order;

import cn.amazon.aws.rp.spapi.clients.ApiResponse;
import cn.amazon.aws.rp.spapi.clients.api.OrdersApi;
import cn.amazon.aws.rp.spapi.clients.api.SellersApi;
import cn.amazon.aws.rp.spapi.clients.model.*;
import cn.amazon.aws.rp.spapi.clients.model.*;
import cn.amazon.aws.rp.spapi.common.IdWorker;
import cn.amazon.aws.rp.spapi.clients.model.GetOrdersResponse;
import cn.amazon.aws.rp.spapi.clients.model.Marketplace;
import cn.amazon.aws.rp.spapi.clients.model.OrderList;
import cn.amazon.aws.rp.spapi.constants.SpApiConstants;
import cn.amazon.aws.rp.spapi.constants.TaskConstants;
import cn.amazon.aws.rp.spapi.dynamodb.IOrdersDao;
import cn.amazon.aws.rp.spapi.dynamodb.ISpApiTaskDao;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SpApiTask;
import cn.amazon.aws.rp.spapi.dynamodb.impl.OrdersDao;
import cn.amazon.aws.rp.spapi.dynamodb.impl.SpApiTaskDao;
import cn.amazon.aws.rp.spapi.enums.DateType;
import cn.amazon.aws.rp.spapi.enums.StatusEnum;
import cn.amazon.aws.rp.spapi.invoker.order.OrderGetNewCreatedByTimeSpan;
import cn.amazon.aws.rp.spapi.invoker.seller.SellerGetMarketParticipation;
import cn.amazon.aws.rp.spapi.lambda.requestlimiter.ApiProxy;
import cn.amazon.aws.rp.spapi.lambda.vo.OrdersWithSeller;
import cn.amazon.aws.rp.spapi.utils.Helper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import cn.amazon.aws.rp.spapi.lambda.requestlimiter.ApiProxy;
import cn.amazon.aws.rp.spapi.utils.DateUtil;
import cn.amazon.aws.rp.spapi.utils.Helper;
import cn.amazon.aws.rp.spapi.utils.Utils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import cn.amazon.aws.rp.spapi.tasks.requestlimiter.ApiProxy;
import cn.amazon.aws.rp.spapi.tasks.vo.OrdersWithSeller;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Invocation chain:
 * EventBridge Timer -> GetAllSellerCredentialsAndPull -> here
 * The retried orders will be sent to event bus.
 */
public class GetOrderListForOneSeller implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(GetOrderListForOneSeller.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final IdWorker idWorker = new IdWorker();
    private final Object input;


    private Map<String, OrdersApi> orderApiHolder;

    private IOrdersDao ordersDao;
    private final ISpApiTaskDao spApiTaskDao;

    public GetOrderListForOneSeller(Object input) {
        this.input = input;
        orderApiHolder = new HashMap<>();
        ordersDao = new OrdersDao();
        spApiTaskDao = new SpApiTaskDao();
    }

    @Override
    public void run() {
        handleRequest(input);
    }

    public Integer handleRequest(Object input) {
        logger.info("enter");
        String jsonSellerSecrets = input != null ? gson.toJson(input) : "{}";

        SellerCredentials sellerCredentials = gson.fromJson(jsonSellerSecrets, SellerCredentials.class);

        try {
            executeTask(sellerCredentials);
        } catch (Throwable throwable) {
            logger.error("order Api call failed", throwable);
            return SpApiConstants.CODE_500;
        }

        return SpApiConstants.CODE_200;
    }

    private String sellerTaskKey = "";

    public void executeTask(SellerCredentials sellerCredentials) {
        try {
            //get task
            sellerTaskKey = sellerCredentials.getSeller_id() + "_" + TaskConstants.LIST_ORDER_TASKS;
            List<SpApiTask> spApiTaskList = spApiTaskDao.getTaskList(sellerTaskKey);
            logger.info("Current task is: {}", gson.toJson(spApiTaskList));
            // If there is no any task, then create the init task.
            if (spApiTaskList.isEmpty()) {
                logger.info("[Not task and create new.]");
                SpApiTask task = new SpApiTask();
                task.setSellerKey(sellerTaskKey);
                task.setSellerId(sellerCredentials.getSeller_id());
                task.setStartTime("2020-InitCredentialsTableHelper08-01 00:00:00");
                task.setTaskId(idWorker.nextId());
                task.setTaskName(TaskConstants.LIST_ORDER_TASKS);
                task.setExecuteStatus(StatusEnum.INIT.getStatus());
                spApiTaskDao.addTask(task);
                logger.info("sellerId:{} queryTime:{}-{}", task.getSellerId(), task.getStartTime(), task.getEndTime());
                // Sync execution.
                //update task status
                logger.info("[ set status to WORKING.]");
                spApiTaskDao.updateTaskStatus(sellerTaskKey, sellerCredentials.getSeller_id(), StatusEnum.WORKING.getStatus());
                // execute the task
                requestOrders(getMktPlaceIdList(sellerCredentials),
                        sellerCredentials,
                        task
                        );
            // Has one task.
            } else if (spApiTaskList.get(0).getNextToken() != null
                        & ! spApiTaskList.get(0).getNextToken().isEmpty()
//                        & !(spApiTaskList.get(0).getExecuteStatus().equals(StatusEnum.WORKING.getStatus())))
                        )
            {

                logger.info("[ Has token and going to retrieve it.]");
                // Has next token in task then we only handle the next token.
                // Sync execution.
                //update task status
                val existingTask = spApiTaskList.get(0);
                logger.info("[ set status to WORKING.]");
                spApiTaskDao.updateTaskStatus(sellerTaskKey, sellerCredentials.getSeller_id(), StatusEnum.WORKING.getStatus());
                final List<String> mktIdList = getMktPlaceIdList(sellerCredentials);
                final HashMap<String, Object> input = new HashMap<>();
                input.put("marketplaceIds", mktIdList);
                pullOrdersByNextToken(input, sellerCredentials, existingTask.getNextToken());
            } else{
                logger.info("[SKIP: Has token and working.... ]");
//                if (spApiTaskList.get(0).getExecuteStatus().equals(StatusEnum.COMPLETE.getStatus())) {
//                    logger.info("<<< RETURN BECAUSE THE TASK IS COMPLETED. <<<");
//                }
//                SpApiTask apiTask = spApiTaskList.get(0);
//                logger.info("sellerId:{} queryTime:{}-{}", apiTask.getSellerId(), apiTask.getStartTime(), apiTask.getEndTime());
//                // Skip if current task is in working status.
//                if (StatusEnum.WORKING.getStatus().intValue() == apiTask.getExecuteStatus().intValue()) {
//                    logger.warn("skip a task in working status!");
//                    return;
//                }
//                // Sync execution.
//                //update task status
//                logger.info("[ set status to WORKING.]");
//                spApiTaskDao.updateTaskStatus(sellerTaskKey, sellerCredentials.getSeller_id(), StatusEnum.WORKING.getStatus());
//                // execute the task
//                requestOrders(getMktPlaceIdList(sellerCredentials),
//                        sellerCredentials,
//                        apiTask);
            }
        } catch (Throwable throwable) {
            logger.error("Api call failed", throwable);
            // Revert the status back, so it can be restarted.
            logger.info("[ set status to INIT.]");
            spApiTaskDao.updateTaskStatus(sellerTaskKey, sellerCredentials.getSeller_id(), StatusEnum.INIT.getStatus());
        }
    }

    private List<String> getMktPlaceIdList(SellerCredentials sellerCredentials) {
        return sellerCredentials.getMarketplaces().stream().map(Marketplace::getId).collect(Collectors.toList());
    }

    private void requestOrders(List<String> mktIdList, SellerCredentials secretsVO, SpApiTask apiTask)
            throws NoSuchFieldException, IllegalAccessException {

        // Request from SP API.
        final OrderList orderListForTimeDelta = getOrderListForTimeDelta(secretsVO, mktIdList, apiTask);

        // Get new orders
        if (orderListForTimeDelta != null) {
            final OrderList orders = orderListForTimeDelta;
            logger.info("Found new orders, send to event bus. Number of order:" + orders.size());

            // Save to db.
            ordersDao.put(orders, secretsVO.getSeller_id());
            // Put to event bridge.
//            OrderReceivedEventGenerator.put(new OrdersWithSeller(orders, secretsVO.getSeller_id()));
        } else {
            logger.info("NO ORDER FOUND - for this api query.");
        }
    }

    /**
     * Time Delta should be the interval of event bus timer. If event bus timer calls for getting new order every 1 minute,
     * Then we should get the orders generated within the same time. (With some overlap to avoid miss order in our end.)
     * TODO Maybe we need to pull orders generated 2 minutes before. - To avoid lost order due to final consistency.
     * TODO Inject the time delta from CDK parameter to avoid hard code.
     */
    private OrderList getOrderListForTimeDelta(SellerCredentials sellerCredentials, List<String> marketplaceId, SpApiTask apiTask)
            throws NoSuchFieldException, IllegalAccessException {

        logger.info("enter");
        // Order API is per Seller - they have different secrets.
        final OrdersApi ordersApi = getOrCreateOrdersApi(sellerCredentials);

        try {
            // Prepare parameters
            final HashMap<String, Object> input = new HashMap<>();
            input.put("marketplaceIds", marketplaceId);
            String from = Helper.getIso8601Time(apiTask.getStartTime());
            input.put("createdAfter", from);

            /*
             Notice! You can not specify createdBefore with after. Server respond 400 for that request.
               Also if you don't want overlap of time you can check the result of previous API. It tells you until when
               the orders are returned.
             */
            //  input.put("createdBefore", to);
            input.put("maxResultsPerPage", "100");
            logger.info(String.format("checking from %s", from));

            final ApiResponse<GetOrdersResponse> ordersWithHttpInfo = apiProxyInvokeForOrders(sellerCredentials, ordersApi, input);

            logger.debug("Server responded.");
            final String nextToken = ordersWithHttpInfo.getData().getPayload().getNextToken();
            OrderList orders = ordersWithHttpInfo.getData().getPayload().getOrders();

            if (nextToken != null) { // Need to get more in the same time.
                asyncGetNextToken(sellerCredentials, apiTask, nextToken);
            }
            return orders;

        } catch (Throwable e) {
            logger.error("invocation of order api failed.", e);
        }
        return null;
    }

    private void asyncGetNextToken(SellerCredentials sellerCredentials, SpApiTask apiTask, String nextToken) {
        logger.info("Going to start a new lambda for next token. Update token now {}-{}-{}"
                , apiTask.getSellerKey()
                , apiTask.getSellerId()
                , nextToken);
        spApiTaskDao.updateNextToken(apiTask.getSellerKey(), apiTask.getSellerId(), nextToken);
        // Change status from WORKING to INIT so next lambda can process the next token.
        logger.info("[ set status to INIT.]");
        spApiTaskDao.updateTaskStatus(apiTask.getSellerKey(), apiTask.getSellerId(), StatusEnum.INIT.getStatus());
        final String funcName = Utils.getEnv("getOrderListForOneSellerFuncName"); // Hard-coded on CDK.
        Helper.invokeLambda(funcName, gson.toJson(sellerCredentials), true);
    }

    private ApiResponse<GetOrdersResponse> apiProxyInvokeForOrders(SellerCredentials secretsVO, OrdersApi ordersApi,
                                                                   HashMap<String, Object> input) throws Throwable {

        // Invoke with API proxy
        final OrderGetNewCreatedByTimeSpan getOrder = new OrderGetNewCreatedByTimeSpan(ordersApi);
        final ApiProxy<GetOrdersResponse> apiProxy = new ApiProxy<>(getOrder);

        // Invoke
        return apiProxy.invkWithToken(input, secretsVO.getSeller_id());
    }

    /**
     * Will recurrent call itself if result still has nextToken.
     * All resulting orders will be put to one object.
     */
    private void pullOrdersByNextToken(HashMap<String, Object> input,
                                       SellerCredentials secretsVO,
                                       String nextToken)
            throws Throwable {
        logger.info("Pulling orders for next token- {}", nextToken);
        final OrdersApi ordersApi = getOrCreateOrdersApi(secretsVO);
        try {

            input.put("nextToken", nextToken);

            final ApiResponse<GetOrdersResponse> ordersWithHttpInfo = apiProxyInvokeForOrders(secretsVO, ordersApi, input);
            final OrderList orders = ordersWithHttpInfo.getData().getPayload().getOrders();
            if (orders != null) {
                logger.info("Number to save: {}", orders.size());
                ordersDao.put(orders, secretsVO.getSeller_id());
            }
            final String newToken = ordersWithHttpInfo.getData().getPayload().getNextToken();
            if (newToken != null) { // Lambda call.
                logger.info("Still has token: " + newToken);
                asyncGetNextToken(
                        secretsVO
                        , spApiTaskDao.getTask(secretsVO.getSeller_id() + "_" + TaskConstants.LIST_ORDER_TASKS)
                        , newToken);
            } else {
                // Completed execution.
                spApiTaskDao.updateNextToken(sellerTaskKey, secretsVO.getSeller_id(), "");
                logger.info("[ set status to COMPLETE.]");
                spApiTaskDao.updateTaskStatus(sellerTaskKey, secretsVO.getSeller_id(),
                        StatusEnum.COMPLETE.getStatus());
            }
        } catch (Throwable e) {
            logger.error("Cannot pull order using token. " + nextToken, e);
            throw e;
        }
    }

    private OrdersApi getOrCreateOrdersApi(SellerCredentials secretsVO) throws NoSuchFieldException, IllegalAccessException {
        OrdersApi api = orderApiHolder.get(Integer.toHexString(secretsVO.hashCode()));
        if (api == null) {
            api = OrdersApi.buildOrdersApi(secretsVO);
            orderApiHolder.put(Integer.toHexString(secretsVO.hashCode()), api);
            logger.info("order API client created.");
        }
        return api;
    }
}
