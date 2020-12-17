package cn.amazon.aws.rp.spapi.invoker.report;

import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.ApiResponse;
import cn.amazon.aws.rp.spapi.clients.api.ReportsApi;
import cn.amazon.aws.rp.spapi.clients.model.GetReportResponse;
import cn.amazon.aws.rp.spapi.lambda.requestlimiter.Invokable;

import java.util.Map;

/**
 * @description: 生成报告计划
 * @className: FulfilledShipmentsRequestReport
 * @type: JAVA
 * @date: 2020/11/10 19:55
 * @author: zhangkui
 */
public class FulfilledShipmentsGetReportApiInvoker implements Invokable<GetReportResponse> {

	private final ReportsApi api;

	public FulfilledShipmentsGetReportApiInvoker(ReportsApi api){
		this.api = api;
	}

	@Override
	public ApiResponse<GetReportResponse> invoke(Map<String, Object> input) throws ApiException {
		return null;
	}

	@Override
	public String getRateLimiterNameSuffix() {
		return FulfilledShipmentsGetReportApiInvoker.class.getName();
	}
}
