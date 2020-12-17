package cn.amazon.aws.rp.spapi.lambda.log;

import cn.amazon.aws.rp.spapi.constants.SpApiConstants;
import cn.amazon.aws.rp.spapi.utils.Helper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQSLogOnly implements RequestHandler<SQSEvent, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(SQSLogOnly.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public Integer handleRequest(SQSEvent input, Context context) {
        Helper.logInput(logger, input, context, gson);
        return SpApiConstants.CODE_200;
    }
}
