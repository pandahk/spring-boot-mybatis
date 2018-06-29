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
    private Integer status;//0-开始 1-完成 2-错误
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

   

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Worker [id=" + id + ", workKey=" + workKey + ", param=" + param + ", status=" + status + ", className="
				+ className + "]";
	}

	
}
