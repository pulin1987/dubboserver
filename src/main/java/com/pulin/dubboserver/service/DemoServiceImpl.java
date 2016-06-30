package com.pulin.dubboserver.service;

public class DemoServiceImpl implements DemoService{

	@Override
	public void test() {
		System.out.println("test:"+System.currentTimeMillis());
	}

}
