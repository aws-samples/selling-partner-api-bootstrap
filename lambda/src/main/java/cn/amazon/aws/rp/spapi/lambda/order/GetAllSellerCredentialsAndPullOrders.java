package cn.amazon.aws.rp.spapi.lambda.order;

import cn.amazon.aws.rp.spapi.clients.ApiResponse;
import cn.amazon.aws.rp.spapi.clients.api.SellersApi;
import cn.amazon.aws.rp.spapi.clients.model.GetMarketplaceParticipationsResponse;
import cn.amazon.aws.rp.spapi.clients.model.Marketplace;
import cn.amazon.aws.rp.spapi.clients.model.MarketplaceParticipation;
import cn.amazon.aws.rp.spapi.clients.model.MarketplaceParticipationList;
import cn.amazon.aws.rp.spapi.constants.SpApiConstants;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.dynamodb.impl.SpApiSecretDao;
import cn.amazon.aws.rp.spapi.utils.Helper;
import cn.amazon.aws.rp.spapi.utils.Utils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static cn.amazon.aws.rp.spapi.utils.Helper.logInput;

/**
 * The event bus will trigger this lambda at intervals.
 * This function will then go and retrieve all seller credentials from db and start the StepFunction to query for new orders.
 */
public class GetAllSellerCredentialsAndPullOrders implements RequestHandler<ScheduledEvent, String> {

    private static final Logger logger = LoggerFactory.getLogger(GetAllSellerCredentialsAndPullOrders.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * We can define the request as - RequestHandler<Map<String,String>, String>
     * Ref - https://docs.amazonaws.cn/en_us/lambda/latest/dg/java-handler.html
     *
     * @param input
     * @param context
     * @return
     */
    @Override
    public String handleRequest(ScheduledEvent input, Context context) {
        logger.info("start");
        logInput(logger, input, context, gson);
        final List<SellerCredentials> secretsVOForAllSeller = SpApiSecretDao.getSellerCredentials();
        ;

        secretsVOForAllSeller.stream().parallel().forEach(sellerCredentials -> {
                    try {
                        if(Utils.isNullOrEmpty(sellerCredentials.getMarketplaces())) {
                            final ApiResponse<GetMarketplaceParticipationsResponse> marketplaceParticipations = SellersApi.getMarketplaceParticipations(sellerCredentials);
                            final MarketplaceParticipationList payloadList = marketplaceParticipations.getData().getPayload();
//                            final List<SellerCredentials.Marketplace> mktList = payloadList.stream().map(p->{
//                                SellerCredentials.Marketplace mkp = new SellerCredentials.Marketplace();
//                                mkp.setId(p.getMarketplace().getId());
//                                return mkp;
//                            }).collect(Collectors.toList());
                            final List<Marketplace> mktList = payloadList.stream().map(MarketplaceParticipation::getMarketplace).collect(Collectors.toList());
                            sellerCredentials.setMarketplaces(mktList);
                            SpApiSecretDao.updateMarketplace(sellerCredentials);

                        }
                        this.startPullForSeller(sellerCredentials);

                    } catch (Throwable throwable) {
                        logger.error("marketplace Api call failed", throwable);
                    }
                }
        );
        return null;
    }

    private void startPullForSeller(SellerCredentials seller) {
        logger.info("start pull order function for seller: " + seller.getSeller_id());

        // Get the function name from environment which is set by CDK.
        final String funcName = Utils.getEnv("getOrderListForOneSellerFuncName");
        logger.debug("invoke with - " + gson.toJson(seller)); // FIXME! delete this.
        Helper.invokeLambda(funcName, gson.toJson(seller), true);
        logger.info("Lambda Started.");
    }
}
