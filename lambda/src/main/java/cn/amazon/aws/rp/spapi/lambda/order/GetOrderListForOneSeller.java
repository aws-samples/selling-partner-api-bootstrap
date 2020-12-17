package cn.amazon.aws.rp.spapi.lambda.order;

import cn.amazon.aws.rp.spapi.clients.ApiResponse;
import cn.amazon.aws.rp.spapi.clients.api.OrdersApi;
import cn.amazon.aws.rp.spapi.clients.api.SellersApi;
import cn.amazon.aws.rp.spapi.clients.model.GetMarketplaceParticipationsResponse;
import cn.amazon.aws.rp.spapi.clients.model.GetOrdersResponse;
import cn.amazon.aws.rp.spapi.clients.model.MarketplaceParticipationList;
import cn.amazon.aws.rp.spapi.clients.model.OrderList;
import cn.amazon.aws.rp.spapi.constants.SpApiConstants;
import cn.amazon.aws.rp.spapi.dynamodb.IOrdersDao;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerSecretsVO;
import cn.amazon.aws.rp.spapi.dynamodb.impl.OrdersDao;
import cn.amazon.aws.rp.spapi.eventbridge.OrderReceivedEventGenerator;
import cn.amazon.aws.rp.spapi.invoker.order.OrderGetNewCreatedByTimeSpan;
import cn.amazon.aws.rp.spapi.invoker.seller.SellerGetMarketParticipation;
import cn.amazon.aws.rp.spapi.lambda.requestlimiter.ApiProxy;
import cn.amazon.aws.rp.spapi.lambda.vo.OrdersWithSeller;
import cn.amazon.aws.rp.spapi.utils.Helper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Invocation chain:
 * EventBridge Timer -> GetAllSellerCredentialsAndPull -> here
 * The retried orders will be sent to event bus.
 */
public class GetOrderListForOneSeller implements RequestHandler<Object, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(GetOrderListForOneSeller.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // According to the nature of Lambda, in most cases it will not create memory leak.
    private Map<String, SellersApi> sellerApiHolder;
    private Map<String, OrdersApi> orderApiHolder;

    private IOrdersDao ordersDao;

    public GetOrderListForOneSeller() {
        sellerApiHolder = new HashMap<>();
        orderApiHolder = new HashMap<>();
        ordersDao = new OrdersDao();
    }

    @Override
    public Integer handleRequest(Object input, Context context) {

        String jsonSellerSecrets = input != null ? gson.toJson(input) : "{}";

        // FIXME! DELETEME!  Security LEAK! ONLY FOR DEBUG

        SellerSecretsVO sellerSecretsVO = gson.fromJson(jsonSellerSecrets, SellerSecretsVO.class);

        Helper.logInput(logger, jsonSellerSecrets, context, gson);
        final ApiResponse<GetMarketplaceParticipationsResponse> marketplaceParticipations;
        try {
            marketplaceParticipations = getMarketplaceParticipations(sellerSecretsVO);
        } catch (Throwable throwable) {
            logger.error("Api call failed", throwable);
            return SpApiConstants.CODE_500;
        }
        // A seller can have sell on US / DE / etc, these are marketplaces.
        final MarketplaceParticipationList payloadList = marketplaceParticipations.getData().getPayload();
        final List<String> mktList = payloadList.stream().map(paticipation -> {
            return paticipation.getMarketplace().getId();
        }).collect(Collectors.toList());

        requestOrders(mktList, sellerSecretsVO);

        return SpApiConstants.CODE_200;
    }

    private void requestOrders(List<String> mktId, SellerSecretsVO secretsVO) {

        // Request from SP API.
        final OrderList orderListForTimeDelta = getOrderListForTimeDelta(secretsVO, mktId);

        // Get new orders
        if (orderListForTimeDelta != null) {
            final OrderList orders = orderListForTimeDelta;
            logger.info("Found new orders, send to event bus. Number of order:" + orders.size());

            // Save to db.
            ordersDao.put(orders, secretsVO.getSeller_id());
            // Put to event bridge.
            OrderReceivedEventGenerator.put(new OrdersWithSeller(orders, secretsVO.getSeller_id()));
        } else {
            logger.info("NO ORDER FOUND - for this api query.");
        }
    }

    /**
     * Time Delta should be the interval of event bus timer. If event bus timer calls for getting new order every 1 minute,
     * Then we should get the orders generated within the same time. (With some overlap to avoid miss order in our end.)
     * TODO Maybe we need to pull orders generated 2 minutes before. - To avoid lost order due to final consistency.
     * TODO Inject the time delta from CDK parameter to avoid hard code.
     */
    private OrderList getOrderListForTimeDelta(
            SellerSecretsVO secretsVO,
            List<String> marketplaceId
    ) {

        logger.info("enter");
        // Order API is per Seller - they have different secrets.
        final OrdersApi ordersApi = getOrCreateOrdersApi(secretsVO);

        try {
            // Prepare parameters
            final HashMap<String, Object> input = new HashMap<>();
            input.put("marketplaceIds", marketplaceId);
            String from = Helper.getIso8601Time(70);

            input.put("createdAfter", from);

            /*
             Notice! You can not specify createdBefore with after. Server respond 400 for that request.
               Also if you don't want overlap of time you can check the result of previous API. It tells you until when
               the orders are returned.
             */
            //  input.put("createdBefore", to);
            input.put("maxResultsPerPage", "100"); 
            logger.info(String.format("checking from %s", from));

            final ApiResponse<GetOrdersResponse> ordersWithHttpInfo = apiProxyInvokeForOrders(secretsVO, ordersApi, input);

            logger.debug("Server responded.");
            final String nextToken = ordersWithHttpInfo.getData().getPayload().getNextToken();
            OrderList orders = ordersWithHttpInfo.getData().getPayload().getOrders();

            if (nextToken != null) { // Need to get more in the same time.
                logger.info("pulling more orders according to next token.");
                orders = pullOrdersByNextToken(input, secretsVO, marketplaceId, nextToken, orders);
            }
            return orders;

        } catch (Throwable e) {
            logger.error("invocation of order api failed.", e);
        }
        return null;
    }

    private ApiResponse<GetOrdersResponse> apiProxyInvokeForOrders(SellerSecretsVO secretsVO, OrdersApi ordersApi, HashMap<String, Object> input) throws Throwable {

        // Invoke with API proxy
        final OrderGetNewCreatedByTimeSpan getOrder = new OrderGetNewCreatedByTimeSpan(ordersApi);
        final ApiProxy<GetOrdersResponse> apiProxy = new ApiProxy<>(getOrder);

        // Invoke
        return apiProxy.invkWithToken(input, secretsVO.getSeller_id());
    }

    /**
     * Will recurrent call itself if result still has nextToken.
     * All resulting orders will be put to one object.
     */
    private OrderList pullOrdersByNextToken(HashMap<String, Object> input,
                                            SellerSecretsVO secretsVO,
                                            List<String> mpId,
                                            String nextToken,
                                            OrderList previousOrders)
            throws Throwable {
        logger.info("current pulled orders: " + previousOrders.size());
        final OrdersApi ordersApi = getOrCreateOrdersApi(secretsVO);
        try {

            input.put("nextToken", nextToken);

            final ApiResponse<GetOrdersResponse> ordersWithHttpInfo = apiProxyInvokeForOrders(secretsVO, ordersApi, input);
            final OrderList orders = ordersWithHttpInfo.getData().getPayload().getOrders();
            if (orders != null) {
                previousOrders.addAll(orders);
            }
            final String newToken = ordersWithHttpInfo.getData().getPayload().getNextToken();
            if (newToken != null) { // Recurrent call.
                logger.info("Still has token: " + newToken);
                pullOrdersByNextToken(input, secretsVO, mpId, nextToken, previousOrders);
            }
            return previousOrders;
        } catch (Throwable e) {
            logger.error("Cannot pull order using token. " + nextToken, e);
            throw e;
        }
    }

    private ApiResponse<GetMarketplaceParticipationsResponse> getMarketplaceParticipations(SellerSecretsVO secretsVO) throws Throwable {

        // Seller API is per Seller - they have different secrets.
        final SellersApi sellersApi = getOrCreateSellersApi(secretsVO);

        final SellerGetMarketParticipation getMarketParticipation = new SellerGetMarketParticipation(sellersApi);
        final ApiProxy<GetMarketplaceParticipationsResponse> apiProxy = new ApiProxy<>(getMarketParticipation);
        final ApiResponse<GetMarketplaceParticipationsResponse> marketplaceParticipationsWithHttpInfo
                = apiProxy.invkWithToken(null, secretsVO.getSeller_id()); // No parameters are needed.
        logger.debug("Server responded.");
        return marketplaceParticipationsWithHttpInfo;

    }

    private OrdersApi getOrCreateOrdersApi(SellerSecretsVO secretsVO) {
        OrdersApi api = orderApiHolder.get(Integer.toHexString(secretsVO.hashCode()));
        if (api == null) {
            api = Helper.buildOrdersApi(secretsVO);
            orderApiHolder.put(Integer.toHexString(secretsVO.hashCode()), api);
            logger.info("order API client created.");
        }
        return api;
    }

    private SellersApi getOrCreateSellersApi(SellerSecretsVO secretsVO) {
        SellersApi api = sellerApiHolder.get(Integer.toHexString(secretsVO.hashCode()));
        if (api == null) {
            api = Helper.buildSellerApi(secretsVO);
            sellerApiHolder.put(Integer.toHexString(secretsVO.hashCode()), api);
            logger.info("Seller API client created.");
        }
        return api;
    }


}
