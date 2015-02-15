package cn.zipper.framwork.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Handler.Callback;
import android.os.Message;

public class ZService extends Service implements Callback{
	
	private final ZBinder binder = new ZBinder();
	protected Handler handler;
	
	public class ZBinder extends Binder {
		
		public ZService getService() {
			return ZService.this;
		}
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		handler = new Handler(this);
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
