package cn.amazon.aws.rp.spapi;

import cn.amazon.aws.rp.spapi.tasks.finances.GetAllSellerCredentialsAndPullFinances;
import cn.amazon.aws.rp.spapi.tasks.order.GetAllSellerCredentialsAndPullOrders;
import cn.amazon.aws.rp.spapi.utils.GlobalThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class SpapiApplication {

    private static final Logger logger = LoggerFactory.getLogger(SpapiApplication.class);

    public static void main(String[] args) {

        logger.info("Spring boot start...");
        SpringApplication.run(SpapiApplication.class, args);
        new GlobalThreadPool();
        GlobalThreadPool.SCHEDULED_POOL.execute(new GetAllSellerCredentialsAndPullFinances(null));
        GlobalThreadPool.SCHEDULED_POOL.execute(new GetAllSellerCredentialsAndPullOrders());
    }
}
