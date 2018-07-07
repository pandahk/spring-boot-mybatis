package com.snowstone.spring.boot.work;

public class Order {

	String ordderNo;
	String amount;
	int status;
	public String getOrdderNo() {
		return ordderNo;
	}
	public void setOrdderNo(String ordderNo) {
		this.ordderNo = ordderNo;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "Order [ordderNo=" + ordderNo + ", amount=" + amount + ", status=" + status + "]";
	}
	
	
	
	
	
	
	
}
