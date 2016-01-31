package com.test.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener){
		//�����̷߳�����������
		new Thread(new Runnable(){

			@Override
			public void run() {
				HttpURLConnection connection=null;  
				try{
					URL url=new URL(address); //Ŀ�� �����ַ
					connection=(HttpURLConnection) url.openConnection(); //����http����
					connection.setRequestMethod("GET");  //�ӷ����������ȡ����
					connection.setConnectTimeout(8000);  //�������ӳ�ʱ
					connection.setReadTimeout(8000);   //���ö�ȡ��ʱ����������
					InputStream in=connection.getInputStream();  //��ȡ���������ص�������
					BufferedReader reader=new BufferedReader(new InputStreamReader(in)); 
					//�Ի�ȡ�������������ж�ȡ
					StringBuilder response=new StringBuilder();
					String line;
					while((line = reader.readLine()) !=null){
						response.append(line);
					}
					if(listener !=null){
						//�ص�onFinish��������
						listener.onFinish(response.toString());
					}
				}catch (Exception e){
					if(listener !=null){
						listener.onError(e);
					}
				}finally{
					if(connection !=null){
						connection.disconnect();
					}
				}
				
			}
			
		}).start();
	}
}
