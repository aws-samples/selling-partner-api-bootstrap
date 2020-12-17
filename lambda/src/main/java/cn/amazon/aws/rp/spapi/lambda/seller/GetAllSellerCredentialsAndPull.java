package cn.amazon.aws.rp.spapi.lambda.seller;

import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerSecretsVO;
import cn.amazon.aws.rp.spapi.utils.Helper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static cn.amazon.aws.rp.spapi.utils.Helper.getSellerSecretsVOS;
import static cn.amazon.aws.rp.spapi.utils.Helper.logInput;

/**
 * The event bus will trigger this lambda at intervals.
 * This function will then go and retrieve all seller credentials from db and start the StepFunction to query for new orders.
 */
public class GetAllSellerCredentialsAndPull implements RequestHandler<ScheduledEvent, String> {

    private static final Logger logger = LoggerFactory.getLogger(GetAllSellerCredentialsAndPull.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * We can define the request as - RequestHandler<Map<String,String>, String>
     *     Ref - https://docs.amazonaws.cn/en_us/lambda/latest/dg/java-handler.html
     * @param input
     * @param context
     * @return
     */
    @Override
    public String handleRequest(ScheduledEvent input, Context context) {
        logger.info("start");
        logInput(logger, input, context, gson);
        final List<SellerSecretsVO> secretsVOForAllSeller = getSellerSecretsVOS();

        secretsVOForAllSeller.stream().parallel().forEach(
                this::startPullForSeller
        );
        return null;
    }

    private void startPullForSeller(SellerSecretsVO seller) {
        logger.info("start pull order function for seller: " +seller.getSeller_id());

        // Get the function name from environment which is set by CDK.
        final String funcName = System.getenv("getOrderListForOneSellerFuncName");
        logger.info("invoke with - "+gson.toJson(seller)); // FIXME! delete this.
        Helper.invokeLambda(funcName, gson.toJson(seller), true);
        logger.info("Lambda Started.");
    }
}
