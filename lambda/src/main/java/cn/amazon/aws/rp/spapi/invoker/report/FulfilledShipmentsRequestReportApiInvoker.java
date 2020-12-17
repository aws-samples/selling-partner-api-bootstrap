package cn.amazon.aws.rp.spapi.invoker.report;

import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.ApiResponse;
import cn.amazon.aws.rp.spapi.clients.api.ReportsApi;
import cn.amazon.aws.rp.spapi.clients.model.CreateReportResponse;
import cn.amazon.aws.rp.spapi.clients.model.CreateReportSpecification;
import cn.amazon.aws.rp.spapi.constants.DateConstants;
import cn.amazon.aws.rp.spapi.lambda.requestlimiter.Invokable;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

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
		specification.setDataStartTime(OffsetDateTime.of(LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern(DateConstants.DATE_TIME_FORMAT)), ZoneOffset.UTC));
		specification.setDataEndTime(OffsetDateTime.of(LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern(DateConstants.DATE_TIME_FORMAT)), ZoneOffset.UTC));
		return api.createReportWithHttpInfo(specification);
	}

	@Override
	public String getRateLimiterNameSuffix() {
		return FulfilledShipmentsRequestReportApiInvoker.class.getName();
	}
}
