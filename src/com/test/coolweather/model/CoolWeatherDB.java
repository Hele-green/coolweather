package com.test.coolweather.model;

import java.util.ArrayList;
import java.util.List;

import com.test.coolweather.db.CoolWeatherOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	public static final String DB_NAME="cool_weather";   //数据库名
	public static final int VERSION=1; //数据库版本
	private static CoolWeatherDB coolweatherdb;
	private SQLiteDatabase db;
	
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper dbHelper=new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
		db=dbHelper.getWritableDatabase();
	}
	
	public synchronized static CoolWeatherDB getInstance(Context context){
		if(coolweatherdb==null){
			coolweatherdb=new CoolWeatherDB(context);
		}
		return coolweatherdb;
	}
	/*
	 * 将Province实例存储到数据库
	 */
	public void saveProvince(Province province){
		if(province !=null){
			ContentValues values=new ContentValues();
			values.put("province_name", province.getProvincename());
			values.put("province_code", province.getProvincecode());
			db.insert("Province", null, values);
		}
	}
	/*
	 * 从数据库中读取所有省份信息
	 */
	public List<Province>loadProvinces(){
		List<Province> list=new ArrayList<Province>();
		Cursor cursor=db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				Province province=new Province();
				province.segtId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvincename(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvincecode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			}while(cursor.moveToNext());
		}
		return list;
	}
	
	/*
	 * 将City实例存储到数据库
	 */
	public void saveCity(City city){
		if(city !=null){
			ContentValues values=new ContentValues();
			values.put("city_name", city.getCityname());
			values.put("city_code", city.getCitycode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	/*
	 * 从数据库中读取所有省份下的城市信息
	 */
	public List<City>loadCities(int provinceId){
		List<City> list=new ArrayList<City>();
		Cursor cursor=db.query("City", null, "province_id=?", new String[] {String.valueOf(provinceId)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				City city=new City();
				city.segtId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityname(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCitycode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			}while(cursor.moveToNext());
		}
		return list;
	}
	
	/*
	 * 将City实例存储到数据库
	 */
	public void saveCounty(County county){
		if(county !=null){
			ContentValues values=new ContentValues();
			values.put("county_name", county.getCountyname());
			values.put("county_code", county.getCountycode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	}
	/*
	 * 从数据库中读取所有城市下的县信息
	 */
	public List<County>loadCounties(int cityId){
		List<County> list=new ArrayList<County>();
		Cursor cursor=db.query("County", null, "city_id=?", 
				new String[] {String.valueOf(cityId)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				County county=new County();
				county.segtId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyname(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountycode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);
			}while(cursor.moveToNext());
		}
		return list;
	}
	
}
