package cn.amazon.aws.rp.spapi;

import cn.amazon.aws.rp.spapi.tasks.finances.GetAllSellerCredentialsAndPullFinances;
import cn.amazon.aws.rp.spapi.tasks.order.GetAllSellerCredentialsAndPullOrders;
import cn.amazon.aws.rp.spapi.utils.GlobalThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpapiApplication.class, args);
		GlobalThreadPool.SCHEDULED_POOL.execute(new GetAllSellerCredentialsAndPullFinances(null));
		GlobalThreadPool.SCHEDULED_POOL.execute(new GetAllSellerCredentialsAndPullOrders());
	}

}
