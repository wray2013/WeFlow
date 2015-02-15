package com.cmmobi.looklook.common.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.utils.ZGraphics;

import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobi.looklook.networktask.UploadNetworkTask;
import com.cmmobivideo.utils.Mp4InfoUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.utils.XUtils;

public class MediaCoverUtils {
	
    public static String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath();
    
//	public static MediaCover getMediaCover(String uid, String url, int type) {
//		MediaCover mMediaCover = new MediaCover();
//		// 没有取得封面url
//		if (url != null) {
//			mMediaCover.url = url;
//			//不是web图
//			if (!isNetworkAddress(mMediaCover.url)) {
//				//uri为null:未命中
//				mMediaCover = checkMediaMapping(uid, mMediaCover.url, mMediaCover, type);
//				if(mMediaCover.url != null) {
//					Bitmap bm = null;
//					//文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
//					if (mMediaCover.type == 5) {
//						bm = ThumbnailUtils.createVideoThumbnail(SDCARD_PATH + mMediaCover.url, Thumbnails.MINI_KIND);
//					} else if (mMediaCover.type == 2) {
//						File file = new File(SDCARD_PATH + mMediaCover.url);
//						try {
//							bm = BitmapHelper.getBitmapFromInputStream(new FileInputStream(file));
//						} catch (FileNotFoundException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//					mMediaCover.bm = bm;
//				}
//			}
//		}
//		return mMediaCover;
//	}
    
    public static MediaCover getMediaCover(String uid, String url, int type) {
    	MediaCover mMediaCover = new MediaCover();
    	if (url != null) {
    		mMediaCover.url = url;
    		Bitmap bm = null;
			//文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
			if (type == 5) {
				if (PluginUtils.isPluginMounted()) {
					bm = Mp4InfoUtils.getVideoCapture(url, 384, 512);
				} else {
					bm = ThumbnailUtils.createVideoThumbnail(mMediaCover.url, Thumbnails.MINI_KIND);
				}
			} 
			mMediaCover.bm = bm;
    	}
    	return mMediaCover;
    }
	
	public MediaCover getCoverFromDiary(String uid, MyDiary diary) {
		NetworkTaskInfo info = new NetworkTaskInfo(diary, INetworkTask.TASK_TYPE_UPLOAD);
		UploadNetworkTask task = new UploadNetworkTask(info);
		return getMediaCover(uid, task.info.taskUrl, task.info.diaryType);
	}
	
	public static String getMediaCoverUrl(String videoPath) {
		MediaCover mc = getMediaCover(ActiveAccount.getInstance(ZApplication.getInstance()).getUID(), videoPath, 5);
		if(mc != null && mc.bm != null) {
			byte[] picData = BitmapUtils.bitmapToBytes(mc.bm);
			return writeVideoCoverToSDcard(picData, videoPath);
		}
		return null;
	}
	
	//是否网络图片
	private static boolean isNetworkAddress(String url) {
		if(url.startsWith("http://") || url.startsWith("HTTP://")) {
			Log.d("==WR==", "This is http network url");
			return true;
		}
		return false;
	}
	
	private static MediaCover checkMediaMapping(String uid, String url, MediaCover mc, int type) {
		MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, url);
		//hit and exist, ignore the MediaType
		if(mv != null && MediaValue.checkMediaAvailable(mv, mv.MediaType)) {
			Log.d("==WR==", "Hit Mapping! url = " + mv.path + ", type = " + mv.MediaType);
			mc.url  = mv.path;
			mc.type = mv.MediaType;//文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
		//再次检查url是否已经为本地url
		} else {
			//本地路径
			if(url.startsWith(SDCARD_PATH)) {
				mc.url = url.replace(SDCARD_PATH, "");
				mc.type = type;
			}
		}
		return mc;
	}
	
    private static String writeVideoCoverToSDcard(byte[] data, String key){
    	String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
    	try {
			String path = Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + userID + "/pic/" + MD5.encode(key.getBytes());
			File webimage_file = new File(path);
			if(!webimage_file.getParentFile().exists()) {
				webimage_file.getParentFile().mkdirs();
			}
			FileOutputStream mOutput = new FileOutputStream(webimage_file, false);
			mOutput.write(data);
			mOutput.close();
			Log.d("==WR==", "video cover image path = " + path);
			
//			int rotation = XUtils.getExifOrientation(path); <<<<<<<<<<<<<<
			return path;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
	public static class MediaCover {
		public String url;
		public Bitmap bm;
		public int type;//文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	}
	
}
