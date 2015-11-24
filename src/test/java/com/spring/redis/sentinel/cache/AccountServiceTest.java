///*------------------------------------------------------------------------------
// * COPYRIGHT Ericsson 2015
// *
// * The copyright to the computer program(s) herein is the property of
// * Ericsson Inc. The programs may be used and/or copied only with written
// * permission from Ericsson Inc. or in accordance with the terms and
// * conditions stipulated in the agreement/contract under which the
// * program(s) have been supplied.
// *----------------------------------------------------------------------------*/
//package com.spring.redis.sentinel.cache;
//
//import static org.junit.Assert.assertNotNull;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//import com.spring.redis.sentinel.cache.service.test.AccountService;
//
//public class AccountServiceTest {
//
//	private AccountService accountService;
//
//	private final Logger logger = LoggerFactory
//			.getLogger(AccountServiceTest.class);
//
//	@Before
//	public void setUp() throws Exception {
//		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
//				"spring-config.xml");
//		accountService = context
//				.getBean("accountService", AccountService.class);
//	}
//
//	@Test
//	public void testInject() {
//		assertNotNull(accountService);
//	}
//
//	@Test
//	public void testGetAccountByName() throws Exception {
//		System.out.println("first query...");
//		logger.info("first query...");
//		accountService.getAccountByName("accountName");
//		System.out.println("second query...");
//		logger.info("second query...");
//		accountService.getAccountByName("accountName");
//	}
// }
