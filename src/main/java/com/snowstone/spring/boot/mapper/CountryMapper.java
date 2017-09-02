package com.snowstone.spring.boot.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.snowstone.spring.boot.model.Country;

public interface CountryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Country record);

    int insertSelective(Country record);

    Country selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Country record);

    int updateByPrimaryKey(Country record);
}