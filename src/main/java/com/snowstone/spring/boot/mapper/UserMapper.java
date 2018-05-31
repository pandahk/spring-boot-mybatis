package com.snowstone.spring.boot.mapper;

import com.snowstone.spring.boot.model.User;

public interface UserMapper  {
	int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}