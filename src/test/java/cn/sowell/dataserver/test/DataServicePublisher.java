package cn.sowell.dataserver.test;

import javax.xml.ws.Endpoint;

public class DataServicePublisher {
	public static void main(String[] args) {
		String address = "http://localhost:9010/HelloWorld";
		Endpoint.publish(address, new HelloWorldImpl());
		System.out.println("server start ...");
		
	}
}
