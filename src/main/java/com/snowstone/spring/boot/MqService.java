package com.snowstone.spring.boot;

import java.util.Date;

import javax.jms.Connection;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdqkl.mq.pojo.MqMessage;
import com.kdqkl.mq.service.MqClient;

@Service
public class MqService {
	@Autowired
	DataSource dataSource;
	
	// mq地址
    private static String brokerURL="tcp://116.62.117.88:61616";
//    String brokerURL = "tcp://116.62.117.88:61616?" +   
//            "jms.optimizeAcknowledge=true" +   
//            "&jms.optimizeAcknowledgeTimeOut=30000" +   
//            "&jms.redeliveryPolicy.maximumRedeliveries=6";
    // 数据模板
    private static DataSource dataSource1;
    // 队列名称
    private static String queueName="foo.bar";
    // 消息
    private static String mqMessage="msg msg22";
    // 创建存放消息的表名
    private static String queueTableName="mqmessage";
    // 队列默认大小
    private static Integer queueMaxCount = 500;
    // 是否创建表
    private static Integer isCreateTable = 0;
    Connection connection;
	
    @Transactional
	public void p(){
		MqClient mc;
		try {
			mc = new MqClient(brokerURL, dataSource, queueTableName,
					queueName, queueMaxCount, isCreateTable);
		
		 
		 mc.start();
		 MqMessage mq=new MqMessage();
		 mq.setMessage("msg msg msg222");
		 mq.setQueueName("foo.bar");
		 mq.setSendTime(new Date().getTime());
		 mq.setTableName("mqmessage");
		 mc.sendMessage(mq);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
}
