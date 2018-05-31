package com.snowstone.spring.boot;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.snowstone.spring.boot.mapper.UserMapper;
import com.snowstone.spring.boot.model.User;

@Controller
public class BootController {

	@Autowired
	UserMapper userMapper;
	@Autowired
	RedisUtil redisUtil;
	
	@RequestMapping("/find")
	@ResponseBody
	public User find() {
		redisUtil.set("kkpp", "7777");
		return userMapper.selectByPrimaryKey(2);
	}
	
	
}
