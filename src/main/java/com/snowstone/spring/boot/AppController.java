package com.snowstone.spring.boot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.snowstone.spring.boot.mq.Comsumer;
import com.snowstone.spring.boot.mq.GoodVo;
import com.snowstone.spring.boot.service.GoodService;

@Controller
public class AppController {

	@Autowired
	RedisUtil redisUtil;
	@Autowired
	GoodService goodService;
	@RequestMapping("/redisTest")
	@ResponseBody
	public String redisTest(HttpServletRequest request) {
		redisUtil.set("11", "上海");
		
		Map<String,String> mm=new HashMap<>();
		mm.put("1", "aa");
		mm.put("2", "bb");
		redisUtil.hmset("k1", mm);
		Map<String,String> m1=redisUtil.hmget("k1");
		System.out.println(JSON.toJSONString(m1));
		
		
		redisUtil.lPush("ll", JSON.toJSONString(mm));
		List<Object> ret=redisUtil.lGet("ll", 0L, -1L);
		
		return JSON.toJSONString(ret);
	}
	
	@RequestMapping("/updateGood")
	@ResponseBody
	public boolean updateGood() {
		
		return goodService.buy("bike","1");
	}
	
	@RequestMapping("/testbuy")
	@ResponseBody
	public String testbuy() {
		
		 goodService.benchMark();
		 return "ok!";
	}
	@Autowired
	Comsumer comsumer;
	@RequestMapping("/cumser")
	@ResponseBody
	public String cumser() throws JMSException {
		
		comsumer.getMessage("foo");
		
		 return "ok";
	}
}
