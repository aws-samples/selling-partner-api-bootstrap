package cn.amazon.aws.rp.spapi.invoker.order;

import cn.amazon.aws.rp.spapi.tasks.requestlimiter.Invokable;
import cn.amazon.aws.rp.spapi.tasks.requestlimiter.RateException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.ApiResponse;
import cn.amazon.aws.rp.spapi.clients.api.OrdersApi;
import cn.amazon.aws.rp.spapi.clients.model.GetOrdersResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class OrderGetNewCreatedByTimeSpan implements Invokable<GetOrdersResponse> {

    private static final Logger logger = LoggerFactory.getLogger(OrderGetNewCreatedByTimeSpan.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final OrdersApi api;

    public OrderGetNewCreatedByTimeSpan(OrdersApi api) {
        this.api = api;
    }

    /**
     * Possible signature in input.
     *      * @param marketplaceIds           A list of MarketplaceId values. Used to select orders that were placed in the specified marketplaces. (required)
     *      * @param createdAfter             A date used for selecting orders created after (or at) a specified time. Only orders placed after the specified time are returned. Either the CreatedAfter parameter or the LastUpdatedAfter parameter is required. Both cannot be empty. The date must be in ISO 8601 format. (optional)
     *      * @param createdBefore            A date used for selecting orders created before (or at) a specified time. Only orders placed before the specified time are returned. The date must be in ISO 8601 format. (optional)
     *      * @param lastUpdatedAfter         A date used for selecting orders that were last updated after (or at) a specified time. An update is defined as any change in order status, including the creation of a new order. Includes updates made by Amazon and by the seller. The date must be in ISO 8601 format. (optional)
     *      * @param lastUpdatedBefore        A date used for selecting orders that were last updated before (or at) a specified time. An update is defined as any change in order status, including the creation of a new order. Includes updates made by Amazon and by the seller. The date must be in ISO 8601 format. (optional)
     *      * @param orderStatuses            A list of OrderStatus values used to filter the results. Possible values: PendingAvailability (This status is available for pre-orders only. The order has been placed, payment has not been authorized, and the release date of the item is in the future.); Pending (The order has been placed but payment has not been authorized); Unshipped (Payment has been authorized and the order is ready for shipment, but no items in the order have been shipped); PartiallyShipped (One or more, but not all, items in the order have been shipped); Shipped (All items in the order have been shipped); InvoiceUnconfirmed (All items in the order have been shipped. The seller has not yet given confirmation to Amazon that the invoice has been shipped to the buyer.); Canceled (The order has been canceled); and Unfulfillable (The order cannot be fulfilled. This state applies only to Multi-Channel Fulfillment orders.). (optional)
     *      * @param fulfillmentChannels      A list that indicates how an order was fulfilled. Filters the results by fulfillment channel. Possible values: FBA (Fulfillment by Amazon); SellerFulfilled (Fulfilled by the seller). (optional)
     *      * @param paymentMethods           A list of payment method values. Used to select orders paid using the specified payment methods. Possible values: COD (Cash on delivery); CVS (Convenience store payment); Other (Any payment method other than COD or CVS). (optional)
     *      * @param buyerEmail               The email address of a buyer. Used to select orders that contain the specified email address. (optional)
     *      * @param sellerOrderId            An order identifier that is specified by the seller. Used to select only the orders that match the order identifier. If SellerOrderId is specified, then FulfillmentChannels, OrderStatuses, PaymentMethod, LastUpdatedAfter, LastUpdatedBefore, and BuyerEmail cannot be specified. (optional)
     *      * @param maxResultsPerPage        A number that indicates the maximum number of orders that can be returned per page. Value must be 1 - 100. Default 100. (optional)
     *      * @param easyShipShipmentStatuses A list of EasyShipShipmentStatus values. Used to select Easy Ship orders with statuses that match the specified  values. If EasyShipShipmentStatus is specified, only Amazon Easy Ship orders are returned.Possible values: PendingPickUp (Amazon has not yet picked up the package from the seller). LabelCanceled (The seller canceled the pickup). PickedUp (Amazon has picked up the package from the seller). AtOriginFC (The packaged is at the origin fulfillment center). AtDestinationFC (The package is at the destination fulfillment center). OutForDelivery (The package is out for delivery). Damaged (The package was damaged by the carrier). Delivered (The package has been delivered to the buyer). RejectedByBuyer (The package has been rejected by the buyer). Undeliverable (The package cannot be delivered). ReturnedToSeller (The package was not delivered to the buyer and was returned to the seller). ReturningToSeller (The package was not delivered to the buyer and is being returned to the seller). (optional)
     *      * @param nextToken                A string token returned in the response of your previous request. (optional)
     *      * @param amazonOrderIds           A list of AmazonOrderId values. An AmazonOrderId is an Amazon-defined order identifier, in 3-7-7 format. (optional)
     *
     * @param input
     * @return
     * @throws RateException
     */
    @Override
    public ApiResponse<GetOrdersResponse> invoke(Map<String, Object> input) throws ApiException {

        final List<String> marketplaceIds = (List<String>) input.get("marketplaceIds");
        final String createdAfter = (String) input.get("createdAfter");
        final String createdBefore = (String) input.get("createdBefore");
        final String nextToken = (String) input.get("nextToken");
        return api.getOrdersWithHttpInfo(
                marketplaceIds,
                createdAfter,
                createdBefore,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                nextToken,
                null);
    }

    @Override
    public String getRateLimiterNameSuffix() {
        return OrderGetNewCreatedByTimeSpan.class.getCanonicalName();
    }
}
