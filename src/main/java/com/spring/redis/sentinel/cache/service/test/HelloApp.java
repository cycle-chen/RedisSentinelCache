package com.spring.redis.sentinel.cache.service.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HelloApp {
	public static void main(String[] args) throws InterruptedException {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		// HelloService helloService = context.getBean(HelloService.class);
		// helloService.sayHello("test");
		// for (int i = 0; i < 400; i++) {
		// Thread.sleep(1000);
		// System.out.println(helloService.sayHello("ozgur"));
		// }
	}
}
