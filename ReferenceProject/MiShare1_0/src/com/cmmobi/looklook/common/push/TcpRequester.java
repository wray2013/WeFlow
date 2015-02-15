package com.cmmobi.looklook.common.push;

import java.net.SocketTimeoutException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.cmmobi.looklook.Config;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.info.profile.ActiveAccount;


import cn.zipper.framwork.io.network.ZNetworkStateDetector;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;



public class TcpRequester {
	public static final int HANDLER_FLAG_TCP_NEW_LOGIN = 0x8277389;
	public static final int HANDLER_FLAG_TCP_HEART_BEAT = 0x8277390;
	public static final int HANDLER_FLAG_TCP_TIME_SYNC = 0x8277391;
	public static final int HANDLER_FLAG_TCP_NEW_MSG = 0x8277392;
	private static final String TAG = "TCPRequester";
	//public static final int TCP_MIN_INTERVAL_SECONDS = 20; //seconds
	public static final int TCP_DEFAULT_INTERVAL_SECONDS = 180;
	//public static final int TCP_DEFAULT_INTERVAL_SECONDS_GPRS = 180;

	private static boolean isRunning = false;
	private static TcpClient client;
	private static TcpRequester ins;
	

	private TCPWork worker;
	
	private TcpRequester(){
		worker = new TCPWork();
		worker.start();
	}
	
	public static TcpRequester getInstance(){
		if(ins==null){
			ins = new TcpRequester();
		}
		
		return ins;
	}
	/**
	 * 
	 * @param handler
	 */
	public boolean sendTCP(Context context, Handler handler, int sync, String productID, String micshareid, int interval_seconds) {
		//GetAccounInfoWorker worker = new GetAccounInfoWorker(handler, WeiboResponse.RWUserInfo[].class);
		
		//TCPWork worker = new TCPWork(context, handler, sync, productID, micshareid, interval_seconds);
		return worker.execute(context, handler, sync, productID, micshareid, interval_seconds);
	}
	
	//private static Lock lock = new ReentrantLock();
	
	private static class TCPWork extends Thread {

		private Handler handler;
//		private Context context;
//		private int sync;
//		private String seq;
//		private String productID;
//		private String micshareid;
//		private int timeOut;
		
		private LinkedBlockingQueue<PushCommand> fifo;
		


/*		public TCPWork(Context context, Handler handler, int sync, String productID, String micshareid, int timeOut) {
			// TODO Auto-generated constructor stub
			this.context = context;
			this.handler = handler;
			this.sync = sync;
			this.productID = productID;
			this.micshareid = micshareid;
			this.timeOut = timeOut;
		}*/

		public TCPWork() {
			// TODO Auto-generated constructor stub
			fifo = new LinkedBlockingQueue<PushCommand>();
		}

		public boolean execute(Context context, Handler handler, int sync, String productID, String micshareid, int timeOut) {
			// TODO Auto-generated method stub
			//this.start();
			return fifo.offer(new PushCommand(sync, productID, micshareid, timeOut));

		}
		
		
		public void run() {
			// synchronized (TcpRequester.class) {...}
			while(true){
				try{
					//1. check socket 
					if (client == null) {
						client = new TcpClient(Config.TCP_HOST, Config.TCP_PORT);	
					}
					
					client.setSoTimeout(Config.TCP_READ_INTERVAL * 1000);
					
					//2. check command queue
					PushCommand command = fifo.poll();
					if(command!=null){
						if(command.micshareid!=null && command.productID!=null && !command.micshareid.equals(ActiveAccount.TEMP_USERID)){
							/*
							 * 请求参数： 001|5358e7db0646f04a820bcb20ebc2e7818a70|1\0
							 * 产品id|用户id|是否需要服务器返回时间戳 1 需要 0不需要
							 * 
							 * 返回参数 1|1324689755413\0 是否有更新 1有 0 没有| 时间戳
							 * （可能没有值，如果没值前面的|也省略） 注意：如果缓存服务器出现异常、重启、或者down掉了，直接返回1，
							 * 客户端收到后主动调用一次listMessage接口
							 */
							StringBuilder sb = new StringBuilder();
							sb.append(command.productID);
							sb.append("|");

							sb.append(command.micshareid);
							sb.append("|");

							sb.append(command.sync);
							
							//3. send new user
							Log.e(TAG, "sendTCP - sync:" + command.sync
									+ ", productID:" + command.productID
									+ ", micshareid:" + command.micshareid
									+ ", interval_seconds:" + command.timeOut);
							client.send(sb.toString().getBytes());
						}else{
							//3. send heart beat
							Log.e(TAG, "sendTCP - heart beat");
							client.send(null);
						}
						
					}
					
					//4. receive
					Log.e(TAG, "begin receive ... ");

					String info = client.receive();

					Log.e(TAG, "end receive - info:" + info);

					String hasNewMsg = parseResponse(info, 0, "0");

					String serverTimeValue = parseResponse(info, 1, null);

					if (hasNewMsg.equals("1")) {
						Message msg = new Message();
						msg.what = HANDLER_FLAG_TCP_NEW_MSG;

						handler.sendMessage(msg);
					}

					if (serverTimeValue != null) {
						Message msg = new Message();
						msg.what = HANDLER_FLAG_TCP_TIME_SYNC;
						msg.obj = serverTimeValue;
						handler.sendMessage(msg);
					}
				}catch(SocketTimeoutException e){
					e.printStackTrace(); //normal case
				}catch(Exception e){
					e.printStackTrace();
					try {
						Thread.sleep(Config.NO_NETWORK_SLEEP_PERIOD_SECONDS * 1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					}
				}

				

				
				
				
			}
			
			
/*			if (lock.tryLock()) {
				try {
					// manipulate protected state
					handler.removeMessages(HANDLER_FLAG_TCP_OP_DONE);
					try {
						
						 * 请求参数： 001|5358e7db0646f04a820bcb20ebc2e7818a70|1#
						 * 产品id|用户id|是否需要服务器返回时间戳 1 需要 0不需要
						 * 
						 * 返回参数 1|1324689755413# 是否有更新 1有 0 没有| 时间戳
						 * （可能没有值，如果没值前面的|也省略） 注意：如果缓存服务器出现异常、重启、或者down掉了，直接返回1，
						 * 客户端收到后主动调用一次listMessage接口
						 
						StringBuilder sb = new StringBuilder();
						sb.append(productID);
						sb.append("|");

						sb.append(micshareid);
						sb.append("|");

						sb.append(sync);

						// sb.append("#");

						if (client == null) {
							client = new TcpClient(Config.TCP_HOST,
									Config.TCP_PORT, sb.toString().getBytes());
						} else {
							Log.e(TAG, "sendTCP - sync:" + sync
									+ ", productID:" + productID
									+ ", micshareid:" + micshareid
									+ ", interval_seconds:" + timeOut);
							client.send( sb.toString().getBytes() null);
						}

						if (timeOut < TCP_DEFAULT_INTERVAL_SECONDS) {
							timeOut = TCP_DEFAULT_INTERVAL_SECONDS;
						}

						// client.setSoTimeout(timeOut*1000);

						Log.e(TAG, "begin receive ... ");

						String info = client.receive();

						Log.e(TAG, "end receive - info:" + info);

						String hasNewMsg = parseResponse(info, 0, "0");

						String serverTimeValue = parseResponse(info, 1, null);

						if (hasNewMsg.equals("1")) {
							Message msg = new Message();
							msg.what = HANDLER_FLAG_TCP_NEW_MSG;

							handler.sendMessage(msg);
						}

						if (serverTimeValue != null) {
							Message msg = new Message();
							msg.what = HANDLER_FLAG_TCP_TIME_SYNC;
							msg.obj = serverTimeValue;
							handler.sendMessage(msg);
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						if (client != null) {
							client.close();
							client = null;
						}

					}

					if (ZNetworkStateDetector.isConnected()) {
						handler.sendEmptyMessageDelayed(HANDLER_FLAG_TCP_OP_DONE, 5000);
					} else {
						handler.sendEmptyMessageDelayed( HANDLER_FLAG_TCP_OP_DONE, TCP_DEFAULT_INTERVAL_SECONDS * 1000);
					}
				} finally {
					lock.unlock();
				}
			} else {
				// perform alternative actions
				Log.v(TAG, "can't get lock, return");
			}*/

		}
		
	}
	
	public static String parseResponse(String resp, int index, String defaultStr){
		 if(resp!=null && resp.length()>0 /*&& resp.endsWith("#")*/){
			 resp = resp.substring(0, resp.length()-1);
			 String[] array = resp.split("\\|");
			 if(array!=null && array.length>index){
				 return array[index];
			 }
		 }
		 
		 return defaultStr;
	}
}
