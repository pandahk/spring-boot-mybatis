package com.snowstone.spring.boot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.snowstone.spring.boot.mapper.UserMapper;
import com.snowstone.spring.boot.model.User;

@Service
public class UserService {

	@Autowired
	UserMapper userMapper;
	
	@Transactional(isolation = Isolation.REPEATABLE_READ)
    public void insertData() {
        User user=new User();
        user.setAge(23);
        user.setName("zhangsan");
        userMapper.insertSelective(user);
      
        System.out.println("---------1");
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void readRange() {
        System.out.println("11:"+userMapper.selectuser(18).size());
        try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        System.out.println("22:"+userMapper.selectuser(18).size());
    }
	
	
	
	
	
}
