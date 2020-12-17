package cn.amazon.aws.rp.spapi.lambda.report;

import cn.amazon.aws.rp.spapi.clients.ApiResponse;
import cn.amazon.aws.rp.spapi.clients.api.ReportsApi;
import cn.amazon.aws.rp.spapi.clients.api.SellersApi;
import cn.amazon.aws.rp.spapi.clients.model.*;
import cn.amazon.aws.rp.spapi.constants.SpApiConstants;
import cn.amazon.aws.rp.spapi.constants.TaskConstants;
import cn.amazon.aws.rp.spapi.dynamodb.IReportsDao;
import cn.amazon.aws.rp.spapi.dynamodb.ISpApiTaskDao;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SpApiTask;
import cn.amazon.aws.rp.spapi.dynamodb.impl.ReportsDao;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerSecretsVO;
import cn.amazon.aws.rp.spapi.dynamodb.impl.SpApiTaskDao;
import cn.amazon.aws.rp.spapi.enums.ReportTypeEnum;
import cn.amazon.aws.rp.spapi.enums.StatusEnum;
import cn.amazon.aws.rp.spapi.utils.Helper;
import cn.amazon.aws.rp.spapi.invoker.seller.SellerGetMarketParticipation;
import cn.amazon.aws.rp.spapi.invoker.report.FulfilledShipmentsRequestReportApiInvoker;
import cn.amazon.aws.rp.spapi.lambda.requestlimiter.ApiProxy;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: FulfilledShipmentsRequestReport
 * @type: JAVA
 * @date: 2020/11/10 20:10
 * @author: zhangkui
 */
public class FulfilledShipmentsRequestReport implements RequestHandler<ScheduledEvent, String> {

	private static final Logger logger = LoggerFactory.getLogger(FulfilledShipmentsRequestReport.class);
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private Map<String, SellersApi> sellerApiHolder;
	private Map<String, ReportsApi> reportsApiHolder;

	private IReportsDao reportsDao;
	private ISpApiTaskDao spApiTaskDao;

	public FulfilledShipmentsRequestReport() {
		sellerApiHolder = new HashMap<>();
		reportsApiHolder = new HashMap<>();
		reportsDao = new ReportsDao();
		spApiTaskDao = new SpApiTaskDao();
	}

	@Override
	public String handleRequest(ScheduledEvent input, Context context) {
		String jsonSellerSecrets = input != null ? gson.toJson(input) : "{}";
//		SellerSecretsVO sellerSecretsVO = gson.fromJson(jsonSellerSecrets, SellerSecretsVO.class);
		Helper.logInput(logger, jsonSellerSecrets, context, gson);
		//get seller
		List<SellerSecretsVO> secretsVOS = Helper.getSellerSecretsVOS();
		if (secretsVOS.isEmpty()) {
			return SpApiConstants.SUCCESS;
		}

		secretsVOS.forEach(sellerSecretsVO -> {
			try {
				final String sellerKey = sellerSecretsVO.getSeller_id() + "_" + StatusEnum.INIT.getStatus() + "_" + TaskConstants.FULFILLED_SHIPMENT_REQUEST_REPORT;
				//get task
				List<SpApiTask> spApiTaskList = spApiTaskDao.getTask(sellerKey);
				if (spApiTaskList.isEmpty()) {
					return;
				}
				SpApiTask apiTask = spApiTaskList.stream().findFirst().get();
				//get marketPlace
				final ApiResponse<GetMarketplaceParticipationsResponse> marketplaceParticipation = getMarketplaceParticipations(sellerSecretsVO);
				final MarketplaceParticipationList payloadList = marketplaceParticipation.getData().getPayload();
				payloadList.stream().forEach(participation -> {
					requestReportsForOneMkt(participation, sellerSecretsVO, apiTask);
				});
			} catch (Throwable throwable) {
				logger.error("Api call failed", throwable);
			}
		});
		return SpApiConstants.SUCCESS;
	}

	private void requestReportsForOneMkt(MarketplaceParticipation marketplace, SellerSecretsVO sellerSecretsVO, SpApiTask apiTask) {
		reportsDao.put(getReportsListForTimeDelta(sellerSecretsVO, apiTask), marketplace, sellerSecretsVO);
	}

	private String getReportsListForTimeDelta(SellerSecretsVO secretsVO, SpApiTask apiTask) {
		logger.info("enter");
		try {
			final HashMap<String, Object> input = new HashMap<>();
			final ReportsApi reportsApi = getOrCreateReportsApi(secretsVO);
//			final String from = Helper.getIso8601Time(7000);
			final String from = apiTask.getStartTime();
			input.put("startTime", from);
			final String to = apiTask.getEndTime();
			input.put("endTime", to);
			input.put("reportType", ReportTypeEnum._GET_AMAZON_FULFILLED_SHIPMENTS_DATA_.name());
			logger.info(String.format("checking from %s to %s", from, to));
			final ApiResponse<CreateReportResponse> createReportResponse = apiProxyInvokeForReports(secretsVO, reportsApi, input);
			return createReportResponse.getData().getPayload().getReportId();
		} catch (Throwable e) {
			logger.error("invocation of report:{} api failed.", ReportTypeEnum._GET_AMAZON_FULFILLED_SHIPMENTS_DATA_.name(), e);
		}
		return null;
	}

	private ApiResponse<CreateReportResponse> apiProxyInvokeForReports(SellerSecretsVO secretsVO, ReportsApi reportsApi, HashMap<String, Object> input) throws Throwable {

		// Invoke with API proxy
		final FulfilledShipmentsRequestReportApiInvoker reportApiInvoker = new FulfilledShipmentsRequestReportApiInvoker(reportsApi);
		final ApiProxy<CreateReportResponse> apiProxy = new ApiProxy<>(reportApiInvoker);

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

	private ReportsApi getOrCreateReportsApi(SellerSecretsVO secretsVO) {
		ReportsApi api = reportsApiHolder.get(Integer.toHexString(secretsVO.hashCode()));
		if (api == null) {
			api = Helper.buildReportsApi(secretsVO);
			reportsApiHolder.put(Integer.toHexString(secretsVO.hashCode()), api);
			logger.info("Reports API client created.");
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
