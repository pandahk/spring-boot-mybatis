package com.snowstone.spring.boot.test;

import java.util.Date;

import javax.sql.DataSource;

import com.kdqkl.mq.pojo.MqMessage;
import com.kdqkl.mq.service.MqClient;

public class Producter {

	// mq地址
    private static String brokerURL;
    // 数据模板
    private static DataSource dataSource;
    // 队列名称
    private static String queueName="foo";
    // 消息
    private static String mqMessage="msg msg";
    // 创建存放消息的表名
    private static String queueTableName="mqmessage";
    // 队列默认大小
    private static Integer queueMaxCount = 50000;
    // 是否创建表
    private static Integer isCreateTable = 1;
	public static void main(String[] args) {
		try {
			MqClient  mc=new MqClient(brokerURL, dataSource, queueTableName,
					queueName, queueMaxCount, isCreateTable);
			 
			 mc.start();
			 MqMessage mq=new MqMessage();
			 mq.setId(12);
			 mq.setMessage("msg msg msg");
			 mq.setQueueName("foor");
			 mq.setMessageStatus(1);
			 mq.setRetryTimes(0);
			 mq.setSendTime(new Date().getTime());
			 mq.setTableName("mqmessage");
			 mq.setVersion(12);
			 mc.sendMessage(mq);
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
	}
	
	
	
	
	
	
}
