package com.test.coolweather.util;

public interface HttpCallbackListener {
	//当服务器成功响应请求时调用，参数代表服务器返回的数据
	void onFinish(String response); 
	
	//当进行网络操作出现错误时调用
	 void onError(Exception e);
}
