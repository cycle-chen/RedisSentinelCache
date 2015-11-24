/*------------------------------------------------------------------------------
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package com.spring.redis.sentinel.cache.service.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.spring.redis.sentinel.cache.entity.Account;

@Service
public class AccountService {
	private final Logger logger = LoggerFactory.getLogger(AccountService.class);

	// 使用了一个缓存名叫 accountCache
	@Cacheable(value = "accountCache", key = "'#id' + 'getAccountByName'")
	public Account getAccountByName(String id) {
		System.out.println("real querying account... {}" + id);
		Account account = new Account("ycc");
		return account;
	}

	private Optional<Account> getFromDB(String accountName) {
		logger.info("real querying db... {}", accountName);
		// Todo query data from database
		return Optional.fromNullable(new Account(accountName));
	}
}
