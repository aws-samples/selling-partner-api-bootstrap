package cn.amazon.aws.rp.spapi.tasks.finances;

import cn.amazon.aws.rp.spapi.clients.ApiResponse;
import cn.amazon.aws.rp.spapi.clients.api.FinancesApi;
import cn.amazon.aws.rp.spapi.clients.model.FinancialEvents;
import cn.amazon.aws.rp.spapi.clients.model.ListFinancialEventsResponse;
import cn.amazon.aws.rp.spapi.clients.model.Marketplace;
import cn.amazon.aws.rp.spapi.common.IdWorker;
import cn.amazon.aws.rp.spapi.constants.SpApiConstants;
import cn.amazon.aws.rp.spapi.constants.TaskConstants;
import cn.amazon.aws.rp.spapi.dynamodb.IFinancesDao;
import cn.amazon.aws.rp.spapi.dynamodb.ISpApiTaskDao;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SpApiTask;
import cn.amazon.aws.rp.spapi.dynamodb.impl.FinancesDao;
import cn.amazon.aws.rp.spapi.dynamodb.impl.SpApiTaskDao;
import cn.amazon.aws.rp.spapi.enums.DateType;
import cn.amazon.aws.rp.spapi.enums.StatusEnum;
import cn.amazon.aws.rp.spapi.invoker.finances.FinancesEventsApiInvoker;
import cn.amazon.aws.rp.spapi.tasks.requestlimiter.ApiProxy;
import cn.amazon.aws.rp.spapi.utils.DateUtil;
import cn.amazon.aws.rp.spapi.utils.Helper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ExecuteTaskForOneSeller implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(ExecuteTaskForOneSeller.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<String, FinancesApi> financesApiHolder;
    private static final IdWorker idWorker = new IdWorker();

    private final IFinancesDao financesDao;
    private final ISpApiTaskDao spApiTaskDao;
    private final Object input;

    public ExecuteTaskForOneSeller(Object input) {
        this.input = input;
        financesApiHolder = new HashMap<>();
        financesDao = new FinancesDao();
        spApiTaskDao = new SpApiTaskDao();
    }

    @Override
    public void run() {
        handleRequest(input);
    }

    public Integer handleRequest(Object input) {
	    String jsonSellerSecrets = input != null ? gson.toJson(input) : "{}";
	    Helper.logInput(logger, jsonSellerSecrets, gson);
	    SellerCredentials sellerCredentials = gson.fromJson(jsonSellerSecrets, SellerCredentials.class);
	    executeTask(sellerCredentials);
	    return SpApiConstants.CODE_200;
    }


    public void executeTask(SellerCredentials sellerCredentials) {
        try {
	        //get task
	        final String sellerTaskKey = sellerCredentials.getSeller_id() + "_" + TaskConstants.LIST_FINANCIAL_EVENTS;
	        List<SpApiTask> spApiTaskList = spApiTaskDao.getTask(sellerTaskKey);
	        if (spApiTaskList.isEmpty()) {
                SpApiTask task = new SpApiTask();
                task.setSellerKey(sellerTaskKey);
                task.setSellerId(sellerCredentials.getSeller_id());
                task.setStartTime("2020-08-01 00:00:00");
                task.setEndTime("2020-08-02 00:00:00");
                task.setTaskId(idWorker.nextId());
                task.setTaskName(TaskConstants.LIST_FINANCIAL_EVENTS);
                task.setExecuteStatus(StatusEnum.INIT.getStatus());
                spApiTaskDao.addTask(task);
                spApiTaskList.add(task);
	        }
	        SpApiTask apiTask = spApiTaskList.stream().findFirst().get();
	        logger.info("sellerId:{} queryTime:{}-{}", apiTask.getSellerId(), apiTask.getStartTime(), apiTask.getEndTime());
	        //check time
	        long diff = DateUtil.betweenTwoTime(DateUtil.toUtc(DateUtil.getLocalDateTime(apiTask.getEndTime())), DateUtil.toUtc(LocalDateTime.now()), ChronoUnit.HOURS);
	        if (diff < 1) {
		        return;
	        }
	        if (StatusEnum.WORKING.getStatus().intValue() == apiTask.getExecuteStatus().intValue()) {
		        return;
	        }

            sellerCredentials.getMarketplaces().forEach(marketplace -> {
		        requestFinancesForOneMkt(marketplace, sellerCredentials, apiTask);
	        });

            //update task
            spApiTaskDao.upTaskStatus(sellerTaskKey, sellerCredentials.getSeller_id(), StatusEnum.WORKING.getStatus());
	        //add task
	        spApiTaskDao.addNewTask(apiTask, DateType.DAYS.name(), 2L);
        } catch (Throwable throwable) {
            logger.error("Api call failed", throwable);
        }
    }

    private void requestFinancesForOneMkt(Marketplace marketplace, SellerCredentials sellerCredentials, SpApiTask apiTask) {
        final List<FinancialEvents> financialEventsList = getFinancesListForTimeDelta(sellerCredentials, apiTask);
        if (financialEventsList.isEmpty()) {
            logger.info("NO FinancialEvents FOUND - for this api query.");
            return;
        }
        financesDao.put(financialEventsList, marketplace, sellerCredentials, apiTask);
    }

    private List<FinancialEvents> getFinancesListForTimeDelta(SellerCredentials secretsVO, SpApiTask spApiTaskVO) {
        final List<FinancialEvents> financialEventsList = new ArrayList<>();
        try {
            logger.info("enter");
            final HashMap<String, Object> input = new HashMap<>();
            final FinancesApi financesApi = getOrCreateFinancesApi(secretsVO);
//			final String from = Helper.getIso8601Time(70);
            final String from = spApiTaskVO.getStartTime();
            final String to = spApiTaskVO.getEndTime();
            input.put("postedAfter", from);
//			final String to = Helper.getIso8601Time(0);
            input.put("postedBefore", to);
            logger.info(String.format("checking from %s to %s", from, to));
            final ApiResponse<ListFinancialEventsResponse> financesWithHttpInfo = apiProxyInvokeForFinances(secretsVO, financesApi, input);
            FinancialEvents financialEvents = financesWithHttpInfo.getData().getPayload().getFinancialEvents();
            if (Objects.nonNull(financialEvents)) {
                financialEventsList.add(financialEvents);
            }
            String nextToken = financesWithHttpInfo.getData().getPayload().getNextToken();
            if (Objects.nonNull(nextToken)) {
                logger.info("pulling more FinancialEvents according to next token.");
                pullFinancialByNextToken(input, secretsVO, nextToken, financialEventsList);
            }
        } catch (Throwable e) {
            logger.error("invocation of order api failed.", e);
        }
        return financialEventsList;
    }

    private void pullFinancialByNextToken(HashMap<String, Object> input,
                                          SellerCredentials sellerCredentials,
                                          String nextToken,
                                          List<FinancialEvents> financialEventsList)
            throws Throwable {
        logger.info("current pulled financial: " + financialEventsList.size());
        FinancesApi financesApi = getOrCreateFinancesApi(sellerCredentials);
        input.put("nextToken", nextToken);
        final ApiResponse<ListFinancialEventsResponse> financesWithHttpInfo = apiProxyInvokeForFinances(sellerCredentials, financesApi, input);
        FinancialEvents financialEvents = financesWithHttpInfo.getData().getPayload().getFinancialEvents();
        if (Objects.nonNull(financialEvents)) {
            financialEventsList.add(financialEvents);
        }
        String newToken = financesWithHttpInfo.getData().getPayload().getNextToken();
        if (Objects.nonNull(newToken)) {
            logger.info("pulling more FinancialEvents according to new token.");
            pullFinancialByNextToken(input, sellerCredentials, newToken, financialEventsList);
        }
    }

    private ApiResponse<ListFinancialEventsResponse> apiProxyInvokeForFinances(SellerCredentials sellerCredentials, FinancesApi financesApi, HashMap<String, Object> input) throws Throwable {

        // Invoke with API proxy
        final FinancesEventsApiInvoker getFinances = new FinancesEventsApiInvoker(financesApi);
        final ApiProxy<ListFinancialEventsResponse> apiProxy = new ApiProxy<>(getFinances);

        // Invoke
        return apiProxy.invkWithToken(input, sellerCredentials.getSeller_id());
    }

    private FinancesApi getOrCreateFinancesApi(SellerCredentials sellerCredentials) throws NoSuchFieldException, IllegalAccessException {
        FinancesApi api = financesApiHolder.get(Integer.toHexString(sellerCredentials.hashCode()));
        if (api == null) {
            api = FinancesApi.buildFinancesApi(sellerCredentials);
            financesApiHolder.put(Integer.toHexString(sellerCredentials.hashCode()), api);
            logger.info("Seller API client created.");
        }
        return api;
    }
}
