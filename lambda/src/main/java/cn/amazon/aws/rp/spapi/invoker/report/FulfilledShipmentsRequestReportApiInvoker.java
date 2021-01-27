package cn.amazon.aws.rp.spapi.invoker.report;

import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.ApiResponse;
import cn.amazon.aws.rp.spapi.clients.api.ReportsApi;
import cn.amazon.aws.rp.spapi.clients.model.CreateReportResponse;
import cn.amazon.aws.rp.spapi.clients.model.CreateReportSpecification;
import cn.amazon.aws.rp.spapi.lambda.requestlimiter.Invokable;
import cn.amazon.aws.rp.spapi.utils.DateUtil;


import java.util.Map;

/**
 * @description: 生成报告计划
 * @className: FulfilledShipmentsRequestReport
 * @type: JAVA
 * @date: 2020/11/10 19:55
 * @author: zhangkui
 */
public class FulfilledShipmentsRequestReportApiInvoker implements Invokable<CreateReportResponse> {

	private final ReportsApi api;

	public FulfilledShipmentsRequestReportApiInvoker(ReportsApi api){
		this.api = api;
	}

	@Override
	public ApiResponse<CreateReportResponse> invoke(Map<String, Object> input) throws ApiException {
		CreateReportSpecification specification = new CreateReportSpecification();
		final String startTime = (String) input.get("startTime");
		final String endTime = (String) input.get("endTime");
		final String reportType = (String) input.get("reportType");
		specification.setReportType(reportType);
		specification.setDataStartTime(DateUtil.getOf(startTime));
		specification.setDataEndTime(DateUtil.getOf(endTime));
		return api.createReportWithHttpInfo(specification);
	}



	@Override
	public String getRateLimiterNameSuffix() {
		return FulfilledShipmentsRequestReportApiInvoker.class.getName();
	}
}
