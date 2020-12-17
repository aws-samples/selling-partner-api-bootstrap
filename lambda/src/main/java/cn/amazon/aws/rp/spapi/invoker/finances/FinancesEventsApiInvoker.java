package cn.amazon.aws.rp.spapi.invoker.finances;

import cn.amazon.aws.rp.spapi.constants.DateConstants;
import cn.amazon.aws.rp.spapi.constants.SpApiConstants;
import cn.amazon.aws.rp.spapi.lambda.requestlimiter.Invokable;
import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.ApiResponse;
import cn.amazon.aws.rp.spapi.clients.api.FinancesApi;
import cn.amazon.aws.rp.spapi.clients.model.ListFinancialEventsResponse;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Map;

/**
 * @description:
 * @className: FinancesEventsApiInvoker
 * @type: JAVA
 * @date: 2020/11/10 10:48
 * @author: zhangkui
 */
public class FinancesEventsApiInvoker implements Invokable<ListFinancialEventsResponse>{

	private final FinancesApi api;

	public FinancesEventsApiInvoker(FinancesApi api){
		this.api = api;
	}

	@Override
	public ApiResponse<ListFinancialEventsResponse> invoke(Map<String, Object> input) throws ApiException {
		final String postedAfter = (String) input.get("postedAfter");
		final String postedBefore = (String) input.get("postedBefore");
		final String nextToken = (String) input.get("nextToken");
		return api.listFinancialEventsWithHttpInfo(
				SpApiConstants.PAGE_SIZE
				, OffsetDateTime.of(LocalDateTime.parse(postedAfter, DateTimeFormatter.ofPattern(DateConstants.DATE_TIME_FORMAT)), ZoneOffset.UTC)
				, OffsetDateTime.of(LocalDateTime.parse(postedBefore, DateTimeFormatter.ofPattern(DateConstants.DATE_TIME_FORMAT)), ZoneOffset.UTC)
				, nextToken);
	}

	@Override
	public String getRateLimiterNameSuffix() {
		return FinancesEventsApiInvoker.class.getName();
	}

	public static void main(String[] args) {
		System.out.println(OffsetDateTime.of(LocalDateTime.parse("2020-11-01 00:00:00", DateTimeFormatter.ofPattern(DateConstants.DATE_TIME_FORMAT)), ZoneOffset.UTC));
	}
}
