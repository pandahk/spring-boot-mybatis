package com.snowstone.spring.boot.mapper;

import com.snowstone.spring.boot.model.BikeRecord;


public interface BikeRecordMapper  {
	
	int deleteByPrimaryKey(Integer id);

    int insert(BikeRecord record);

    int insertSelective(BikeRecord record);

    BikeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BikeRecord record);

    int updateByPrimaryKey(BikeRecord record);
	
	
	
	
	
}