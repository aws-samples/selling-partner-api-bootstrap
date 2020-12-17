package cn.amazon.aws.rp.spapi.lambda.finances;

import cn.amazon.aws.rp.spapi.clients.ApiResponse;
import cn.amazon.aws.rp.spapi.clients.api.FinancesApi;
import cn.amazon.aws.rp.spapi.clients.api.SellersApi;
import cn.amazon.aws.rp.spapi.clients.model.*;
import cn.amazon.aws.rp.spapi.constants.SpApiConstants;
import cn.amazon.aws.rp.spapi.constants.TaskConstants;
import cn.amazon.aws.rp.spapi.dynamodb.IFinancesDao;
import cn.amazon.aws.rp.spapi.dynamodb.ISpApiTaskDao;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerSecretsVO;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SpApiTask;
import cn.amazon.aws.rp.spapi.dynamodb.impl.FinancesDao;
import cn.amazon.aws.rp.spapi.dynamodb.impl.SpApiTaskDao;
import cn.amazon.aws.rp.spapi.enums.DateType;
import cn.amazon.aws.rp.spapi.enums.StatusEnum;
import cn.amazon.aws.rp.spapi.invoker.finances.FinancesEventsApiInvoker;
import cn.amazon.aws.rp.spapi.invoker.seller.SellerGetMarketParticipation;
import cn.amazon.aws.rp.spapi.lambda.requestlimiter.ApiProxy;
import cn.amazon.aws.rp.spapi.utils.DateUtil;
import cn.amazon.aws.rp.spapi.utils.Helper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ExecuteTaskForOneSeller implements RequestHandler<Object, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(ExecuteTaskForOneSeller.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<String, SellersApi> sellerApiHolder;
    private final Map<String, FinancesApi> financesApiHolder;

    private final IFinancesDao financesDao;
    private final ISpApiTaskDao spApiTaskDao;

    public ExecuteTaskForOneSeller() {
        sellerApiHolder = new HashMap<>();
        financesApiHolder = new HashMap<>();
        financesDao = new FinancesDao();
        spApiTaskDao = new SpApiTaskDao();
    }

    @Override
    public Integer handleRequest(Object input, Context context) {
	    String jsonSellerSecrets = input != null ? gson.toJson(input) : "{}";
	    Helper.logInput(logger, jsonSellerSecrets, context, gson);
	    SellerSecretsVO sellerSecretsVO = gson.fromJson(jsonSellerSecrets, SellerSecretsVO.class);
	    executeTask(sellerSecretsVO);
	    return SpApiConstants.CODE_200;
    }


    public void executeTask(SellerSecretsVO sellerSecretsVO) {
        try {
	        //get task
	        final String sellerKey = sellerSecretsVO.getSeller_id() + "_" + TaskConstants.LIST_FINANCIAL_EVENTS;
	        List<SpApiTask> spApiTaskList = spApiTaskDao.getTask(sellerKey);
	        if (spApiTaskList.isEmpty()) {
		        return;
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
	        //update task
	        spApiTaskDao.upTaskStatus(sellerKey, sellerSecretsVO.getSeller_id(), StatusEnum.WORKING.getStatus());
	        final ApiResponse<GetMarketplaceParticipationsResponse> marketplaceParticipation = getMarketplaceParticipations(sellerSecretsVO);
	        final MarketplaceParticipationList payloadList = marketplaceParticipation.getData().getPayload();
	        payloadList.forEach(participation -> {
		        requestFinancesForOneMkt(participation, sellerSecretsVO, apiTask);
	        });
	        //add task
	        spApiTaskDao.addNewTask(apiTask, DateType.DAYS.name(), 2L);
        } catch (Throwable throwable) {
            logger.error("Api call failed", throwable);
        }
    }

    private void requestFinancesForOneMkt(MarketplaceParticipation marketplace, SellerSecretsVO sellerSecretsVO, SpApiTask apiTask) {
        final List<FinancialEvents> financialEventsList = getFinancesListForTimeDelta(sellerSecretsVO, apiTask);
        if (financialEventsList.isEmpty()) {
            logger.info("NO FinancialEvents FOUND - for this api query.");
            return;
        }
        financesDao.put(financialEventsList, marketplace, sellerSecretsVO, apiTask);
    }

    private List<FinancialEvents> getFinancesListForTimeDelta(SellerSecretsVO secretsVO, SpApiTask spApiTaskVO) {
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
                                          SellerSecretsVO secretsVO,
                                          String nextToken,
                                          List<FinancialEvents> financialEventsList)
            throws Throwable {
        logger.info("current pulled financial: " + financialEventsList.size());
        FinancesApi financesApi = getOrCreateFinancesApi(secretsVO);
        input.put("nextToken", nextToken);
        final ApiResponse<ListFinancialEventsResponse> financesWithHttpInfo = apiProxyInvokeForFinances(secretsVO, financesApi, input);
        FinancialEvents financialEvents = financesWithHttpInfo.getData().getPayload().getFinancialEvents();
        if (Objects.nonNull(financialEvents)) {
            financialEventsList.add(financialEvents);
        }
        String newToken = financesWithHttpInfo.getData().getPayload().getNextToken();
        if (Objects.nonNull(newToken)) {
            logger.info("pulling more FinancialEvents according to new token.");
            pullFinancialByNextToken(input, secretsVO, newToken, financialEventsList);
        }
    }

    private ApiResponse<ListFinancialEventsResponse> apiProxyInvokeForFinances(SellerSecretsVO secretsVO, FinancesApi financesApi, HashMap<String, Object> input) throws Throwable {

        // Invoke with API proxy
        final FinancesEventsApiInvoker getFinances = new FinancesEventsApiInvoker(financesApi);
        final ApiProxy<ListFinancialEventsResponse> apiProxy = new ApiProxy<>(getFinances);

        // Invoke
        return apiProxy.invkWithToken(input, secretsVO.getSeller_id());
    }

    private ApiResponse<GetMarketplaceParticipationsResponse> getMarketplaceParticipations(SellerSecretsVO secretsVO) throws Throwable {

        // Seller API is per Seller - they have different secrets.
        final SellersApi sellersApi = getOrCreateSellersApi(secretsVO);

        final SellerGetMarketParticipation getMarketParticipation = new SellerGetMarketParticipation(sellersApi);
        final ApiProxy<GetMarketplaceParticipationsResponse> apiProxy = new ApiProxy<>(getMarketParticipation);
        final ApiResponse<GetMarketplaceParticipationsResponse> marketplaceParticipationsWithHttpInfo
                = apiProxy.invkWithToken(null, secretsVO.getSeller_id()); // No parameters are needed.
        logger.debug("Server responded.");
        return marketplaceParticipationsWithHttpInfo;

    }

    private FinancesApi getOrCreateFinancesApi(SellerSecretsVO secretsVO) {
        FinancesApi api = financesApiHolder.get(Integer.toHexString(secretsVO.hashCode()));
        if (api == null) {
            api = Helper.buildFinancesApi(secretsVO);
            financesApiHolder.put(Integer.toHexString(secretsVO.hashCode()), api);
            logger.info("Seller API client created.");
        }
        return api;
    }

    private SellersApi getOrCreateSellersApi(SellerSecretsVO secretsVO) {
        SellersApi api = sellerApiHolder.get(Integer.toHexString(secretsVO.hashCode()));
        if (api == null) {
            api = Helper.buildSellerApi(secretsVO);
            sellerApiHolder.put(Integer.toHexString(secretsVO.hashCode()), api);
            logger.info("Seller API client created.");
        }
        return api;
    }
}
