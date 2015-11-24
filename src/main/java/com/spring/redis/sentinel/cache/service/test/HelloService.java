//package com.spring.redis.sentinel.cache.service.test;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//@Service("helloService")
//public class HelloService {
//	@Autowired
//	private RedisTemplate<String, Object> redisTemplate;
//
//	public String sayHello() {
//		return "test!";
//	}
//
//	@Cacheable(value = "cache1", condition = "#test.equals(#test)", key = "test")
//	public String sayHello(String test) {
//		System.out.println("that was not int the cache...,query from db");
//		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//		Date date = new Date();
//		// DefaultRedisSet<Object> set = new DefaultRedisSet<Object>("test",
//		// redisTemplate);
//		// set.add(test);
//		return "test! " + dateFormat.format(date);
//	}
// }
