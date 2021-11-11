package cn.amazon.aws.rp.spapi.lambda.report;

import cn.amazon.aws.rp.spapi.clients.ApiResponse;
import cn.amazon.aws.rp.spapi.clients.api.ReportsApi;
import cn.amazon.aws.rp.spapi.clients.api.SellersApi;
import cn.amazon.aws.rp.spapi.clients.model.CreateReportResponse;
import cn.amazon.aws.rp.spapi.clients.model.GetMarketplaceParticipationsResponse;
import cn.amazon.aws.rp.spapi.clients.model.MarketplaceParticipation;
import cn.amazon.aws.rp.spapi.clients.model.MarketplaceParticipationList;
import cn.amazon.aws.rp.spapi.constants.SpApiConstants;
import cn.amazon.aws.rp.spapi.constants.TaskConstants;
import cn.amazon.aws.rp.spapi.dynamodb.IReportsDao;
import cn.amazon.aws.rp.spapi.dynamodb.ISpApiTaskDao;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SpApiTask;
import cn.amazon.aws.rp.spapi.dynamodb.impl.ReportsDao;
import cn.amazon.aws.rp.spapi.dynamodb.impl.SpApiSecretDao;
import cn.amazon.aws.rp.spapi.dynamodb.impl.SpApiTaskDao;
import cn.amazon.aws.rp.spapi.enums.ReportTypeEnum;
import cn.amazon.aws.rp.spapi.enums.StatusEnum;
import cn.amazon.aws.rp.spapi.invoker.report.FulfilledShipmentsRequestReportApiInvoker;
import cn.amazon.aws.rp.spapi.lambda.requestlimiter.ApiProxy;
import cn.amazon.aws.rp.spapi.utils.Helper;
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
public class FulfilledShipmentsRequestReport {

	private static final Logger logger = LoggerFactory.getLogger(FulfilledShipmentsRequestReport.class);
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private Map<String, ReportsApi> reportsApiHolder;

	private IReportsDao reportsDao;
	private ISpApiTaskDao spApiTaskDao;

	public FulfilledShipmentsRequestReport() {
		reportsApiHolder = new HashMap<>();
		reportsDao = new ReportsDao();
		spApiTaskDao = new SpApiTaskDao();
	}

public String handleRequest(Object input) {
		String jsonSellerSecrets = input != null ? gson.toJson(input) : "{}";
//		SellerSecretsVO sellerSecretsVO = gson.fromJson(jsonSellerSecrets, SellerSecretsVO.class);
		Helper.logInput(logger, jsonSellerSecrets, gson);
		//get seller
		List<SellerCredentials> sellerCredentials = SpApiSecretDao.getSellerCredentials();
		if (sellerCredentials.isEmpty()) {
			return SpApiConstants.SUCCESS;
		}

		sellerCredentials.forEach(credentials -> {
			try {
				final String sellerKey = credentials.getSeller_id() + "_" + StatusEnum.INIT.getStatus() + "_" + TaskConstants.FULFILLED_SHIPMENT_REQUEST_REPORT;
				//get task
				List<SpApiTask> spApiTaskList = spApiTaskDao.getTask(sellerKey);
				if (spApiTaskList.isEmpty()) {
					return;
				}
				SpApiTask apiTask = spApiTaskList.stream().findFirst().get();
				//get marketPlace
				final ApiResponse<GetMarketplaceParticipationsResponse> marketplaceParticipation = SellersApi.getMarketplaceParticipations(credentials);
				final MarketplaceParticipationList payloadList = marketplaceParticipation.getData().getPayload();
				payloadList.stream().forEach(participation -> {
					requestReportsForOneMkt(participation, credentials, apiTask);
				});
			} catch (Throwable throwable) {
				logger.error("Api call failed", throwable);
			}
		});
		return SpApiConstants.SUCCESS;
	}

	private void requestReportsForOneMkt(MarketplaceParticipation marketplace, SellerCredentials sellerCredentials, SpApiTask apiTask) {
		reportsDao.put(getReportsListForTimeDelta(sellerCredentials, apiTask), marketplace, sellerCredentials);
	}

	private String getReportsListForTimeDelta(SellerCredentials sellerCredentials, SpApiTask apiTask) {
		logger.info("enter");
		try {
			final HashMap<String, Object> input = new HashMap<>();
			final ReportsApi reportsApi = getOrCreateReportsApi(sellerCredentials);
//			final String from = Helper.getIso8601Time(7000);
			final String from = apiTask.getStartTime();
			input.put("startTime", from);
			final String to = apiTask.getEndTime();
			input.put("endTime", to);
			input.put("reportType", ReportTypeEnum._GET_AMAZON_FULFILLED_SHIPMENTS_DATA_.name());
			logger.info(String.format("checking from %s to %s", from, to));
			final ApiResponse<CreateReportResponse> createReportResponse = apiProxyInvokeForReports(sellerCredentials, reportsApi, input);
			return createReportResponse.getData().getPayload().getReportId();
		} catch (Throwable e) {
			logger.error("invocation of report:{} api failed.", ReportTypeEnum._GET_AMAZON_FULFILLED_SHIPMENTS_DATA_.name(), e);
		}
		return null;
	}

	private ApiResponse<CreateReportResponse> apiProxyInvokeForReports(SellerCredentials secretsVO, ReportsApi reportsApi, HashMap<String, Object> input) throws Throwable {

		// Invoke with API proxy
		final FulfilledShipmentsRequestReportApiInvoker reportApiInvoker = new FulfilledShipmentsRequestReportApiInvoker(reportsApi);
		final ApiProxy<CreateReportResponse> apiProxy = new ApiProxy<>(reportApiInvoker);

		// Invoke
		return apiProxy.invkWithToken(input, secretsVO.getSeller_id());
	}

	private ReportsApi getOrCreateReportsApi(SellerCredentials sellerCredentials) throws NoSuchFieldException, IllegalAccessException {
		ReportsApi api = reportsApiHolder.get(Integer.toHexString(sellerCredentials.hashCode()));
		if (api == null) {
			api = ReportsApi.buildReportsApi(sellerCredentials);
			reportsApiHolder.put(Integer.toHexString(sellerCredentials.hashCode()), api);
			logger.info("Reports API client created.");
		}
		return api;
	}

}
