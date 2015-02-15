package com.cmmobi.looklook.common.web;

import java.util.concurrent.ConcurrentHashMap;

import android.widget.ImageView;

/**
 * 图片下载线程池,暂时可允许最多三个线程同时下载。
 * 
 * @author zhangwei
 */
public class ImageDownloadPool {
	private static ImageDownloadPool ins;
	private String TAG = "DownloadPool";
	private int MAX_THREAD_COUNT;// 定义最大同时下载线程
	private int mActiveThread; // 当前活动的线程数

	// private List<DownloadPiece> mQuery; //下载队列.
	private ConcurrentHashMap<String, DownloadItem> cache;

	public class DownloadPiece {

		ImageView imageview;
		public String uri;// 图片的url
	}

	private ImageDownloadPool() {
		MAX_THREAD_COUNT = 3;
		mActiveThread = 0;
		// mQuery = new ArrayList<DownloadPiece>();
		cache = new ConcurrentHashMap<String, DownloadItem>();
	}

	public static ImageDownloadPool getInstance() {
		if (ins == null) {
			ins = new ImageDownloadPool();
			// ins.setPriority(Thread.MIN_PRIORITY);
			// ins.setName("DownloadPool");
			// ins.start();
		}

		return ins;
	}

	// 生产者,生产空闲资源
	public synchronized void produce(String key) {
		//String key = MD5.encode(url.getBytes());
		while (mActiveThread < 0) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(cache.containsKey(key)){
			cache.remove(key);
			//synchronized(this){		
				mActiveThread--;
			//}
		}


		/**
		 * 通知所有消费者 若不加入notify调用,处于wait状态的消费者将一直等待下去。
		 */
		this.notifyAll();


	}

	// 消费者，消费空闲资源
	public  synchronized boolean consume(String key) {

		if(cache.containsKey(key)){
			return false;
		}
		
		DownloadItem item = new DownloadItem(System.currentTimeMillis());

		while (mActiveThread > MAX_THREAD_COUNT) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		cache.put(key, item);
		//synchronized(this){
			mActiveThread++;
		//}

		/**
		 * 通知所有生产者 若不加入notify调用,处于wait状态的生产者将一直等待下去。
		 */
		this.notifyAll();
		return true;
	}




}