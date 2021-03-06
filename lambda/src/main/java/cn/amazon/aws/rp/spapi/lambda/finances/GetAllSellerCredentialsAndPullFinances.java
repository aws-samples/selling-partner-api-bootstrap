package cn.amazon.aws.rp.spapi.lambda.finances;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * @description: 	Retrieve jobs/tasks (time span to query) from DDB. And start new Lambda to work on the jobs.
 * 					Job status is synchronized on DB as well.
 * @className: FinancesEventsList
 * @type: JAVA
 * @date: 2020/11/10 11:07
 * @author: zhangkui
 */
public class GetAllSellerCredentialsAndPullFinances implements RequestHandler<ScheduledEvent, String> {

	private static final Logger logger = LoggerFactory.getLogger(GetAllSellerCredentialsAndPullFinances.class);
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Override
	public String handleRequest(ScheduledEvent input, Context context) {
		String jsonSellerSecrets = input != null ? gson.toJson(input) : "{}";
		//get seller
		List<SellerCredentials> sellerCredentials = SpApiSecretDao.getSellerCredentials();
		if (sellerCredentials.isEmpty()) {
			return SpApiConstants.SUCCESS;
		}
		Helper.logInput(logger, jsonSellerSecrets, context, gson);
		sellerCredentials.forEach(credentials -> {

		    // Start the job in another lambda.
			logger.info("start pull payment info for a seller: " +credentials.getSeller_id());
			// Get the function name from environment which is set by CDK.
			final String funcName = Utils.getEnv("getFinancesListForOneSellerFuncName");
			Helper.invokeLambda(funcName, gson.toJson(credentials), true);
			logger.info("Lambda Started.");
		});
		return SpApiConstants.SUCCESS;
	}

}
