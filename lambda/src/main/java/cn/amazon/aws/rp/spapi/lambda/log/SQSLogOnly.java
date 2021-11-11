package cn.amazon.aws.rp.spapi.lambda.log;

public class SQSLogOnly {

}

/**
 * Log events from event subscription API to lambda
 */
//public class SQSLogOnly implements RequestHandler<SQSEvent, Integer> {
//
//    private static final Logger logger = LoggerFactory.getLogger(SQSLogOnly.class);
//    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
//
//    @Override
//    public Integer handleRequest(SQSEvent input, Context context) {
//        Helper.logInput(logger, input, context, gson);
//        return SpApiConstants.CODE_200;
//    }
//}
