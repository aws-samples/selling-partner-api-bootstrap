package cn.amazon.aws.rp.spapi.eventbridge;

import cn.amazon.aws.rp.spapi.lambda.vo.OrdersWithSeller;
import cn.amazon.aws.rp.spapi.utils.Utils;
import com.amazonaws.services.eventbridge.AmazonEventBridgeClient;
import com.amazonaws.services.eventbridge.model.PutEventsRequest;
import com.amazonaws.services.eventbridge.model.PutEventsRequestEntry;
import com.amazonaws.services.eventbridge.model.PutEventsResult;
import com.amazonaws.services.eventbridge.model.PutEventsResultEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cn.amazon.aws.rp.spapi.clients.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * https://docs.aws.amazon.com/eventbridge/latest/userguide/add-events-putevents.html
 */
public class OrderReceivedEventGenerator {
    private static Logger logger = LoggerFactory.getLogger(OrderReceivedEventGenerator.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final AmazonEventBridgeClient eventBridgeClient =
            (AmazonEventBridgeClient) AmazonEventBridgeClient.builder().build();
    private static String BUS_NAME = "sp-api";
    private static final String SOURCE_NAME = "com.aws.rapidprototyping.spapi";
    // See the eventBusNewOrderRule from CDK scripts.
    public static final String DETAIL_TYPE_NEW_ORDER = "newOrder||";


    public static void put(OrdersWithSeller ordersWithSeller) {

        logger.debug("received put event.");

        getEventBusName();


        List<PutEventsRequestEntry> putEventsRequestEntryList = new ArrayList<>();

        for (Order order : ordersWithSeller.getOrders()) {
            putEventsRequestEntryList.add(new PutEventsRequestEntry()
                    .withTime(new Date())
                    .withEventBusName(BUS_NAME)
                    .withSource(SOURCE_NAME)
                    .withDetailType(DETAIL_TYPE_NEW_ORDER + ordersWithSeller.getSellerId())
                    .withDetail(gson.toJson(order)));
            logger.info(String.format("put order [%s] into event bus.", gson.toJson(order)));
        }

        final int size = ordersWithSeller.getOrders().size();


        // The max batch event size is 10, so we need to split the list if its size is more than 10.
        final int batch = size / 10;
        final int left = size % 10;

        for (int i = 0; i < batch; i++) {
            logger.info("from " + i * 10 + ",to " + (i + 1) * 10);
            List<PutEventsRequestEntry> subList = putEventsRequestEntryList.subList(i * 10, (i + 1) * 10);
            putImp(subList);
        }
        logger.info("from " + (size-left) + ",to " + size);
        List<PutEventsRequestEntry> subList = putEventsRequestEntryList.subList(size - left, size);
        putImp(subList);
        logger.info("exist");
    }

    public static void putImp(List<PutEventsRequestEntry> subList) {
        PutEventsRequest putEventsRequest = new PutEventsRequest();
        putEventsRequest.withEntries(subList);
        if (subList.isEmpty()) {
            logger.info("No orders found.");
            return;
        }
        PutEventsResult putEventsResult = eventBridgeClient.putEvents(putEventsRequest);

        while (putEventsResult.getFailedEntryCount() > 0) {
            logger.error("failed and retry.");
            final List<PutEventsRequestEntry> failedEntriesList = new ArrayList<>();
            final List<PutEventsResultEntry> PutEventsResultEntryList = putEventsResult.getEntries();
            for (int j = 0; j < PutEventsResultEntryList.size(); j++) {
                final PutEventsRequestEntry putEventsRequestEntry = subList.get(j);
                final PutEventsResultEntry putEventsResultEntry = PutEventsResultEntryList.get(j);
                if (putEventsResultEntry.getErrorCode() != null) {
                    failedEntriesList.add(putEventsRequestEntry);
                }
            }

            subList = failedEntriesList;
            putEventsRequest.setEntries(subList);
            logger.warn("Retry put to event bus.");
            putEventsResult = eventBridgeClient.putEvents(putEventsRequest);
        }
    }

    private static void getEventBusName() {
        final String event_bus_name = Utils.getEnv("EVENT_BUS_NAME");
        if (event_bus_name != null) {
            BUS_NAME = event_bus_name;
        }
    }

    /**
     * Check UT - This is for UT to test event bus.
     */
    public void put() {
        logger.debug("received put event.");
        PutEventsRequestEntry requestEntry = new PutEventsRequestEntry()
                .withTime(new Date())
                .withEventBusName(BUS_NAME)
                .withSource(SOURCE_NAME)
                .withDetailType(DETAIL_TYPE_NEW_ORDER)
                .withDetail("{ \"key1\": \"value1\", \"key2\": \"value2\" }");

        List<PutEventsRequestEntry> putEventsRequestEntryList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            putEventsRequestEntryList.add(requestEntry);
        }

        PutEventsRequest putEventsRequest = new PutEventsRequest();
        putEventsRequest.withEntries(putEventsRequestEntryList);
        PutEventsResult putEventsResult = eventBridgeClient.putEvents(putEventsRequest);

        while (putEventsResult.getFailedEntryCount() > 0) {
            logger.error("failed and retry.");
            final List<PutEventsRequestEntry> failedEntriesList = new ArrayList<>();
            final List<PutEventsResultEntry> PutEventsResultEntryList = putEventsResult.getEntries();
            for (int i = 0; i < PutEventsResultEntryList.size(); i++) {
                final PutEventsRequestEntry putEventsRequestEntry = putEventsRequestEntryList.get(i);
                final PutEventsResultEntry putEventsResultEntry = PutEventsResultEntryList.get(i);
                if (putEventsResultEntry.getErrorCode() != null) {
                    failedEntriesList.add(putEventsRequestEntry);
                }
            }

            putEventsRequestEntryList = failedEntriesList;
            putEventsRequest.setEntries(putEventsRequestEntryList);
            putEventsResult = eventBridgeClient.putEvents(putEventsRequest);
            logger.error("retried.");
        }
        logger.info("exist");
    }


}
