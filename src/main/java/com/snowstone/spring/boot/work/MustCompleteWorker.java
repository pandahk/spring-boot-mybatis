package com.snowstone.spring.boot.work;

public interface MustCompleteWorker {
	/**
     * 校验从业务角度，是否需要运行
     * 例如重复调用的时候要做校验
     * @param key
     */
    boolean needWork(String key);

    /**
     * @param param 执行任务的参数
     */
    void work(String key,String param);
}
