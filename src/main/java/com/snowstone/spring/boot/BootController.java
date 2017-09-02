package com.snowstone.spring.boot;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.snowstone.spring.boot.mapper.CountryMapper;
import com.snowstone.spring.boot.model.Country;

@Controller
public class BootController {

	@Autowired
	CountryMapper countryMapper;
	@RequestMapping("/vv")
	@ResponseBody
	public Country vv() {
		return countryMapper.selectByPrimaryKey(130);
	}
	
	
}
