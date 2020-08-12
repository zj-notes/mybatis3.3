package com.mybatis.demo.service.impl;

import com.mybatis.demo.service.CalculateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service("calculateService")
public class CalculateServiceImpl implements CalculateService {

	// spring这种也可直接注入 ApplicationContext 对象
	@Autowired
	private ApplicationContext applicationContext ;

	@Override
	public String test(){
		System.out.println(applicationContext.toString());
		return applicationContext.toString();
	}
}