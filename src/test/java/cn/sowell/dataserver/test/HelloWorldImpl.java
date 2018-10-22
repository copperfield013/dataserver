package cn.sowell.dataserver.test;

import javax.jws.WebService;

@WebService(endpointInterface="cn.sowell.dataserver.test.HelloWorld",serviceName="HelloWorld")
public class HelloWorldImpl implements HelloWorld {
	@Override
	public String sayHello() {
		return "Hello ";
	}
}
