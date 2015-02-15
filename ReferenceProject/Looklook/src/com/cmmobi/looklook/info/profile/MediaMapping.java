package com.cmmobi.looklook.info.profile;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.cmmobi.looklook.common.utils.MD5;

public class MediaMapping {
	public transient static final int HANDLER_FLAG_READ_CLEANUP = 0x1893a101;
	public transient static final int HANDLER_FLAG_FAVOR_CLEANUP = 0x1893a102;
	public transient static final int HANDLER_FLAG_SELFPHOTO_CLEANUP = 0x1893a103;
	public transient static final int HANDLER_FLAG_PRIVATEMSG_CLEANUP = 0x1893a104;
	public transient static final int HANDLER_FLAG_SYNC_CLEANUP = 0x1893a111;
	public transient static final int HANDLER_FLAG_LOCAL_CLEANUP = 0x1893a112;
	private transient static final String TAG = "MediaMapping";
	
	private HashMap<String, MediaValue> map;
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
		map = new HashMap<String, MediaValue>();
		
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
	public long getTotalSizeByType(int type){
/*		if(type==1){
			return total_txt_size;
		}else if(type==2){
			return total_jpg_size;
		}else if(type==3){
			return total_short_audio_size;
		}else if(type==4){
			return total_long_audio_size;
		}else if(type==5){
			return total_video_size;
		}*/
		long ret = 0;
		synchronized (map) {
			Iterator<Entry<String, MediaValue>> it = map.entrySet().iterator();
			while(it.hasNext()){
				Entry<String, MediaValue> item = it.next();
				
				if(item!=null){
					MediaValue mv = item.getValue();
					if(type<0 || mv.MediaType==type){
						ret = ret+mv.realSize;
					}
				}
			}
		}
		
		return ret; //其它，暂时给0
	}
	
	/**
	 * 根据来源获取当前来源的文件总大小，单位 byte
	 * @param send 1：浏览数据 2：收藏数据 3：自拍数据 4：私信数据
	 * */
	public long getTotalSizeBySrc(int src){
/*		if(src==1){
			return total_read_size;
		}else if(src==2){
			return total_favorite_size;
		}else if(src==3){
			return total_self_photo_size;
		}else if(src==4){
			return total_private_msg_size;
		}*/
		long ret = 0;
		synchronized (map) {
			Iterator<Entry<String, MediaValue>> it = map.entrySet().iterator();
			while(it.hasNext()){
				Entry<String, MediaValue> item = it.next();
				
				if(item!=null){
					MediaValue mv = item.getValue();
					if(src<0 || mv.Belong==src){
						ret = ret+mv.realSize;
					}
				}
			}
		}
		
		return ret; //其它，暂时给0
		
	}
	
	/**
	 * 根据是否同步获取对应的文件总大小，单位 byte
	 * @param send  1：是  2：不是
	 * */
	public long getTotalSizeBySync(int Sync){
/*		if(Sync==1){
			return total_sync_size;
		}else if(Sync==2){
			return total_local_size;
		}*/
		
		long ret = 0;
		synchronized (map) {
			Iterator<Entry<String, MediaValue>> it = map.entrySet().iterator();
			while(it.hasNext()){
				Entry<String, MediaValue> item = it.next();
				
				if(item!=null){
					MediaValue mv = item.getValue();
					if(Sync<0 || mv.Sync==Sync){
						ret = ret+mv.realSize;
					}
				}
			}
		}
		
		return ret; //其它，暂时给0
	}
	
	/**
	 *  清理浏览缓存
	 *  type -1： all 1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	 * */
	public void cleanReadMedia(Handler handler, int type){
/*		for(Entry<String, MediaValue> entry: map.entrySet()){
			if(entry!=null && entry.getValue()!=null){
				String key = entry.getKey();
				MediaValue mv = entry.getValue();
				if(mv.Belong==1 && (type<0 || type==mv.MediaType)){
					deleteDiskFile(mv);
					delMedia(key);
				}
			}
		}*/
		DEL_TASK dt = new DEL_TASK(handler, HANDLER_FLAG_READ_CLEANUP, 0);
		dt.execute(1, 1, type);
	}
	
	/**
	 *  清理收藏缓存
	 *  type -1： all 1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	 * */
	public void cleanFavoriteMedia(Handler handler, int type){
/*		for(Entry<String, MediaValue> entry: map.entrySet()){
			if(entry!=null && entry.getValue()!=null){
				String key = entry.getKey();
				MediaValue mv = entry.getValue();
				if(mv.Belong==2 && (type<0 || type==mv.MediaType)){
					deleteDiskFile(mv);
					delMedia(key);
				}
			}
		}*/
		DEL_TASK dt = new DEL_TASK(handler, HANDLER_FLAG_FAVOR_CLEANUP, 0);
		dt.execute(1, 2, type);
	}
	
	/**
	 *  清理自拍缓存
	 *  type -1： all 1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	 * */
	public void cleanSelfPhotoMedia(Handler handler, int type){
/*		for(Entry<String, MediaValue> entry: map.entrySet()){
			if(entry!=null && entry.getValue()!=null){
				String key = entry.getKey();
				MediaValue mv = entry.getValue();
				if(mv.Belong==3 && (type<0 || type==mv.MediaType)){
					deleteDiskFile(mv);
					delMedia(key);
				}
			}
		}*/
		DEL_TASK dt = new DEL_TASK(handler, HANDLER_FLAG_SELFPHOTO_CLEANUP, 0);
		dt.execute(1, 3, type);
	}
	
	/**
	 *  清理私信缓存
	 *  type -1： all 1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	 * */
	public void cleanPrivateMsgMedia(Handler handler, int type){
/*		for(Entry<String, MediaValue> entry: map.entrySet()){
			if(entry!=null && entry.getValue()!=null){
				String key = entry.getKey();
				MediaValue mv = entry.getValue();
				if(mv.Belong==4 && (type<0 || type==mv.MediaType)){
					deleteDiskFile(mv);
					delMedia(key);
				}
			}
		}*/
		DEL_TASK dt = new DEL_TASK(handler, HANDLER_FLAG_PRIVATEMSG_CLEANUP, 0);
		dt.execute(1, 4, type);
	}
	
	/**
	 *  清理已同步缓存
	 * @param type -1 all 1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	 * @param limit 清理的上限， 0或小于0表示无上限，全清理，否则不超过这个阈值
	 * */
	public void cleanSyncMsgMedia(Handler handler, int type, long limit){
/*		for(Entry<String, MediaValue> entry: map.entrySet()){
			if(entry!=null && entry.getValue()!=null){
				String key = entry.getKey();
				MediaValue mv = entry.getValue();
				if(mv.Sync==1 && (type<0 || type==mv.MediaType)){
					deleteDiskFile(mv);
					delMedia(key);
				}
			}
		}*/
		DEL_TASK dt = new DEL_TASK(handler, HANDLER_FLAG_SYNC_CLEANUP, limit);
		dt.execute(2, 1, type);
	}
	
	/**
	 *  清理未同步同步缓存
	 *  type -1： all 1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	 * */
	public void cleanLocalMsgMedia(Handler handler, int type){
/*		for(Entry<String, MediaValue> entry: map.entrySet()){
			if(entry!=null && entry.getValue()!=null){
				String key = entry.getKey();
				MediaValue mv = entry.getValue();
				if(mv.Sync==2 && (type<0 || type==mv.MediaType)){
					deleteDiskFile(mv);
					delMedia(key);
				}
			}
		}*/
		DEL_TASK dt = new DEL_TASK(handler, HANDLER_FLAG_LOCAL_CLEANUP, 0);
		dt.execute(2, 2, type);
	}
	
	
	/**
	 * 智能清理  已同步清一半，浏览缓存全清
	 * */
	public void AutoCleanUp(Handler handler){
		cleanReadMedia(handler, -1);
		
		//has del syncSize
/*		long delDoneSize = 0;
		long delLimit = total_sync_size/2;*/
		long total_sync_size = getTotalSizeBySync(1);
		cleanSyncMsgMedia(handler, -1, total_sync_size/2);
		
/*		for(Entry<String, MediaValue> entry: map.entrySet()){
			if(delDoneSize>=delLimit){
				break;
			}
			
			if(entry!=null && entry.getValue()!=null){
				String key = entry.getKey();
				MediaValue mv = entry.getValue();
				if(mv.Sync==1){
					deleteDiskFile(mv);
					delMedia(key);
					delDoneSize += mv.realSize;
				}
			}
		}*/
	}
	
	public MediaValue getMedia(String UID, String uri){
		MediaValue result = null;
		if(map!=null && UID!=null && uri!=null){
			String key = MD5.encode((UID+uri).getBytes());

			synchronized (map) {
				result = map.get(key);
			}
			 
		}
		
		return result;
	}
	
	/**
	 * 
	 * @return the value of any previous mapping with the specified key or null if there was no such mapping.
	 * */
	public MediaValue setMedia(String UID, String uri, MediaValue value){
		MediaValue result = null;
		if(map!=null && UID!=null && uri!=null){
			String key = MD5.encode((UID+uri).getBytes());

			synchronized (map) {
/*				addSumByType(value);
				addSumBySrc(value);
				addSumBySync(value);*/
				result = map.put(key, value);
/*				if(result!=null){
					subSumByType(result);
					subSumBySrc(result);
					subSumBySync(result);
				}*/

			}
			 
		}
		
		return result;
	}
	
	/**
	 *  
	 *  从map中删除对应mediavalue
	 * */
	public void delMedia(String UID, String uri){
		if(map!=null && UID!=null && uri!=null){
			String key = MD5.encode((UID+uri).getBytes());
			delMedia(key);
			
		}
		
	}
	
	/**
	 *  
	 *  从map中删除mediavalue
	 * */
	private void delMedia(String key){
		if(map!=null && key!=null){

			synchronized (map) {
				MediaValue result = map.get(key);
				if(result!=null){
					subSumByType(result);
					subSumBySrc(result);
					subSumBySync(result);
					map.remove(key);
				}


			}
			 
		}
		
	}
	
	private void addSumByType(MediaValue value){
/*		if(value.MediaType==1){
			total_txt_size += value.realSize;
		}else if(value.MediaType==2){
			total_jpg_size += value.realSize;
		}else if(value.MediaType==3){
			total_short_audio_size += value.realSize;
		}else if(value.MediaType==4){
			total_long_audio_size += value.realSize;
		}else if(value.MediaType==5){
			total_video_size += value.realSize;
		}*/
	}
	
	private void addSumBySrc(MediaValue value){
/*		if(value.Belong==1){
			total_read_size += value.realSize;
		}else if(value.Belong==2){
			total_favorite_size += value.realSize;
		}else if(value.Belong==3){
			total_self_photo_size += value.realSize;
		}else if(value.Belong==4){
			total_private_msg_size += value.realSize;
		}*/
		
	}
	
	private void addSumBySync(MediaValue value){
/*		if(value.Sync==1){
			total_sync_size += value.realSize;
		}else if(value.Sync==2){
			total_local_size += value.realSize;
		}*/
	}

	private void subSumByType(MediaValue value){
/*		if(value.MediaType==1){
			total_txt_size -= value.realSize;
		}else if(value.MediaType==2){
			total_jpg_size -= value.realSize;
		}else if(value.MediaType==3){
			total_short_audio_size -= value.realSize;
		}else if(value.MediaType==4){
			total_long_audio_size -= value.realSize;
		}else if(value.MediaType==5){
			total_video_size -= value.realSize;
		}*/
	}
	
	private void subSumBySrc(MediaValue value){
/*		if(value.Belong==1){
			total_read_size -= value.realSize;
		}else if(value.Belong==2){
			total_favorite_size -= value.realSize;
		}else if(value.Belong==3){
			total_self_photo_size -= value.realSize;
		}else if(value.Belong==4){
			total_private_msg_size -= value.realSize;
		}*/
		
	}
	
	private void subSumBySync(MediaValue value){
/*		if(value.Sync==1){
			total_sync_size -= value.realSize;
		}else if(value.Sync==2){
			total_local_size -= value.realSize;
		}*/
	}
	
	private class DEL_TASK extends AsyncTask<Integer, Void, Integer>{
		private Handler handler;
		private int handler_flag;
		private long limit;
		
		public DEL_TASK(Handler _handler, int _handler_flag, long _limit){
			handler = _handler;
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
			
			long delDoneSize = 0;
			synchronized (map) {
				Iterator<Entry<String, MediaValue>> it = map.entrySet().iterator();
				while(it.hasNext()){
					if(limit>0 && delDoneSize>=limit){
						break;
					}
					
					Entry<String, MediaValue> item = it.next();
					
					if(item!=null){
						MediaValue mv = item.getValue();
						if(((category==1 && mv.Belong==category_value) || (category==2 && mv.Sync==category_value))
								&& (fileType<0 || mv.MediaType==fileType)){
							deleteDiskFile(mv);
							it.remove();
							delMedia(item.getKey());
							delDoneSize += mv.realSize;
						}
					}
				}
			}

/*			for(Entry<String, MediaValue> entry: map.entrySet()){
				if(limit>0 && delDoneSize>=limit){
					break;
				}
				
				if(entry!=null && entry.getValue()!=null){
					String key = entry.getKey();
					MediaValue mv = entry.getValue();
					
					if(((category==1 && mv.Belong==category_value) || (category==2 && mv.Sync==category_value))
							&& (fileType<0 || mv.MediaType==fileType)){
						deleteDiskFile(mv);
						delMedia(key);
						delDoneSize += mv.realSize;
					}
				}
			}*/
			
			return fileType;
		}
		
		@Override
		protected void onPostExecute(Integer result){
			if(handler!=null){
				handler.obtainMessage(handler_flag, result).sendToTarget();
			}
		}
	}
	
	public void deleteDiskFile(MediaValue value){
		if(value != null && value.path!=null ){
			File file = new File(Environment.getExternalStorageDirectory() + value.path);

			if(file.exists() && file.isFile()){
				Log.e(TAG, "del file:" + file.getAbsolutePath());
				file.delete();
			}
		}

	}
}
