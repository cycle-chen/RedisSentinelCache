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
    CONTRACT("contract"), ONTRACT_CUSTOMER("contract::customer"), CUSTOMER("customer"), CUSTOMER_CUSTOMER("customer"), CUSTOMER_BILLINGACCOUNT(
            "customer::billingaccount"), CONTRACT_BILLINGACCOUNT("contract::billingaccount"), BILLINGACCOUNT(
            "billingaccount"), BILLINGACCOUNT_BILLINGADDRESS("billingaccount::billingaddress"), BILLINGADDRESS(
            "billingaddress"), CONTRACT_ADDRESS("contract::address"), ADDRESS("address"), CONTRACT_PARTYIDENTIFICATION(
            "contract::partyidentification"), PARTYIDENTIFICATION("partyidentification");
    private String prefix;

    /**
     * get the key of the store
     * 
     * @param keys
     * @return
     */
    public String key(String... keys) {
        if ((null == keys) || (keys.length == 0)) {
            return null;
        }
        String keyPrefix = this.getPrefix();
        for (String key : keys) {
            keyPrefix += "::" + key;
        }
        return keyPrefix;
    }

    /**
     * get the key of index
     *
     * @param conditionName
     * @param conditionValue
     * @return
     */
    public String searchIndex(String conditionName, String conditionValue) {
        String keyPrefix = this.getPrefix();
        if ((null == conditionName) || "".equals(conditionName)) {
            return null;
        }
        if ((null == conditionValue) || "".equals(conditionValue)) {
            return null;
        }
        return keyPrefix + "::" + conditionName + "::" + conditionValue;
    }

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
