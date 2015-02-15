package com.cmmobi.looklook.common.storage;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;


public class AsyncCommonInfoSaver extends Thread implements Callback{
	private static AsyncCommonInfoSaver  ins = null;
	private static final String TAG = "AsyncCommonInfoSaver";
	private Handler handler;
	private Class<?> cls;
	private Object saveObj;
	private String key;
	
	private final int HANDLER_FLAG_ASYNCSAVE_DONE = 0x18791124;
	private final int HANDLER_FLAG_SAVE_DELAY = 0x1381710;
	private transient final long TIME_DELAY_TO_SAVE = 50; //50毫秒
	
	private long requset_time;
	private long write_time;;
	
	public static AsyncCommonInfoSaver getInstance(){
		if(ins==null){
			ins = new AsyncCommonInfoSaver();
		}
		
		return ins;
	} 
	
	/**
	 * 
	 * @param handler
	 * @param responseType: 响应类型;
	 * @param cls: 响应对象的class类型;
	 */
	private AsyncCommonInfoSaver() {
		this.handler = new Handler(this);
		this.start();
	}
	
	public void save(String key, Object request, Class<?> cls) {
		// TODO Auto-generated method stub
		this.key = key;
		this.saveObj = request;
		this.cls = cls;
		handler.removeMessages(HANDLER_FLAG_SAVE_DELAY);
		handler.sendEmptyMessageDelayed(HANDLER_FLAG_SAVE_DELAY, TIME_DELAY_TO_SAVE);
	
	}
	
	@Override
	public void run(){
		while(true){
			if(write_time<requset_time){
				Log.e(TAG, "AsyncCommonInfoSaver - run, key: " + key + " persist!");
				int tryCount = 3;
				while(tryCount>0){
					try{
						StorageManager.getInstance().putItem(key, saveObj, cls);
						break;
					}catch(Exception e){
						e.printStackTrace();
						try {
							tryCount-- ;
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}

				this.write_time = System.currentTimeMillis();
				handler.obtainMessage(HANDLER_FLAG_ASYNCSAVE_DONE).sendToTarget();

			}else{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}


		}

		
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case HANDLER_FLAG_SAVE_DELAY:
			this.requset_time = System.currentTimeMillis();
			break;
		case HANDLER_FLAG_ASYNCSAVE_DONE:
			break;

		default:
			break;
		}
		return false;
	}
}
