/*------------------------------------------------------------------------------
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package com.redis.sentinel.cache;

public class Test {
    public static void main(String[] args) {
        System.out.println(CacheKeyPrefix.CONTRACT_ADDRESS.key("123", "234"));
        System.out.println(CacheKeyPrefix.CUSTOMER.searchIndex("phone", "15975383197"));
    }
}
