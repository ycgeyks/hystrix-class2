package org.crazyit.cloud.open;

import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommand.Setter;

public class OpenMain {

	public static void main(String[] args) {
		// 10秒内大于10次请求，满足第一个条件
		ConfigurationManager
				.getConfigInstance()
				.setProperty(
						"hystrix.command.default.circuitBreaker.requestVolumeThreshold",
						10);
		for(int i = 0; i < 15; i++) {
			ErrorCommand c = new ErrorCommand();
			c.execute();
			// 输出断路器状态
			System.out.println(c.isCircuitBreakerOpen());
		}
	}

	static class ErrorCommand extends HystrixCommand<String> {
		public ErrorCommand() {
			super(Setter.withGroupKey(
					HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
					.andCommandPropertiesDefaults(
							HystrixCommandProperties.Setter()
									.withExecutionTimeoutInMilliseconds(500)));
		}

		@Override
		protected String run() throws Exception {
			Thread.sleep(800);
			return "";
		}

		@Override
		protected String getFallback() {
			return "fallback";
		}
		
		
	}
}
