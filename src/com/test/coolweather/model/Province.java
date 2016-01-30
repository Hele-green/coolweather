package com.test.coolweather.model;

public class Province {
	private int id;
	private String provincename;
	private String provincecode;
	public int getId(){
		return id;
	}
	
	public void segtId(int id){
		this.id=id;
	}
	
	public String getProvincename(){
		return provincename;
	}
	
	public void setProvincename(String provincename){
		this.provincename=provincename;
	}
	
	public String getProvincecode(){
		return provincecode;
	}
	
	public void setProvincecode(String provincecode){
		this.provincecode=provincecode;
	}
}
