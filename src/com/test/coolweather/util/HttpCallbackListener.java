package com.test.coolweather.util;

public interface HttpCallbackListener {
	//���������ɹ���Ӧ����ʱ���ã�����������������ص�����
	void onFinish(String response); 
	
	//����������������ִ���ʱ����
	 void onError(Exception e);
}
