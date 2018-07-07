package com.snowstone.spring.boot.work;

import java.io.IOException;
import java.io.Serializable;

import org.apache.ibatis.javassist.bytecode.stackmap.TypeData.ClassName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.snowstone.spring.boot.model.User;



@Component
public class Apply implements MustCompleteWorker , Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5635810537574787015L;
	@Autowired
	private WorkExecutor workExecutor;
	
	
	public void apply() {
		workExecutor.submit("m1", "zs", Apply.class);
	}
	
	@Override
	public boolean needWork(String key) {
		return true;
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public void work(String key, String param) {
//		workExecutor.submit("m1", "zs", Apply.class);
		
		Order order=JSON.parseObject(param, Order.class);
		//账户表  扣减
		//保存
		System.out.println(order.toString());
	}
	
	
	
	
	
	
}
