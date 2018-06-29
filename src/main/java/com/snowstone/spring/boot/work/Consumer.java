//package com.snowstone.spring.boot.work;
//
//import java.util.concurrent.Callable;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.snowstone.spring.boot.mapper.WorkerMapper;
//import com.snowstone.spring.boot.work.Worker.WorkStatus;
//
//public class Consumer implements Callable {
//	 private Worker worker;
//	 @Autowired
//	 private WorkerMapper workerMapper;
//     public Consumer(Worker worker) {
//         this.worker = worker;
//     }
//
//		@Override
//		public Object call() throws Exception {
//			try {
//				worker = workerMapper.findOne(worker.getId());
//				if (worker == null) {
//					return "";
//				}
//				if (WorkStatus.INIT.equals(worker.getStatus())) {
//					runWork(worker);
//					worker.setStatus(Worker.WorkStatus.COMPLETE);
//				}
//			} catch (Exception e) {
//				try {
//					worker = workerMapper.findOne(worker.getId());
//					if (WorkStatus.INIT.equals(worker.getStatus())) {
//						worker.setStatus(Worker.WorkStatus.ERROR);
//					}
//				} catch (Exception e1) {
//				}
//			} finally {
//				remainderCount.incrementAndGet();
//			}
//			updateStatus(worker);
//			return "";
//		}
//		 private void runWork(Worker worker) throws ClassNotFoundException {
//		    	MustCompleteWorker workerService = (MustCompleteWorker) context.getBean(Class.forName(worker.getClassName()));
//		        String key = worker.getWorkKey();
//		        String param = worker.getParam();
//		        if(workerService.needWork(key)){
//		            workerService.work(key,param);
//		        }
//		    }
//		 private void updateStatus(Worker worker){
////			 workerMapper.save(worker);
//		    }
//}
