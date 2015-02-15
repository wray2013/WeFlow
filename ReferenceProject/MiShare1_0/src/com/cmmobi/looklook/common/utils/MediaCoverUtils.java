package com.cmmobi.looklook.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.core.ZToast;
import cn.zipper.framwork.device.ZScreen;
import cn.zipper.framwork.io.file.ZFileSystem;
import cn.zipper.framwork.utils.ZGraphics;
import cn.zipper.framwork.utils.ZThread;

import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobivideo.utils.Mp4InfoUtils;
import com.cmmobivideo.utils.XUtils;

import effect.Mp4Info;

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
    	MediaCover mediaCover = new MediaCover();
    	mediaCover.url = url;
    	int size = (ZScreen.getWidth() - 5*6) / 3;
    	
    	if (url != null) {
    		Bitmap bitmap = null;
			
			if (type == 5) {//文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
				float offset = 0.5f;
				for(int i=0; i<14; i++) {
//					bitmap = Mp4InfoUtils.getVideoCapture(offset, mediaCover.url, size, size);
					bitmap = XUtils.videScreenCapture(mediaCover.url, offset, size, size);
					ZLog.e("mediaCover.url = " + mediaCover.url);
					if (bitmap == null) {
						offset += 0.1;
						ZThread.sleep(10);
					} else {
						/*int orientation = XUtils.getExifOrientation(url);
						if (orientation != 0) {
							bitmap = ZGraphics.rotate(bitmap, orientation, true);
						}*/
						if((android.os.Build.MODEL.equalsIgnoreCase("GT-N7100") 
								//某台锤子系统的单独适配
								&& !android.os.Build.HOST.equalsIgnoreCase("smartisan"))
								|| android.os.Build.MODEL.equalsIgnoreCase("GT-I9300")
								|| android.os.Build.MODEL.startsWith("GT-I9100")
								|| android.os.Build.MODEL.equalsIgnoreCase("GT-I9220")) {
							Mp4Info info = new Mp4Info(mediaCover.url);
							bitmap = ZGraphics.rotate(bitmap, info.angle, true);
							/*byte[] picData = BitmapUtils.bitmapToBytes(bitmap);
							String temp = writeVideoCoverToSDcard(picData, url);
							
							int angle = XUtils.getExifOrientation(temp);
							bitmap = ZGraphics.rotate(bitmap, angle, true);
							ZToast.showLong(android.os.Build.MANUFACTURER);
							ZFileSystem.delFile(temp);*/
						}
						break;
					}
				}
				
			} else if (type == 2) {
				bitmap = BitmapFactory.decodeFile(url);
				for(int i=0; i<5; i++) {
					bitmap = ThumbnailUtils.extractThumbnail(bitmap, size, size);
					if (bitmap == null) {
						ZThread.sleep(100 + i * 10);
					} else {
						int orientation = XUtils.getExifOrientation(url);
						if (orientation != 0) {
							bitmap = ZGraphics.rotate(bitmap, orientation, true);
						}
						break;
					}
				}
			}
			
			if (bitmap == null) {
				bitmap = ThumbnailUtils.createVideoThumbnail(mediaCover.url, Images.Thumbnails.MICRO_KIND);
				bitmap = ThumbnailUtils.extractThumbnail(bitmap, size, size);
				
				if (bitmap != null) {
					int orientation = XUtils.getExifOrientation(url);
					if (orientation != 0) {
						bitmap = ZGraphics.rotate(bitmap, orientation, true);
					}
				}
			}
			
			mediaCover.bm = bitmap;
    	}
    	return mediaCover;
    }
	
//	public MediaCover getCoverFromDiary(String uid, MyDiary diary) {
//		NetworkTaskInfo info = new NetworkTaskInfo(diary, INetworkTask.TASK_TYPE_UPLOAD);
//		UploadNetworkTask task = new UploadNetworkTask(info);
//		return getMediaCover(uid, task.info.taskUrl, task.info.diaryType);
//	}
	
	public static MediaCover getMediaCover(String videoPath, int type) {
		MediaCover mc = getMediaCover(ActiveAccount.getInstance(ZApplication.getInstance()).getUID(), videoPath, type);
		if(mc != null && mc.bm != null) {
			byte[] picData = BitmapUtils.bitmapToBytes(mc.bm);
			mc.url = writeVideoCoverToSDcard(picData, videoPath);
			return mc;
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
			Log.d("==WR==", "Hit Mapping! url = " + mv.localpath + ", type = " + mv.MediaType);
			mc.url  = mv.localpath;
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
			String path = Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + userID + "/pic/" + MD5.encode(key.getBytes()) + ".vc";
			File webimage_file = new File(path);
			if(!webimage_file.getParentFile().exists()) {
				webimage_file.getParentFile().mkdirs();
			}
			FileOutputStream mOutput = new FileOutputStream(webimage_file, false);
			mOutput.write(data);
			mOutput.close();
			Log.d("==WR==", "video cover image path = " + path);
			
			return path;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
	public static class MediaCover {
		public String url; // 以前这个值表示媒体路径, 现在改为表示封面路径;
		public Bitmap bm;
		public int type;//文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	}
	
}
