package cn.amazon.aws.rp.spapi.lambda.order;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.amazon.aws.rp.spapi.utils.Helper.logInput;

/**
 * Deprecated - this function is supposed to use with notification API. We don't implement it for now.
 *
 * Invocation chain:
 *  EventBridge Timer -> GetAllSellerCredentialsAndPull -> GetOrderListForOneSeller -> event bus -> here
 *
 * Responsibility:
 *  Get one order and save it to db.
 */
@Deprecated public class GetOneNewOrder implements RequestHandler<ScheduledEvent, String> {

    private static final Logger logger = LoggerFactory.getLogger(GetOneNewOrder.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public GetOneNewOrder() {
    }

    @Override
    public String handleRequest(ScheduledEvent input, Context context) {

        logger.debug("start");
        logInput(logger, input, context, gson);

//        final Order order = gson.fromJson(input.getDetail(), Order.class);
//
//        // Remove prefix, only keep the sellerID. see OrderReceivedEventGenerator.
//        String sellerId = input.getDetailType().replace(OrderReceivedEventGenerator.DETAIL_TYPE_NEW_ORDER, "");
//        final OrdersApi ordersApi = Helper.buildOrdersApi(sellerId);
//        ordersApi.getOrder(order.getAmazonOrderId());

        return null;
    }
}
