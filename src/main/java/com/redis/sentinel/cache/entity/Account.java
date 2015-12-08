package com.redis.sentinel.cache.entity;

import java.io.Serializable;

public class Account implements Serializable {
	/**
	 * TODO javadoc for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
    private int age ;
	private int phoneNumber;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(int phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	// @Override
	// public String toString() {
	// return this.name + this.id;
	// }
}
