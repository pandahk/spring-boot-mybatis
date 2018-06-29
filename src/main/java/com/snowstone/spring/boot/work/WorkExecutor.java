package com.snowstone.spring.boot.work;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Consumer;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.snowstone.spring.boot.RedisUtil;
import com.snowstone.spring.boot.mapper.WorkerMapper;
import com.snowstone.spring.boot.model.Worker;

import cn.jszhan.commons.kern.apiext.auth.BASE64;
import cn.jszhan.commons.kern.apiext.redis.RedisClient;


@Component
public class WorkExecutor {
	private final static Logger logger = LoggerFactory.getLogger(WorkExecutor.class);
	private static final int workCount = 30;
	private static AtomicInteger remainderCount = new AtomicInteger(workCount);
	private static final String REDIS_KEY = "WORK:ASYN:WROKQUEUE";
	ExecutorService executor = Executors.newFixedThreadPool(workCount);
	@Autowired
	private WorkerMapper workerMapper;
	@Autowired
	private ScheduleCenter scheduleCenter;

	@Autowired
	private ApplicationContext context;

	@PostConstruct
	public void init() {
		new Thread(new Dispatcher()).start();
	}

	public <T extends MustCompleteWorker> void submit(String key, String param, Class<T> clazz) {
		if (context.getBean(clazz) == null) {
			throw new RuntimeException("no Corresponding bean for current class : " + clazz.getClass().getName());
		}
		Worker worker = new Worker();
		worker.setWorkKey(key);
		worker.setParam(param);
		worker.setClassName(clazz.getName());
		worker.setStatus(0);
		persistent(worker);
		scheduleCenter.schedule.submit(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000 * 1);
				} catch (InterruptedException e) {
				}
				toWorkQueue(worker);
			}
		});
	}

	/**
	 * 任务写入缓存
	 * 
	 * @param worker
	 */
	public void toWorkQueue(Worker worker) {
		RedisClient.lpushObjByJson(REDIS_KEY, worker, null);
	}

	/**
	 * 任务信息持久化
	 * 
	 * @param worker
	 * @return
	 */
	private int persistent(Worker worker) {
		return workerMapper.insertSelective(worker);
	}

	private class Dispatcher implements Runnable {

		private Long lengthOfQueue() {
			
			Long len = RedisClient.llen(REDIS_KEY);
			if (new Long(0L).equals(len)) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
			}
			return len;
		}
		private void runWork(Worker worker) throws ClassNotFoundException {
			MustCompleteWorker workerService = (MustCompleteWorker) context
					.getBean(Class.forName(worker.getClassName()));
			String key = worker.getWorkKey();
			String param = worker.getParam();
			if (workerService.needWork(key)) {
				workerService.work(key, param);
			}
		}

		private void updateStatus(Worker worker) {
			 workerMapper.updateByPrimaryKeySelective(worker);
		}
		@Override
		public void run() {
			while (true) {
				try {
					while (remainderCount.get() > 0 && lengthOfQueue() > 0) {
						logger.info("---------------start");
						Worker worker = RedisClient.rpopObjByJson(REDIS_KEY, Worker.class);
						System.out.println("11:"+JSON.toJSON(worker));
						executor.submit(new Consumer(worker));
						remainderCount.decrementAndGet();
						logger.info("---------------end");
					}

				} catch (Exception e) {
					try {
						Thread.sleep(5 * 1000);
					} catch (InterruptedException e1) {
					}
				}

			}
		}
	}

	class Consumer implements Callable {
		private Worker worker;
		

		public Consumer(Worker worker) {
			this.worker = worker;
		}

		@Override
		public Object call() throws Exception {
			try {
				logger.info("-----------one");
				System.out.println("22:"+JSON.toJSON(worker));
				worker = workerMapper.selectByPrimaryKey(worker.getId());
				System.out.println("33:"+worker);
				if (worker == null) {
					return "";
				}
				if (worker.getStatus().intValue()==0) {
					runWork(worker);
					worker.setStatus(1);
				}
			} catch (Exception e) {
				try {
					Worker work2  = workerMapper.selectByPrimaryKey(worker.getId());
					if ("0".equals(work2.getStatus())) {
						work2.setStatus(2);
					}
				} catch (Exception e1) {
				}
			} finally {
				remainderCount.incrementAndGet();
			}
			updateStatus(worker);
			logger.info("-----------one--end");
			return "";
		}

		private void runWork(Worker worker) throws ClassNotFoundException {
			MustCompleteWorker workerService = (MustCompleteWorker) context
					.getBean(Class.forName(worker.getClassName()));
			String key = worker.getWorkKey();
			String param = worker.getParam();
			if (workerService.needWork(key)) {
				workerService.work(key, param);
			}
		}

		private void updateStatus(Worker worker) {
			 workerMapper.updateByPrimaryKeySelective(worker);
		}
	}

}
