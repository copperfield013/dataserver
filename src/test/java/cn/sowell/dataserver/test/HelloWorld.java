package cn.sowell.dataserver.test;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface HelloWorld {
	
	@WebMethod
	public String sayHello();
}
