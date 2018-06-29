package com.snowstone.spring.boot.work;

import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ScheduleCenter {

	public ExecutorService schedule = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1);

	public ExecutorService upLoadSchedule = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1);
	public ExecutorService repareSchedule = Executors.newFixedThreadPool(3);

}
