package com.snowstone.spring.boot;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.snowstone.spring.boot.listener.RequestMessageListener;
import com.snowstone.spring.boot.mapper.UserMapper;
import com.snowstone.spring.boot.mapper.WorkerMapper;
import com.snowstone.spring.boot.model.User;
import com.snowstone.spring.boot.work.Apply;
import com.snowstone.spring.boot.work.Order;
import com.snowstone.spring.boot.work.WorkExecutor;

import cn.jszhan.commons.kern.apiext.redis.RedisClient;
import redis.clients.jedis.Jedis;

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
	
//	@Autowired
//	MqService mqService;
	
	@Autowired
	private WorkExecutor workExecutor;
	
	@Autowired
	WorkerMapper workerMapper;
	
	/**
	 * 限制ip防刷测试
	 * @param request
	 * @return
	 */
	@RequestMapping("/xx")
	@ResponseBody
	public String xx(HttpServletRequest request) {
		//获得真实ip
		String ip = getIpAddr(request);
		//获取当前用户
		String userId = "12";
		//计数key
		String key = "risk:"+userId+":"+ip;
		//redis记录每次请求，自增加1
		Jedis jedis=RedisClient.getConnection();
		jedis.incr(key);
		
		//设置24小时过期（ps：时间可以作为配置参数，写灵活点)
		jedis.expire(key, 10);
		jedis.close();
		//获取限制次数
		String value = jedis.get(key);
		int limitCount = 5;
		//判断key是否存在
		if(limitCount > 0 && jedis.exists(key)){
		    value = StringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
		    if(StringUtils.isNotBlank(value)){
		        //判断是否达到限制次数
		        if(Integer.valueOf(value) > limitCount){
		            return "你的请求超过最大限制,请注意！！";
		        }
		    }
		}
		return "000000000";
		
		
		
	}
	/**
	 * 限制单个用户 10秒内只能请求一次
	 * @param request
	 * @return
	 */
	@RequestMapping("/rr")
	@ResponseBody
	public String limit() {
		
		Jedis jedis=RedisClient.getConnection();
		String ret = null;
		try {
			ret = jedis.set("12", "2", "NX", "EX", 10);
			if (!"OK".equals(ret)) {
				return "你操作太频繁，用户12";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			RedisClient.returnResource(jedis);
		}
		return ret;
	}
	@RequestMapping("/ss")
	@ResponseBody
	public void apply() {
//		Worker w=workerMapper.selectByPrimaryKey(71);
//		System.out.println(w.toString());
//		return w;
//		RedisClient.set(REDIS_KEY, "mkmkmk");
//		RedisClient.lpushObjByJson("WORK:ASYN:WORKQUEUE", Apply.class, null);
		Order order=new Order();
		order.setOrdderNo("order_no_001");
		order.setAmount("600.0");
		order.setStatus(1);
		for (int i = 0; i < 200; i++) {
			workExecutor.submit("order_no_00"+i, JSON.toJSONString(order), Apply.class);
		}
		
//		RedisClient.lpushObjByJson("WORK:ASYN:xx", Apply.class, null);
	}
	@RequestMapping("/map")
	@ResponseBody
	public Map map() {
		LinkedHashMap< String, String> link=new LinkedHashMap<>();
		link.put("12", "500");
		link.put("33", "100");
		link.put("11", "300");
		return link;
	}
	
	
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
    private static Integer isCreateTable = 0;
    Connection connection;
    
    
    
    
//    @RequestMapping("/mq")
//	@ResponseBody
//	public void mq() {
//    	mqService.p();
//    }
    
    
    //生产者
//	@RequestMapping("/mq1")
//	@ResponseBody
//	public void mq1() {
//		try {
//			MqClient  mc=new MqClient(brokerURL, dataSource, queueTableName,
//					queueName, queueMaxCount, isCreateTable);
//			 
//			 mc.start();
//			 MqMessage mq=new MqMessage();
//			 mq.setMessage("msg msg msg222");
//			 mq.setQueueName("foo.bar");
//			 mq.setSendTime(new Date().getTime());
//			 mq.setTableName("mqmessage");
//			 mc.sendMessage(mq);
//			 
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		
//		System.out.println("---------ok mq1");
//	}
//	//消费者
//	@RequestMapping("/mq2")
//	@ResponseBody
//	public void mq2() {
//		MqBo mb=new MqBo();
//		mb.setListener(requestMessageListener);
//		mb.setQueueName(queueName);
//		MqComsumer t=new MqComsumer(brokerURL);
//		t.start();
//		t.register(mb);
//		System.out.println("---------ok mq2");
//	}
//	
	public static String getIpAddr(HttpServletRequest request){
        String ipAddress = request.getHeader("x-forwarded-for");
            if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){
                    //根据网卡取本机配置的IP
                    InetAddress inet=null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress= inet.getHostAddress();
                }
            }
            //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15
                if(ipAddress.indexOf(",")>0){
                    ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));
                }
            }
            return ipAddress; 
    }
}
