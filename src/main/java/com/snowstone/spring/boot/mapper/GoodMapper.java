package com.snowstone.spring.boot.mapper;

import com.snowstone.spring.boot.model.Good;
import com.snowstone.spring.boot.model.Good;

import org.springframework.stereotype.Repository;

public interface GoodMapper  {
	int deleteByPrimaryKey(Integer id);

    int insert(Good record);

    int insertSelective(Good record);

    Good selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Good record);

    int updateByPrimaryKey(Good record);
    //add
    int update(Integer id);
}