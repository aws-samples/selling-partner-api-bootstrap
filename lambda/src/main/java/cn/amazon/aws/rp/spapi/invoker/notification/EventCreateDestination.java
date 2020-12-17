package cn.amazon.aws.rp.spapi.invoker.notification;

import cn.amazon.aws.rp.spapi.lambda.requestlimiter.Invokable;
import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.ApiResponse;
import cn.amazon.aws.rp.spapi.clients.api.NotificationsApi;
import cn.amazon.aws.rp.spapi.clients.model.CreateDestinationRequest;
import cn.amazon.aws.rp.spapi.clients.model.CreateDestinationResponse;

import java.util.Map;

public class EventCreateDestination implements Invokable<CreateDestinationResponse> {
    private final NotificationsApi api;

    public EventCreateDestination(NotificationsApi api) {
        this.api = api;
    }

    @Override
    public ApiResponse<CreateDestinationResponse> invoke(Map<String, Object> input) throws ApiException {
        return api.createDestinationWithHttpInfo(
                (CreateDestinationRequest) input.get("createDestinationRequest"));
    }

    @Override
    public String getRateLimiterNameSuffix() {
        return EventCreateDestination.class.getCanonicalName();
    }
}
