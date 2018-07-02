package com.sweetstone.spring.boot;

import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.snowstone.spring.boot.service.GoodService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:application.yml")
public class GoodTest {

	@Autowired
	GoodService goodService;
	
	String good_code="bike";
	
	int thread_num=2000;
	
	CountDownLatch cdl=new CountDownLatch(thread_num);
	
	
	long timed=0L;
	@Before
	public void start(){
		System.out.println("开始测试。。。");
	}
	
	@After
	public void end(){
		System.out.println("结束测试。。。");
	}
	
	@Test
	public void benchMark(){
		
		Thread[] ths=new Thread[thread_num];
		
		for (int i = 0; i < ths.length; i++) {
			String userId="tony_"+i;
			Thread th=new Thread(new UserRequest(userId));
			ths[i]=th;
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
	
	class UserRequest	 implements Runnable{

		String userId;
		
		public UserRequest(String userId){
			this.userId=userId;
		}
		
		@Override
		public void run() {
			try {
				cdl.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			goodService.buy();
			
		}
		
		
		
		
		
		
		
		
		
	}
	
}
