package com.test.coolweather.util;

import android.text.TextUtils;

import com.test.coolweather.model.City;
import com.test.coolweather.model.CoolWeatherDB;
import com.test.coolweather.model.County;
import com.test.coolweather.model.Province;

public class Utility {
	/*
	 * 解析和处理服务器返回的省级数据
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB
			coolweatherdb,String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces=response.split(",");
			if(allProvinces !=null && allProvinces.length>0){
				for(String p : allProvinces){
					String[] array=p.split("\\|");
					Province province=new Province();
					province.setProvincecode(array[0]);
					province.setProvincename(array[1]);
					//将解析出来的数据存储到Province表
					coolweatherdb.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/*
	 * 解析和处理服务器返回的市级数据
	 */
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB
			coolweatherdb,String response, int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allcity=response.split(",");
			if(allcity !=null && allcity.length>0){
				for(String p : allcity){
					String[] array=p.split("\\|");
					City province=new City();
					province.setCitycode(array[0]);
					province.setCityname(array[1]);
					province.setProvinceId(provinceId);
					//将解析出来的数据存储到City表
					coolweatherdb.saveCity(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/*
	 * 解析和处理服务器返回的县级数据
	 */
	public synchronized static boolean handleCountiesResponse(CoolWeatherDB
			coolweatherdb,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allcounty=response.split(",");
			if(allcounty !=null && allcounty.length>0){
				for(String p : allcounty){
					String[] array=p.split("\\|");
					County province=new County();
					province.setCountycode(array[0]);
					province.setCountyname(array[1]);
					province.setCityId(cityId);
					//将解析出来的数据存储到Province表
					coolweatherdb.saveCounty(province);
				}
				return true;
			}
		}
		return false;
	}
	
}
