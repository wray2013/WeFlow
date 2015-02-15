package com.cmmobi.looklook.common.gson;

import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.cmmobi.looklook.Config;
import com.cmmobi.looklook.common.constant.Constant;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;



public class UDPRequester {
	public static final int HANDLER_FLAG_UDP_OP_DONE = 0x8287390;
	public static final int HANDLER_FLAG_UDP_TIME_SYNC = 0x8287391;
	public static final int HANDLER_FLAG_UDP_NEW_MSG = 0x8287392;
	private static final String TAG = "UDPRequester";
	public static final int UDP_MIN_INTERVAL_SECONDS = 20; //seconds
	public static final int UDP_DEFAULT_INTERVAL_SECONDS = 20;
	public static final int UDP_DEFAULT_INTERVAL_SECONDS_GPRS = 180;

	private static boolean isRunning = false;
	
	/**
	 * 
	 * @param handler
	 */
	public static boolean sendUDP(Context context, Handler handler, int sync, String productID, String micshareid, int interval_seconds) {
		//GetAccounInfoWorker worker = new GetAccounInfoWorker(handler, WeiboResponse.RWUserInfo[].class);
		
		UDPWork worker = new UDPWork(context, handler, sync, productID, micshareid, interval_seconds);
		worker.execute();
		return true;



	}
	
	private static class UDPWork extends Thread {


		private Handler handler;
		private int sync;
		private String seq;
		private Context context;
		private String productID;
		private String micshareid;
		private int timeOut;

		public UDPWork(Context context, Handler handler, int sync, String productID, String micshareid, int timeOut) {
			// TODO Auto-generated constructor stub
			this.context = context;
			this.handler = handler;
			this.sync = sync;
			this.productID = productID;
			this.micshareid = micshareid;
			this.timeOut = timeOut;
		}

		public void execute() {
			// TODO Auto-generated method stub
			this.start();

		}
		
		
		public void run(){
			synchronized (UDPRequester.class) {
				
		        UDPClient client;
				handler.removeMessages(HANDLER_FLAG_UDP_OP_DONE);
				try {
					/*
					 * 请求参数： 001|5358e7db0646f04a820bcb20ebc2e7818a70|1#
					 * 产品id|用户id|是否需要服务器返回时间戳 1 需要 0不需要
					 * 
					 * 返回参数 1|1324689755413# 是否有更新 1有 0 没有| 时间戳 （可能没有值，如果没值前面的|也省略）
					 * 注意：如果缓存服务器出现异常、重启、或者down掉了，直接返回1，客户端收到后主动调用一次listMessage接口
					 */
					StringBuilder sb = new StringBuilder();
					sb.append(productID);
					sb.append("|");
					
					sb.append(micshareid);
					sb.append("|");
					
					sb.append(sync);
					
					sb.append("#");
					
					client = new UDPClient();
					if(timeOut<UDP_MIN_INTERVAL_SECONDS){
						timeOut = UDP_MIN_INTERVAL_SECONDS;
					}

			        client.setServerHost(Config.UDP_HOST, Config.UDP_PORT);
					
			        client.setSoTimeout(timeOut*1000);
			        
			        Log.e(TAG, "sendUDP - sync:" + sync + ", productID:" + productID + ", micshareid:" + micshareid + ", interval_seconds:" + timeOut);	
			        
			        client.send(sb.toString().getBytes());

			        Log.e(TAG, "begin receive ... ");
			        
			        String info = client.receive();
			        
			        Log.e(TAG, "end receive - info:" + info);
		        	
			        client.close();			
					
		        	String hasNewMsg = parseResponse(info, 0, "0");
		        	
		        	String serverTimeValue = parseResponse(info, 1, null);

		        	if(hasNewMsg.equals("1")){
			        	Message msg = new Message();
			        	msg.what = HANDLER_FLAG_UDP_NEW_MSG;
						
						handler.sendMessage(msg);
		        	}
		        	
		        	if(serverTimeValue!=null){
			        	Message msg = new Message();
			        	msg.what = HANDLER_FLAG_UDP_TIME_SYNC;
			        	msg.obj = serverTimeValue;
						handler.sendMessage(msg);
		        	}
		        	
		        	
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(ZNetworkStateDetector.isConnected()){
					handler.sendEmptyMessageDelayed(HANDLER_FLAG_UDP_OP_DONE, Constant.HEARTBEAT_INTERVAL);
				}else{
					handler.sendEmptyMessageDelayed(HANDLER_FLAG_UDP_OP_DONE, UDP_MIN_INTERVAL_SECONDS * 1000);
				}
			}
			

			
		}
		
	}
	
	public static String parseResponse(String resp, int index, String defaultStr){
		 if(resp!=null && resp.length()>1 /*&& resp.endsWith("#")*/){
			 resp = resp.substring(0, resp.length()-1);
			 String[] array = resp.split("\\|");
			 if(array!=null && array.length>index){
				 return array[index];
			 }
		 }
		 
		 return defaultStr;
	}
}
