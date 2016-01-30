package com.test.coolweather.model;

public class County {
	private int id;
	private String countyname;
	private String contycode;
	private int cityId;
	public int getId(){
		return id;
	}
	
	public void segtId(int id){
		this.id=id;
	}
	
	public String getCountyname(){
		return countyname;
	}
	
	public void setCountyname(String countyname){
		this.countyname=countyname;
	}
	
	public String getCountycode(){
		return contycode;
	}
	
	public void setCountycode(String contycode){
		this.contycode=contycode;
	}
	
	public int getCityId(){
		return cityId;
	}
	
	public void setCityId(int cityId){
		this.cityId=cityId;
	}
}
