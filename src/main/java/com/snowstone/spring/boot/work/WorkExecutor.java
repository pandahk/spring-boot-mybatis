package com.snowstone.spring.boot.work;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Consumer;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.snowstone.spring.boot.RedisUtil;
import com.snowstone.spring.boot.mapper.WorkerMapper;
import com.snowstone.spring.boot.model.Worker;
import com.snowstone.spring.boot.model.Worker.WorkStatus;

import cn.jszhan.commons.kern.apiext.redis.RedisClient;


@Component
public class WorkExecutor {

    private static final int workCount = 30;
    private static AtomicInteger remainderCount = new AtomicInteger(workCount);
    private static final String REDIS_KEY = "WORK:ASYN:WROKQUEUE";
    ExecutorService executor = Executors.newFixedThreadPool(workCount);
	 private Worker worker;
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
    public <T extends MustCompleteWorker> void  submit(String key,String param,Class<T> clazz){
        if(context.getBean(clazz) == null) {
            throw new RuntimeException("no Corresponding bean for current class : " + clazz.getClass().getName());
        }
		Worker worker = new Worker();
		worker.setWorkKey(key);
		worker.setParam(param);
		worker.setClassName(clazz.getName());
		worker.setStatus(Worker.WorkStatus.INIT);
//		final Worker finalworker = persistent(worker);
		scheduleCenter.schedule.submit(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000 * 10);
				} catch (InterruptedException e) {
				}
				toWorkQueue(worker);
			}
		});
    }
    /**
     * 任务写入缓存
     * @param worker
     */
    public void toWorkQueue(Worker worker) {
        RedisClient.lpushObjByJson(REDIS_KEY,worker,null);
    }

    /**
     * 任务信息持久化
     * @param worker
     * @return 
     */
    private int persistent(Worker worker) {
        return workerMapper.insertSelective(worker);
    }
    private class Dispatcher implements Runnable {


        private Long lengthOfQueue() {
            Long len = RedisClient.llen(REDIS_KEY);
            if(new Long(0L).equals(len)) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
            }
            return len;
        }

        @Override
        public void run() {
        	 while (true) {
                 try{
                     while (remainderCount.get() > 0 && lengthOfQueue() > 0) {
                         Worker worker = RedisClient.rpopObjByJson(REDIS_KEY,Worker.class);
                         executor.submit(new Consumer(worker));
                         remainderCount.decrementAndGet();
                     }
                     
                 }catch (Exception e) {
                     try {
                         Thread.sleep(5*1000);
                     } catch (InterruptedException e1) {
                     }
                 }

             }
        }
    }
    
     class Consumer implements Callable {
   	 private Worker worker;
   	 @Autowired
   	 private WorkerMapper workerMapper;
        public Consumer(Worker worker) {
            this.worker = worker;
        }

   		@Override
   		public Object call() throws Exception {
   			try {
   				worker = workerMapper.findOne(worker.getId());
   				if (worker == null) {
   					return "";
   				}
   				if (WorkStatus.INIT.equals(worker.getStatus())) {
   					runWork(worker);
   					worker.setStatus(Worker.WorkStatus.COMPLETE);
   				}
   			} catch (Exception e) {
   				try {
   					worker = workerMapper.findOne(worker.getId());
   					if (WorkStatus.INIT.equals(worker.getStatus())) {
   						worker.setStatus(Worker.WorkStatus.ERROR);
   					}
   				} catch (Exception e1) {
   				}
   			} finally {
   				remainderCount.incrementAndGet();
   			}
   			updateStatus(worker);
   			return "";
   		}
   		 private void runWork(Worker worker) throws ClassNotFoundException {
   		    	MustCompleteWorker workerService = (MustCompleteWorker) context.getBean(Class.forName(worker.getClassName()));
   		        String key = worker.getWorkKey();
   		        String param = worker.getParam();
   		        if(workerService.needWork(key)){
   		            workerService.work(key,param);
   		        }
   		    }
   		 private void updateStatus(Worker worker){
//   			 workerMapper.save(worker);
   		    }
   }
    
    
    
}
