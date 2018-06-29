package com.snowstone.spring.boot.model;

import java.io.Serializable;

public class Worker implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3790589303595947335L;
	private Integer id;
    private String workKey;
    private String param;
    private WorkStatus status;
    private String className;

    public String getWorkKey() {
        return workKey;
    }

    public void setWorkKey(String workKey) {
        this.workKey = workKey;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public WorkStatus getStatus() {
        return status;
    }

    public void setStatus(WorkStatus status) {
        this.status = status;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "Worker{" +
                "workKey='" + workKey + '\'' +
                ", param='" + param + '\'' +
                ", status=" + status +
                ", className='" + className + '\'' +
                '}';
    }

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public enum WorkStatus {
        INIT,
        ERROR,
        COMPLETE,
        ;
    }
}
