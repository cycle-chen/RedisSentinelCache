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

public enum CacheKeyPrefix {
    CONTRACT("contract"), CONTRACT_CUSTOMER("contract::customer"), CUSTOMER("customer"), CUSTOMER_BILLINGACCOUNT(
            "customer::billingaccount"), CONTRACT_BILLINGACCOUNT("contract::billingaccount"), BILLINGACCOUNT(
            "billingaccount"), BILLINGACCOUNT_BILLINGADDRESS("billingaccount::billingaddress"), BILLINGADDRESS(
            "billingaddress"), CONTRACT_ADDRESS("contract::address"), ADDRESS("address"), CONTRACT_PARTYIDENTIFICATION(
            "contract::partyidentification"), PARTYIDENTIFICATION("partyidentification");
    private String prefix;

    public abstract <K> String key(K... k, String... column);

    public abstract <K> String indexKey(K... k);

    private CacheKeyPrefix(String prefix) {
        this.setPrefix(prefix);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
