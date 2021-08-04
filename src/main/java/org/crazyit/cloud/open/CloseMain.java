package org.crazyit.cloud.open;

import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandMetrics.HealthCounts;
import com.netflix.hystrix.HystrixCommandProperties;

public class CloseMain {

	public static void main(String[] args) throws Exception {
		// 10秒内大于3次请求，满足第一个条件
		ConfigurationManager
				.getConfigInstance()
				.setProperty(
						"hystrix.command.default.circuitBreaker.requestVolumeThreshold",
						3);
		boolean isTimeout = true;
		for(int i = 0; i < 20; i++) {
			TestCommand c = new TestCommand(isTimeout);
			c.execute();
			HealthCounts hc = c.getMetrics().getHealthCounts();
			System.out.println("断路器状态：" + c.isCircuitBreakerOpen() + ", 请求数量：" + hc.getTotalRequests());
			if(c.isCircuitBreakerOpen()) {
				System.out.println("============  断路器打开了，等待休眠期结束");
				Thread.sleep(1000);
				if(i>7) {
					isTimeout = false;
				}
			}
		}
	}

	static class TestCommand extends HystrixCommand<String> {
		
		private boolean isTimeout;
		
		public TestCommand(boolean isTimeout) {
			super(Setter.withGroupKey(
					HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
					.andCommandPropertiesDefaults(
							HystrixCommandProperties.Setter()
									.withExecutionTimeoutInMilliseconds(500)));
			this.isTimeout = isTimeout;
		}

		@Override
		protected String run() throws Exception {
			if(isTimeout) {
				Thread.sleep(800);
			} else {
				Thread.sleep(200);
			}			
			return "";
		}

		@Override
		protected String getFallback() {
			return "fallback";
		}
	}
}
