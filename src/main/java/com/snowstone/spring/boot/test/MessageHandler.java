package com.snowstone.spring.boot.test;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.jms.pool.PooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 消息处理，负责监听队列，设置异步监听器 Created by MJH on 2017/6/2.
 */
public class MessageHandler implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(MessageHandler.class);

	private PooledConnection connection = null;

	private String queue = null;

	

//	private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
//
//	private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
//
//	private static final String BROKEN_URL = "tcp://116.62.117.88:61616";
//
//	ConnectionFactory connectionFactory;
//
	Session session;
	public MessageHandler(PooledConnection connection, String queue,Session session) {
		this.connection = connection;
		this.queue = queue;
		this.session=session;
	}
	
	@Override
    public void run() {
        try {
//        	connectionFactory = new ActiveMQConnectionFactory(USERNAME,PASSWORD,BROKEN_URL);
//   			connection  = connectionFactory.createConnection();
//            connection.start(); // 启动连接
//            Session session = connection.createSession(Boolean.FALSE, Session.CLIENT_ACKNOWLEDGE);
            Destination dest = session.createQueue(queue);
            MessageConsumer messageConsumer = session.createConsumer(dest);
            messageConsumer.setMessageListener(new RequestMessageListener());

        } catch (JMSException e) {
            logger.error("messageHandler error",e);
        }

      }

}