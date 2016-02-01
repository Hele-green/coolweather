package com.test.coolweather.activity;

import java.util.ArrayList;
import java.util.List;

import com.test.coolweather.R;
import com.test.coolweather.model.City;
import com.test.coolweather.model.CoolWeatherDB;
import com.test.coolweather.model.County;
import com.test.coolweather.model.Province;
import com.test.coolweather.util.HttpCallbackListener;
import com.test.coolweather.util.HttpUtil;
import com.test.coolweather.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	
	private ProgressDialog progressdialog;
	private TextView titleText;
	private ListView listview;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolweatherdb;
	private List<String> dataList=new ArrayList<String>();
	
	private List<Province> provinceList; //省列表
	private List<City> cityList;   //市列表
	private List<County> countyList; //县列表
	
	private Province selectedprovince;  //选中的省
	private City selectedcity;  //选中的市
	private int currentLevel;  //当前选中的级别
	
	@Override
	
	protected void  onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_choose_main);
		listview=(ListView) findViewById(R.id.list_view);
		titleText=(TextView) findViewById(R.id.title_text);
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
		listview.setAdapter(adapter);
		coolweatherdb=CoolWeatherDB.getInstance(this);
		//listview的点击事件
		listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,int index,long arg3){
				if(currentLevel==LEVEL_PROVINCE){
					selectedprovince=provinceList.get(index);
					querycity();
				}else if(currentLevel==LEVEL_CITY){
					selectedcity=cityList.get(index);
					querycounty();
				}
			}
			
		});
		queryprovince();
	}
	/*
	 * 查询全国所有的省，优先在数据库中查询，如果没有查询到再到服务器上去查询
	 */
	private void queryprovince(){
		provinceList=coolweatherdb.loadProvinces();
		if(provinceList.size()>0){
			dataList.clear();
			for(Province province:provinceList){
				dataList.add(province.getProvincename());
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			titleText.setText("中国");
			currentLevel=LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
	
	/*
	 * 查询省内所有市，优先在数据库中查询，如果没有查询到再到服务器上去查询
	 */
	private void querycity(){
		cityList=coolweatherdb.loadCities(selectedprovince.getId());
		if(cityList.size()>0){
			dataList.clear();
			for(City province:cityList){
				dataList.add(province.getCityname());
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			titleText.setText(selectedprovince.getProvincename());
			currentLevel=LEVEL_CITY;
		}else{
			queryFromServer(selectedprovince.getProvincecode(),"city");
		}
	}
	
	/*
	 * 查询市内所有县，优先在数据库中查询，如果没有查询到再到服务器上去查询
	 */
	private void querycounty(){
		countyList=coolweatherdb.loadCounties(selectedcity.getId());
		if(countyList.size()>0){
			dataList.clear();
			for(County province:countyList){
				dataList.add(province.getCountyname());
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			titleText.setText(selectedcity.getCityname());
			currentLevel=LEVEL_COUNTY;
		}else{
			queryFromServer(selectedcity.getCitycode(),"county");
		}
	}
	
	/*
	 * 根据传入的代号和类型从服务器上查询
	 */
	private void queryFromServer(final String code,final String type){
		String address;
		if(!TextUtils.isEmpty(code)){
			address="http://www.weather.com.cn/data/list3/city" +code +".xml";
		}else{
			address="http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){
			@Override
			public void onFinish(String response){
				boolean result=false;
				if("province".equals(type)){
					//对服务器返回的数据进行解析处理
					result=Utility.handleProvincesResponse(coolweatherdb, response);
				}else if("city".equals(type)){
					result=Utility.handleCitiesResponse(coolweatherdb, response,
							selectedprovince.getId());
				}else if("county".equals(type)){
					result=Utility.handleCountiesResponse(coolweatherdb, response,
							selectedcity.getId());
				}
				if(result){
					//通过runOnUiThread（）方法回到主线程处理逻辑
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
							closeProgressDialog();
							if("province".equals(type)){
								queryprovince();
							}else if("city".equals(type)){
								querycity();
							}else if("county".equals(type)){
								querycounty();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e){
				//通过runOnUiThread（）方法回到主线程处理逻辑
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", 
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	/*
	 * 显示进度对话框
	 */
	private void showProgressDialog(){
		if(progressdialog==null){
			progressdialog=new ProgressDialog(this);
			progressdialog.setMessage("正在加载....");
			progressdialog.setCanceledOnTouchOutside(false);
		}
		progressdialog.show();
	}
	
	/*
	 * 关闭对话框
	 */
	private void closeProgressDialog(){
		if(progressdialog !=null){
			progressdialog.dismiss();
		}
	}
	
	/*
	 * 捕获back键，根据当前级别判断，此时应该返回市列表、省列表、还是直接退出
	 */
	@Override
	public void onBackPressed(){
		if(currentLevel==LEVEL_COUNTY){
			querycity();
		}else if(currentLevel==LEVEL_CITY){
			queryprovince();
		}else{
			finish();
		}
	}
}
