package com.snowstone.spring.boot.test;


import javax.jms.Session;

import org.apache.activemq.pool.PooledConnection;

import com.kdqkl.mq.context.ActiveMQPoolsUtil;
import com.kdqkl.mq.service.AbstractMqServer;

public class MqComsumer extends AbstractMqServer {
	PooledConnection connection;
	Session session;
	
	
	public MqComsumer(String brokerURL, String queue) {
		super(brokerURL, queue);
	}

//	public MqComsumer(PooledConnection connection, String queue, Session session) {
//		super(connection, queue, session);
//	}


	@Override
	public void onMessage () {
		connection=ActiveMQPoolsUtil.getConnection();
		session=ActiveMQPoolsUtil.getSession();
		new Thread(new MessageHandler(connection,queue,session)).start();
	}
	
	
//	public static void main(String[] args) {
//		String brokerURL="tcp://116.62.117.88:61616";
//		
//		MqComsumer t=new MqComsumer(brokerURL);
//		t.start();
//		t.onMessage();
//		
//	}

}
