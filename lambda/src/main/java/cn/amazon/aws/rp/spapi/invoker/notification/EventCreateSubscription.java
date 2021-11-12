package cn.amazon.aws.rp.spapi.invoker.notification;

import cn.amazon.aws.rp.spapi.tasks.requestlimiter.Invokable;
import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.ApiResponse;
import cn.amazon.aws.rp.spapi.clients.api.NotificationsApi;
import cn.amazon.aws.rp.spapi.clients.model.CreateSubscriptionRequest;
import cn.amazon.aws.rp.spapi.clients.model.CreateSubscriptionResponse;

import java.util.Map;

public class EventCreateSubscription implements Invokable<CreateSubscriptionResponse> {
    private final NotificationsApi api;

    public EventCreateSubscription(NotificationsApi api) {
        this.api = api;
    }

    @Override
    public ApiResponse<CreateSubscriptionResponse> invoke(Map<String, Object> input) throws ApiException {
        final CreateSubscriptionRequest createSubscriptionRequest =
                (CreateSubscriptionRequest) input.get("createSubscriptionRequest");
        final String notificationType = (String) input.get("notificationType");
        return api.createSubscriptionWithHttpInfo(createSubscriptionRequest, notificationType);
    }

    @Override
    public String getRateLimiterNameSuffix() {
        return EventCreateDestination.class.getCanonicalName();
    }
}
