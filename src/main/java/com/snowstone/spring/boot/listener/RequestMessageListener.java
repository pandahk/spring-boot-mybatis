package com.snowstone.spring.boot.listener;

import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.pool.PooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Date;
import java.util.List;
import com.kdqkl.mq.service.AbstractMqServer;
/**
 * 任务监听器
 * 负责接收到消息后 发送HTTP请求
 * Created by MJH on 2017/6/1.
 */
@Component
public class RequestMessageListener implements MessageListener {


	private static Logger logger = LoggerFactory.getLogger(RequestMessageListener.class);


    private Queue getRealQueue(Message message) throws JMSException {
        ActiveMQMessage mqMessage = (ActiveMQMessage) message;
        Queue queue = (Queue) mqMessage.getJMSDestination();
        return queue;
    }

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            String messageId = textMessage.getJMSMessageID();
            String mqMessageStr = textMessage.getText();
            Queue destination = getRealQueue(message);
            String queueName = destination.getQueueName();
           System.out.println("999999====="+mqMessageStr+"||queueName="+queueName);
           message.acknowledge();
        } catch (Exception e) {
            logger.error("onMessage error", e);
            throw new RuntimeException("onMessage error",e);
        }
    }

}
