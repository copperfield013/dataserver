package cn.sowell.dataserver.ws.impl;

import javax.jws.WebService;

import cn.sowell.dataserver.ws.DemoService;

@WebService(serviceName="demo", endpointInterface="cn.sowell.dataserver.ws.DemoService")
public class DemoServiceImpl implements DemoService{

	@Override
	public String hello() {
		return "hello";
	}

}
