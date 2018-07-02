package com.snowstone.spring.boot.service;

import java.util.concurrent.CountDownLatch;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.snowstone.spring.boot.RedisUtil;
import com.snowstone.spring.boot.mapper.BikeRecordMapper;
import com.snowstone.spring.boot.mapper.GoodMapper;
import com.snowstone.spring.boot.model.BikeRecord;
import com.snowstone.spring.boot.mq.GoodVo;
import com.snowstone.spring.boot.mq.Producter;

import cn.jszhan.commons.kern.apiext.redis.RedisClient;

@Service
public class GoodService {
	@Autowired
	GoodMapper goodMapper;
	@Autowired
	BikeRecordMapper bikeRecordMapper;
	@Autowired
	Producter producter;
	@PostConstruct
	public void init() {

		// 令牌token
		for (int i = 0; i < 2; i++) {

			redisUtil.lPush("token_list", "" + i);
		}
	}

	@Autowired
	RedisUtil redisUtil;
	@Transactional(rollbackFor = Throwable.class)
	public boolean buy2(String goodCode, String userId) {
		try {
			int r1 = goodMapper.update(1);
			if (r1 != 1) {
				return false;
			}
			BikeRecord br = new BikeRecord();
			br.setGoodCode(goodCode);
			br.setUserId(userId);
			int r2 = bikeRecordMapper.insertSelective(br);
			if (r2 != 1) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	@Transactional(rollbackFor = Throwable.class)
	public boolean buy(String goodCode, String userId) {
		// 更新商品数量，减一

		try {
//			int r1 = goodMapper.update(1);
//			if (r1 != 1) {
//				return false;
//			}
//			BikeRecord br = new BikeRecord();
//			br.setGoodCode(goodCode);
//			br.setUserId(userId);
//			int r2 = bikeRecordMapper.insertSelective(br);
//			if (r2 != 1) {
//				return false;
//			}
			//mq
			GoodVo vo=new GoodVo();
			vo.setGoodCode(goodCode);
			vo.setUserId(userId);
			producter.sendMessage("foo", vo);
			System.out.println("发送mq消息成功!"+JSON.toJSONString(vo));
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	String good_code = "bike";

	int thread_num = 2;

	CountDownLatch cdl = new CountDownLatch(thread_num);

	public void benchMark() {

		Thread[] ths = new Thread[thread_num];

		for (int i = 0; i < ths.length; i++) {
			String userId = "tony_" + i;
			Thread th = new Thread(new UserRequest(userId));
			ths[i] = th;
			th.start();

			cdl.countDown();
		}
		for (Thread thread : ths) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	class UserRequest implements Runnable {

		String userId;

		public UserRequest(String userId) {
			this.userId = userId;
		}

		@Override
		public void run() {
			try {
				cdl.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Object obj = redisUtil.lpop("token_list");
			if (obj == null) {
				System.out.println("令牌获取失败!用户id为:" + userId);
				return;
			}
			System.out.println("令牌获取成功!用户id为:" + userId);
			boolean ret = buy("bike", userId);
			System.out.println("秒杀结果:" + ret);
		}

	}
}
