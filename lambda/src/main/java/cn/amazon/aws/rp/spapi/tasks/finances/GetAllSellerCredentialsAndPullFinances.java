package cn.amazon.aws.rp.spapi.tasks.finances;

import cn.amazon.aws.rp.spapi.constants.SpApiConstants;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.dynamodb.impl.SpApiSecretDao;
import cn.amazon.aws.rp.spapi.utils.GlobalThreadPool;
import cn.amazon.aws.rp.spapi.utils.Helper;
import cn.amazon.aws.rp.spapi.utils.Utils;
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
public class GetAllSellerCredentialsAndPullFinances implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(GetAllSellerCredentialsAndPullFinances.class);
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private final String input;

	public GetAllSellerCredentialsAndPullFinances(String input) {
		this.input = input;
	}

	@Override
	public void run() {
		handleRequest(input);
	}

	public String handleRequest(String input) {
		String jsonSellerSecrets = input != null ? gson.toJson(input) : "{}";
		//get seller
		List<SellerCredentials> sellerCredentials = SpApiSecretDao.getSellerCredentials();
		if (sellerCredentials.isEmpty()) {
			return SpApiConstants.SUCCESS;
		}
		Helper.logInput(logger, jsonSellerSecrets, gson);
		sellerCredentials.forEach(credentials -> {

		    // Start the job in another lambda.
			logger.info("start pull payment info for a seller: " +credentials.getSeller_id());
			// Get the function name from environment which is set by CDK.
			final String funcName = Utils.getEnv("getFinancesListForOneSellerFuncName");
//			Helper.invokeLambda(funcName, gson.toJson(credentials), true);
			GlobalThreadPool.SCHEDULED_POOL.execute(new ExecuteTaskForOneSeller(gson.toJson(credentials)));
			logger.info("Lambda Started.");
		});
		return SpApiConstants.SUCCESS;
	}
}
