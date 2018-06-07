package com.snowstone.spring.boot;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.jms.Connection;
import javax.sql.DataSource;

import org.apache.activemq.jms.pool.PooledConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kdqkl.mq.bo.MqBo;
import com.kdqkl.mq.context.ActiveMQPoolsUtil;
import com.kdqkl.mq.pojo.MqMessage;
import com.kdqkl.mq.service.MqClient;
import com.kdqkl.mq.service.MqComsumer;
import com.snowstone.spring.boot.RedisUtil;
import com.snowstone.spring.boot.listener.RequestMessageListener;
import com.snowstone.spring.boot.mapper.UserMapper;
import com.snowstone.spring.boot.model.User;

@Controller
public class BootController {

	@Autowired
	UserMapper userMapper;
	@Autowired
	RedisUtil redisUtil;
	@Autowired
	DataSource dataSource;
	@Autowired
	RequestMessageListener requestMessageListener;
	@RequestMapping("/find")
	@ResponseBody
	public User find() {
		try {
			System.out.println(dataSource.getConnection().getMetaData().getURL());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		redisUtil.set("kkpp", "7777");
		return userMapper.selectByPrimaryKey(2);
	}
	
	
	// mq地址
    private static String brokerURL="tcp://116.62.117.88:61616";
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
    private static Integer isCreateTable = 1;
    Connection connection;
    //生产者
	@RequestMapping("/mq1")
	@ResponseBody
	public void mq1() {
		try {
			MqClient  mc=new MqClient(brokerURL, dataSource, queueTableName,
					queueName, queueMaxCount, isCreateTable);
			 
			 mc.start();
			 MqMessage mq=new MqMessage();
			 mq.setId(12);
			 mq.setMessage("msg msg msg222");
			 mq.setQueueName("foo.bar");
			 mq.setMessageStatus(1);
			 mq.setRetryTimes(2);
			 mq.setSendTime(new Date().getTime());
			 mq.setTableName("mqmessage");
			 mq.setVersion(12);
			 mc.sendMessage(mq);
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("---------ok mq1");
	}
	//消费者
	@RequestMapping("/mq2")
	@ResponseBody
	public void mq2() {
		MqBo mb=new MqBo();
		mb.setListener(requestMessageListener);
		mb.setQueueName(queueName);
		MqComsumer t=new MqComsumer(brokerURL);
		t.start();
		t.register(mb);
		System.out.println("---------ok mq2");
	}
}
