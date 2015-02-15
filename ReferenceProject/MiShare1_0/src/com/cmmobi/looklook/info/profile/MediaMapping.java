package com.cmmobi.looklook.info.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.storage.SqlMediaManager;
import com.cmmobi.looklook.common.utils.MD5;
import com.googlecode.concurrentlinkedhashmap.EntryWeigher;
import com.googlecode.concurrentlinkedhashmap.EvictionListener;

public class MediaMapping {
	public transient static final int HANDLER_FLAG_READ_CLEANUP = 0x1893a101;
	public transient static final int HANDLER_FLAG_FAVOR_CLEANUP = 0x1893a102;
	public transient static final int HANDLER_FLAG_SELFPHOTO_CLEANUP = 0x1893a103;
	public transient static final int HANDLER_FLAG_PRIVATEMSG_CLEANUP = 0x1893a104;
	public transient static final int HANDLER_FLAG_SYNC_CLEANUP = 0x1893a111;
	public transient static final int HANDLER_FLAG_LOCAL_CLEANUP = 0x1893a112;
	private transient static final String TAG = "MediaMapping";
	
	public transient static EntryWeigher<String, MediaValue> memoryUsageWeigher = new EntryWeigher<String, MediaValue>() {
		  //final MemoryMeter meter = new MemoryMeter();

		  @Override 
		  public int weightOf(String key, MediaValue value) {
		    //long bytes = meter.measure(key) + meter.measure(value);
			  long bytes = value.realSize;//
			  if(bytes<=0){
				  bytes = 1;
			  }

		    return (int) Math.min(bytes, Integer.MAX_VALUE);
		  }
	};
	
	public transient static EvictionListener<String, MediaValue> listener = new EvictionListener<String, MediaValue>() {
		  @Override 
		  public void onEviction(String key, MediaValue value) {
			    //rm the key(file name)
			    
			    //context.deleteFile(key);
			    deleteDiskFile(value);
			    Log.e(TAG, "Evicted(delete) key=" + key);
		  }
	};
	
	//private ConcurrentLinkedHashMap<String, MediaValue> map;
	//by type
	/**
	 * 所有文本类型数据的大小，单位byte
	 * */
	//private long total_txt_size;
	
	/**
	 * 所有图片类型数据的大小，单位byte
	 * */
	//private long total_jpg_size;
	
	/**
	 * 所有短音频类型数据的大小，单位byte
	 * */
	//private long total_short_audio_size;
	
	/**
	 * 所有长音频类型数据的大小，单位byte
	 * */
	//private long total_long_audio_size;
	
	/**
	 * 所有视频类型数据的大小，单位byte
	 * */
	//private long total_video_size;
	
	//by src
	/**
	 * 所有浏览类型数据的大小，单位byte
	 * */
	//private long total_read_size;
	
	/**
	 * 所有收藏类型数据的大小，单位byte
	 * */
	//private long total_favorite_size;
	
	/**
	 * 所有自拍类型数据的大小，单位byte
	 * */
	//private long total_self_photo_size;
	
	/**
	 * 所有私信类型数据的大小，单位byte
	 * */
	//private long total_private_msg_size;
	
	
	//by sync
	/**
	 * 所有本地且未同步类型数据的大小，单位byte
	 * */
	//private long total_local_size;
	
	/**
	 * 所有本地且已同步类型数据的大小，单位byte
	 * */
	//private long total_sync_size;
	
	
	public MediaMapping(){
/*		map = new ConcurrentLinkedHashMap.Builder<String, MediaValue>()
				.maximumWeightedCapacity(Constant.MEDIA_CACHE_LIMIT) // 1GB, internal storage, not memory
			    .weigher(memoryUsageWeigher)
			    .listener(listener)
			    .build();*/
		
/*		map = new HashMap<String, MediaValue>();*/

		
		//total_txt_size = 0;
		//total_jpg_size = 0;
		//total_short_audio_size = 0;
		//total_long_audio_size = 0;
		//total_video_size = 0;
		
/*		total_read_size = 0;
		total_favorite_size = 0;
		total_self_photo_size = 0;
		total_private_msg_size = 0;*/
		
/*		total_local_size = 0;
		total_sync_size = 0;*/
	}
	
	
	/**
	 * 根据文件类型获取当该类型文件总大小，单位 byte
	 * @param type 1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	 * */
	public long getTotalSizeByType(String uid, int type){
		return SqlMediaManager.getInstance().getTotalSizeByType(uid, type);
	}
	
	/**
	 * 根据来源获取当前来源的文件总大小，单位 byte
	 * @param send 1：浏览数据 2：收藏数据 3：自拍数据 4：私信数据
	 * */
	public long getTotalSizeBySrc(String uid, int src){
		return SqlMediaManager.getInstance().getTotalSizeBySrc(uid, src);
		
	}
	
	/**
	 * 根据是否同步获取对应的文件总大小，单位 byte
	 * @param send  1：是  2：不是
	 * */
	public long getTotalSizeBySync(String uid, int Sync){
		return SqlMediaManager.getInstance().getTotalSizeBySync(uid, Sync);
	}
	
	/**
	 *  清理浏览缓存
	 *  type -1： all 1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	 * */
	public void cleanReadMedia(Handler handler, String UID, int type){
		DEL_TASK dt = new DEL_TASK(handler, UID, HANDLER_FLAG_READ_CLEANUP, 0);
		dt.execute(1, 1, type);
	}
	
	/**
	 *  清理收藏缓存
	 *  type -1： all 1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	 * */
/*	public void cleanFavoriteMedia(Handler handler, int type){
		DEL_TASK dt = new DEL_TASK(handler, HANDLER_FLAG_FAVOR_CLEANUP, 0);
		dt.execute(1, 2, type);
	}*/
	
	/**
	 *  清理自拍缓存
	 *  type -1： all 1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	 * */
	public void cleanSelfPhotoMedia(Handler handler, String UID, int type){
		DEL_TASK dt = new DEL_TASK(handler, UID, HANDLER_FLAG_SELFPHOTO_CLEANUP, 0);
		dt.execute(1, 3, type);
	}
	
	/**
	 *  清理私信缓存
	 *  type -1： all 1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	 * */
	public void cleanPrivateMsgMedia(Handler handler, String UID, int type){
		DEL_TASK dt = new DEL_TASK(handler, UID, HANDLER_FLAG_PRIVATEMSG_CLEANUP, 0);
		dt.execute(1, 4, type);
	}
	
	/**
	 *  清理已同步缓存
	 * @param type -1 all 1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	 * @param limit 清理的上限， 0或小于0表示无上限，全清理，否则不超过这个阈值
	 * */
	public void cleanSyncMsgMedia(Handler handler, String UID, int type, long limit){
		DEL_TASK dt = new DEL_TASK(handler, UID, HANDLER_FLAG_SYNC_CLEANUP, limit);
		dt.execute(2, 1, type);
	}
	
	/**
	 *  清理未同步同步缓存
	 *  type -1： all 1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	 * */
	public void cleanLocalMsgMedia(Handler handler, String UID, int type){
		DEL_TASK dt = new DEL_TASK(handler, UID, HANDLER_FLAG_LOCAL_CLEANUP, 0);
		dt.execute(2, 0, type);
	}
	
	
	/**
	 * 智能清理  已同步清一半，浏览缓存全清
	 * */
	public void AutoCleanUp(Handler handler, String UID){
		cleanReadMedia(handler, UID, -1);
		long total_sync_size = getTotalSizeBySync(UID, 1);
		cleanSyncMsgMedia(handler, UID, -1, total_sync_size/2);
	}
	
	private MediaValue getMedia(String key){
		return SqlMediaManager.getInstance().getMedia(key);
	}
	
	public MediaValue getMedia(String UID, String uri){
		MediaValue result = null;
		if(UID!=null && uri!=null){
			String key = MD5.encode((UID+uri).getBytes());
			
			result = getMedia(key);
			//Log.v(TAG, "getMedia - UID:" + UID + ", uri:" + uri + ", key:" + key + ", result:" + result);
		}
		
		return result;
	}
	
	/**
	 * 
	 * @return the value of any previous mapping with the specified key or null if there was no such mapping.
	 * */
	public void setMedia(String UID, String uri, MediaValue value){
		if(UID!=null && uri!=null && value!=null){
			String key = MD5.encode((UID+uri).getBytes());
			//Log.v(TAG, "setMedia - UID:" + UID + ", uri:" + uri + ", path:" + value.localpath + ", key:" + key);
			copyMediaMappingToImageLoader(UID, uri, value);
			SqlMediaManager.getInstance().setMedia(key, value);
		}
	}
	
	/**
	 * 复制映射到imageloader中
	 * @param UID
	 * @param uri
	 * @param value
	 */
	public static void copyMediaMappingToImageLoader(String UID,String uri,MediaValue value){
		if(UID!=null && uri!=null && value!=null){
			String key = MD5.encode((UID+uri).getBytes());
			if(uri.startsWith("http://")&&2==value.MediaType){
				String relativePath=Constant.SD_STORAGE_ROOT+"/"+UID+"/pic/"+key+".jpg";
				String targetPath=LookLookActivity.SDCARD_PATH+relativePath;
				File target=new File(targetPath);
				if(!target.exists()){
					target.getParentFile().mkdirs();
					try {
						renameFile(LookLookActivity.SDCARD_PATH+value.localpath,targetPath);
						value.localpath=relativePath;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * 移动文件
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	public static void renameFile(String src,String dest) throws IOException{
		Log.e(TAG, "renameFile src:" + src + ", dest:" + dest);
		File destFile=new File(dest);
		if(!destFile.getParentFile().exists())
			destFile.getParentFile().mkdirs();
		if(!destFile.exists())
			destFile.createNewFile();
		File srcFile=new File(src); 
		srcFile.renameTo(destFile);//移动 
		srcFile.delete();//删除 
    }
	
	/**
	 *  
	 *  从map中删除对应mediavalue
	 * */
	public void delMedia(String UID, String uri){
		delMedia(UID, uri, false);
	}
	
	/**
	 *  
	 *  从map中删除对应mediavalue
	 * */
	public void delMedia(String UID, String uri, boolean delDiskFile){
		if(UID!=null && uri!=null){
			String key = MD5.encode((UID+uri).getBytes());
			//Log.v(TAG, "setMedia - UID:" + UID + ", uri:" + uri + ", key:" + key);
			delMedia(key, delDiskFile);
		}
		
	}
	
	/**
	 *  
	 *  从map中删除mediavalue
	 * */
	private void delMedia(String key, boolean delDiskFile){
		SqlMediaManager.getInstance().delMedia(key, delDiskFile);
	}
	

	private class DEL_TASK extends AsyncTask<Integer, Void, Integer>{
		private Handler handler;
		private int handler_flag;
		private long limit;
		private String UID;
		
		public DEL_TASK(Handler _handler, String UID, int _handler_flag, long _limit){
			handler = _handler;
			this.UID = UID;
			handler_flag = _handler_flag;
			limit = _limit;
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			int category = params[0];  //1 belong  2 sync
			int category_value = params[1]; //belong: 1：浏览数据 2：收藏数据 3：自拍数据 4：私信数据
			                                //sync:   1：是  2：不是
			int fileType = params[2];  //1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
			
			SqlMediaManager.getInstance().delMediaBat(UID, category, category_value, fileType, limit);
			
			return fileType;
		}
		
		@Override
		protected void onPostExecute(Integer result){
			if(handler!=null){
				handler.obtainMessage(handler_flag, result).sendToTarget();
			}
		}
	}
	
	public static void deleteDiskFile(final MediaValue value) {
		new Thread() {
			public void run() {
				if (value != null && value.localpath != null) {
					File file = new File(
							Environment.getExternalStorageDirectory()
									+ value.localpath);

					if (file.exists() && file.isFile()) {
						Log.e(TAG, "del file:" + file.getAbsolutePath());
						file.delete();
					}
				}
			}
		}.start();

	}
}
