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
	
	private List<Province> provinceList; //ʡ�б�
	private List<City> cityList;   //���б�
	private List<County> countyList; //���б�
	
	private Province selectedprovince;  //ѡ�е�ʡ
	private City selectedcity;  //ѡ�е���
	private int currentLevel;  //��ǰѡ�еļ���
	
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
		//listview�ĵ���¼�
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
	 * ��ѯȫ�����е�ʡ�����������ݿ��в�ѯ�����û�в�ѯ���ٵ���������ȥ��ѯ
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
			titleText.setText("�й�");
			currentLevel=LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
	
	/*
	 * ��ѯʡ�������У����������ݿ��в�ѯ�����û�в�ѯ���ٵ���������ȥ��ѯ
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
	 * ��ѯ���������أ����������ݿ��в�ѯ�����û�в�ѯ���ٵ���������ȥ��ѯ
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
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯ
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
					//�Է��������ص����ݽ��н�������
					result=Utility.handleProvincesResponse(coolweatherdb, response);
				}else if("city".equals(type)){
					result=Utility.handleCitiesResponse(coolweatherdb, response,
							selectedprovince.getId());
				}else if("county".equals(type)){
					result=Utility.handleCountiesResponse(coolweatherdb, response,
							selectedcity.getId());
				}
				if(result){
					//ͨ��runOnUiThread���������ص����̴߳����߼�
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
				//ͨ��runOnUiThread���������ص����̴߳����߼�
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", 
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	/*
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog(){
		if(progressdialog==null){
			progressdialog=new ProgressDialog(this);
			progressdialog.setMessage("���ڼ���....");
			progressdialog.setCanceledOnTouchOutside(false);
		}
		progressdialog.show();
	}
	
	/*
	 * �رնԻ���
	 */
	private void closeProgressDialog(){
		if(progressdialog !=null){
			progressdialog.dismiss();
		}
	}
	
	/*
	 * ����back�������ݵ�ǰ�����жϣ���ʱӦ�÷������б�ʡ�б�����ֱ���˳�
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
