package com.cmmobi.looklook.common.gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.file.ZFileSystem;
import cn.zipper.framwork.utils.ZArrayUtils;
import cn.zipper.framwork.utils.ZGraphics;
import cn.zipper.framwork.utils.ZStringUtils;

import com.cmmobi.common.net.NetworkTypeUtility;
import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonRequest3.Attachs;
import com.cmmobi.looklook.common.gson.GsonResponse3.AuxAttach;
import com.cmmobi.looklook.common.gson.GsonResponse3.DiaryAttach;
import com.cmmobi.looklook.common.gson.GsonResponse3.MainAttach;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyAttach;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.gson.GsonResponse3.createStructureResponse;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.MediaCoverUtils;
import com.cmmobi.looklook.common.utils.MediaCoverUtils.MediaCover;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.info.weather.MyWeather;
import com.cmmobi.looklook.networktask.CacheNetworkTask;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobi.looklook.networktask.NetworkTaskManager;
import com.cmmobi.looklook.networktask.UploadNetworkTask;
import com.cmmobivideo.utils.Mp4InfoUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.utils.XUtils;


/**
 * 日记结构创建器, 用来向服务器请求数据,并将响应结果映射成日记结构(MyDiary);
 * 
 * @author lizhiguo;
 */
public final class DiaryController implements Callback {
	
	public static final int DIARY_REQUEST_DONE = 0xFF00000A; //日记请求结束后, 会通过handler回调这个命令;
	public static final int CREATE_DIARY = 0xFF00000B; //创建日记;
	public static enum FileOperate {RENAME, COPY};
	
	
	public static class DiaryWrapper {
		
		private Handler handler; // 外部handler, 用来回调数据;
		private boolean isRequestDone; // 上传日记结构的请求得到成功响应;
		private boolean isContentReady; // 日记内容已经生成 (拍摄完成, 拍照完成);
		private boolean isMicroShare; // 与GsonRequester3.createStructureRequest.isonlymic同义;
		
		public boolean isDiaryOK; // 日记是否工作完毕(通信成功和通信失败都算完毕)
		public GsonRequest3.createStructureRequest request;
		public GsonResponse3.createStructureResponse response;
		public MyDiary diary;
		public MediaCover cover;
		public String videoCoverUrl;
		public String playtime;
		
		private DiaryWrapper() {
		}
	}
	
	private static DiaryController controller = new DiaryController();
	private static HashMap<String, DiaryWrapper> diaryWrappers;
	private static HandlerThread thread;
	private static Handler handler;
	
	
	private DiaryController() { // 禁止构造;
		diaryWrappers = new HashMap<String, DiaryController.DiaryWrapper>();
		thread = new HandlerThread("");
		thread.start();
		handler = new Handler(thread.getLooper(), this);
	}
	
	public static DiaryController getInstanse() {
		return controller;
	}
	
	
	/**
	 * 
	 * @param handler: 回调导入结果的handler;
	 * @param diaryUUID
	 * @param attachUUID
	 * @param attachPath: 媒体附件的路径;
	 * @param attachType: 附件的类型;
	 * @param attachSuffix: 附件的扩展名 (GsonProtocol.SUFFIX_XXXX);
	 * @param content
	 * @param playtime
	 * @param tags
	 * @param position_status
	 * @param isIncludeMicroShare: 导入的是否是微享;
	 * @param fileOperate
	 * @return
	 */
	public synchronized DiaryWrapper includeDiary(
			Handler handler, 
			String diaryUUID, 
			String attachUUID, 
			String attachPath, 
			String attachType, 
			String attachSuffix, 
			String content, 
			String playtime, 
			String tags, 
			String longitude_real,
			String latitude_real,
			String position_real,
			String position_status, 
			boolean isMicroShare, 
			FileOperate fileOperate, 
			String shoot_time) {
		
		boolean b = true;
		DiaryWrapper wrapper = null;
		
		if (!isTextType(attachType)) {
			
			if (ZFileSystem.isFileExists(attachPath)) {
				String path = getFullPathByType(attachType, attachUUID, attachSuffix);
				
				renameOrCopy(fileOperate, attachPath, path);
				
				if (isPictureType(attachType)) {
					try {
						Bitmap bitmap = BitmapFactory.decodeFile(path, null);
						bitmap = ZGraphics.rotate(bitmap, XUtils.getExifOrientation(path), true);
						
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);
						byte[] bytes = os.toByteArray();
						
						FileOutputStream mOutput = new FileOutputStream(path, false);
						mOutput.write(bytes);
						mOutput.close();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				b = false;
				wrapper = new DiaryWrapper();
				wrapper.handler = handler;
				sendMessage(wrapper, true);
			}
		}
		
		if (b) {
			String isMicroShareString = isMicroShare
					? GsonProtocol.IS_ONLY_MICROSHARE_TRUE 
					: GsonProtocol.IS_ONLY_MICROSHARE_FALSE;
			
			shoot_time = translateShootTime(shoot_time, null);
			
			wrapper = requestNewDiary(
					handler, 
					diaryUUID, 
					attachUUID, 
					attachType, 
					attachSuffix, 
					content, 
					tags, 
					isMicroShareString, 
					longitude_real,
					latitude_real,
					position_real,
					position_status, 
					shoot_time);
			
			wrapper.playtime = playtime;
			wrapper.isMicroShare = isMicroShare;
			diaryContentIsReady(diaryUUID);
		}
		
		return wrapper;
	}
	
	/**
	 * 请求新建日记结构 (在日记内容开始创建时调用此函数, 如: 开始录音后, 开始拍摄后, 开始拍照后);
	 * @param handler
	 * @param diaryUUID
	 * @param attachUUID
	 * @param attachType: 附件的类型;
	 * @param attachSuffix: 附件的扩展名 (GsonProtocol.SUFFIX_XXXX);
	 * @param content
	 * @return
	 */
	public synchronized DiaryWrapper requestNewDiary(
			Handler handler, 
			String diaryUUID, 
			String attachUUID, 
			String attachType, 
			String attachSuffix, 
			String content, 
			String tags, 
			String isonlymic,
			String longitude_real,
			String latitude_real,
			String position_real,
			String position_status, 
			String shoot_time) {
		
		shoot_time = translateShootTime(shoot_time, null);
		
		Attachs attach = createNewAttach(
				GsonProtocol.EMPTY_VALUE, 
				attachUUID, 
				attachType, 
				attachSuffix, 
				GsonProtocol.ATTACH_LEVEL_MAIN, 
				GsonProtocol.ATTACH_OPERATE_TYPE_ADD, 
				content, 
				null);

		Attachs[] attachs = new Attachs[1];
		attachs[0] = attach;
		
		return requestDiary(
				handler, 
				GsonProtocol.EMPTY_VALUE, 
				diaryUUID, 
				GsonProtocol.DIARY_OPERATE_TYPE_NEW, 
				GsonProtocol.EMPTY_VALUE, 
				GsonProtocol.EMPTY_VALUE, 
				tags, 
				isonlymic, 
				longitude_real,
				latitude_real,
				position_real,
				getLongitude(),
				getLatitude(),
				getPositionString1(),
				longitude_real,
				latitude_real,
				position_real,
				position_status,
				attachs, 
				false, 
				shoot_time);
	}
	
	public synchronized DiaryWrapper updateDiaryByID(
			Handler handler, 
			String diaryID, 
			Attachs[] attachs, 
			String tags, 
			String longitude_view,
			String latitude_view,
			String position_view,
			String position_status, 
			String shoot_time) {
		
		return updateDiary(
				handler, 
				DiaryManager.getInstance().findMyDiaryByDiaryID(diaryID), 
				attachs, 
				tags, 
				longitude_view,
				latitude_view,
				position_view,
				position_status, 
				shoot_time);
	}
	
	/**
	 * 更新日记结构;
	 * @param handler
	 * @param diary
	 * @param attachs
	 */
	public synchronized DiaryWrapper updateDiary(
			Handler handler, 
			MyDiary diary, 
			Attachs[] attachs, 
			String tags,
			String longitude_view,
			String latitude_view,
			String position_view,
			String position_status, 
			String shoot_time) {
		
		shoot_time = translateShootTime(shoot_time, diary);
		if (diary != null) {
			String diaryUUID = diary.diaryuuid;
			MyDiary localDiary = DiaryManager.getInstance().findMyDiaryByUUID(diaryUUID);
			if (localDiary != null) {
				diary = localDiary;
			}
		}
		boolean isNeedCreate = !diary.isCreated();
		
		if (diary.request != null) {
			attachs = mergeAttachs(diary.request.attachs, attachs);
		}
		
		DiaryWrapper wrapper = requestDiary(
				handler, 
				diary.diaryid, 
				diary.diaryuuid, 
				isNeedCreate ? GsonProtocol.DIARY_OPERATE_TYPE_NEW : GsonProtocol.DIARY_OPERATE_TYPE_UPDATE, 
				diary.diaryid, 
				diary.diaryuuid, 
				tags, 
				GsonProtocol.IS_ONLY_MICROSHARE_FALSE, 
				longitude_view,
				latitude_view,
				position_view,
				getLongitude(),
				getLatitude(),
				getPositionString1(),
				longitude_view,
				latitude_view,
				position_view,
				position_status,
				attachs, 
				true, 
				shoot_time);
		
		diaryContentIsReady(diary.diaryuuid);
		
		return wrapper;
	}
	
	
	public synchronized DiaryWrapper savaAsDiary(
			Handler handler, 
			String originalDiaryUUID, 
			Attachs[] attachs, 
			String tags, 
			String longitude_real,
			String latitude_real,
			String position_real,
			String coverUrl, 
			String position_status, 
			FileOperate fileOperate, 
			String shoot_time) {
		
		ZLog.e();
		
		Vector<Attachs> buffer = new Vector<Attachs>();
		
		if (attachs != null) {
			for (Attachs attach : attachs) {
				
				Attachs temp = createNewAttach(
						GsonProtocol.EMPTY_VALUE, 
						attach.attachuuid, 
						attach.attach_type, 
						attach.suffix, 
						GsonProtocol.ATTACH_LEVEL_MAIN, 
						GsonProtocol.ATTACH_OPERATE_TYPE_ADD, 
						attach.content, 
						attach.filepath);
				buffer.add(temp);
				
				String newPath = getFullPathByType(attach.attach_type, attach.attachuuid, attach.suffix);
				renameOrCopy(fileOperate, attach.filepath, newPath);
			}
		}
		
		MyDiary originalDiary = DiaryManager.getInstance().findMyDiaryByUUID(originalDiaryUUID);
		shoot_time = translateShootTime(shoot_time, originalDiary);
		
		if (originalDiary.attachs.attach != null) {
			for (AuxAttach auxAttach : originalDiary.attachs.attach) {
				
				String attachUUID = getNextUUID();
				String path = originalDiary.getAuxAttachPath(auxAttach.attachuuid);
				String suffix = getSuffix(path);
				
				Attachs temp = createNewAttach(
						GsonProtocol.EMPTY_VALUE, 
						attachUUID, 
						auxAttach.attachtype, 
						suffix, 
						GsonProtocol.ATTACH_LEVEL_SUB, 
						GsonProtocol.ATTACH_OPERATE_TYPE_ADD, 
						auxAttach.content, 
						path);
				buffer.add(temp);
				
				if (path != null) {
					String newPath = getFullPathByType(auxAttach.attachtype, attachUUID, suffix);
					renameOrCopy(fileOperate, path, newPath);
				}
			}
		}
		
		String newDiaryUUID = getNextUUID();
		Attachs[] newAttachs = buffer.toArray(new Attachs[0]);
		
		DiaryWrapper wrapper = requestDiary(
				handler, 
				GsonProtocol.EMPTY_VALUE, 
				newDiaryUUID, 
				GsonProtocol.DIARY_OPERATE_TYPE_COPY, 
				originalDiary.diaryid, 
				originalDiary.diaryuuid, 
				tags, 
				GsonProtocol.IS_ONLY_MICROSHARE_FALSE, 
				longitude_real,
				latitude_real,
				position_real,
				getLongitude(),
				getLatitude(),
				getPositionString1(),
				longitude_real,
				latitude_real,
				position_real,
				position_status,
				newAttachs, 
				false, 
				shoot_time);
		
		wrapper.videoCoverUrl = coverUrl;
		
		diaryContentIsReady(newDiaryUUID);
		
		return wrapper;
	}
	
	/**
	 * 日记内容准备好后需调用此函数 (如录音完毕, 拍摄完毕);
	 * @param diaryUUID
	 */
	public synchronized DiaryWrapper diaryContentIsReady(String diaryUUID) {
		DiaryWrapper wrapper = findWrapper(diaryUUID);
		
		createCover(wrapper);
		wrapper.isContentReady = true;
		
		Message message = new Message();
		message.what = CREATE_DIARY;
		message.obj = wrapper;
		handler.sendMessage(message);
		
		return wrapper;
	}
	
	
	/**
	 * 请求日记操作;
	 * @param handler: 回调请求结果的handler;
	 * @param diaryID: 要操作的日记的ID;
	 * @param diaryUUID: 要操作的日记的UUID;
	 * @param operateType: 操作类型;
	 * @param originalDiaryID: 原始日记的ID;
	 * @param originalDiaryUUID: 原始日记的UUID;
	 * @param tags: 标签列表;
	 * @param attachs: 附件数组;
	 * @return
	 */
	public DiaryWrapper requestDiary(
			Handler handler, 
			String diaryID, 
			String diaryUUID, 
			String operateType, 
			String originalDiaryID, 
			String originalDiaryUUID, 
			String tags, 
			String isonlymic, 
			String longitude_real,
			String latitude_real,
			String position_real,
			String longitude,
			String latitude,
			String position,
			String longitude_view,
			String latitude_view,
			String position_view,
			String position_status,
			Attachs[] attachs, 
			boolean keepCreateTimeMillis, 
			String shoot_time) {
		
		MyDiary diary = DiaryManager.getInstance().findMyDiaryByUUID(diaryUUID);
		String timeString = getTimeString();
		
		shoot_time = translateShootTime(shoot_time, diary);
		
		if (diary != null && keepCreateTimeMillis) {
			timeString = diary.diarytimemilli;
		}
		
		GsonRequest3.createStructureRequest request = Requester3.createStructure(
				DiaryController.handler, 
				diaryID, // diaryID;
				diaryUUID, // diaryUUID;
				operateType, // 日记操作类型: GsonProtocol.DIARY_OPERATE_TYPE_XXX;
				originalDiaryID, // 原diaryID;
				originalDiaryUUID, // 原diaryUUID;
				tags, // 标签列表;
				timeString, // 创建时间;
				getAddrCode(), // 国际地址码;
				position_status, // 位置是否可见
				isonlymic, 
				longitude_real,
				latitude_real,
				position_real,
				longitude,
				latitude,
				position,
				longitude_view,
				latitude_view,
				position_view,
				shoot_time, 
				attachs); // 附件组;
		
		DiaryWrapper wrapper = new DiaryWrapper();
		wrapper.request = request;
		wrapper.handler = handler;
		wrapper.diary = diary;
		
		holdWrapper(diaryUUID, wrapper);

		return wrapper;
	}
	
	/**
	 * 根据服务器响应来合并数据, 并生成diary对象, 固化到MediaMapping中;
	 * @param wrapper
	 */
	private void createDiary(final DiaryWrapper wrapper) {
		
		if (!wrapper.isDiaryOK && wrapper.isContentReady && wrapper.isRequestDone) {
			
			Attachs[] requestAttachs = wrapper.request.attachs;
			DiaryAttach diaryAttach = getDiaryAttach(wrapper.request.diaryuuid);
			
			if (requestAttachs != null) {
				for (int i=0; i<requestAttachs.length; i++) {
					// 准备合并;
					Attachs requestAttach = requestAttachs[i];
					String attachID = requestAttach.attachid;
					String attachUUID = requestAttach.attachuuid;
					String attachPath = "file://" + requestAttach.attachuuid;
					
					// 合并;
					if (isSuccessfulResponse(wrapper.response)) {
						requestAttach.issynchronized = true;
						
						if (!ZArrayUtils.isEmpty(wrapper.response.attachs) && i < wrapper.response.attachs.length) {
							MyAttach attach = wrapper.response.attachs[i];
							attachID = attach.attachid;
							attachUUID = attach.attachuuid;
							attachPath = attach.path;
						}
					}
					mergeAttachToDiaryAttach(wrapper, diaryAttach, requestAttach, attachID, attachUUID, attachPath);
					
					// 映射/反映射;
					String absolutePath = getFullPathByType(requestAttach.attach_type, attachUUID, requestAttach.suffix);
					if (!isTextType(requestAttach)) {
						if (requestAttach.isDeleteOperate()) {
							deleteFromMediaMapping(attachPath);
							ZFileSystem.delFile(absolutePath);
							
						} else if (requestAttach.isUpdateOperate()) {
							if (!TextUtils.isEmpty(requestAttach.filepath) && !requestAttach.filepath.equals(absolutePath)) {
								ZFileSystem.delFile(absolutePath);
							}
							
							if (TextUtils.isEmpty(requestAttach.filepath)) {
								deleteFromMediaMapping(attachPath);
							} else {
								ZFileSystem.renameTo(requestAttach.filepath, absolutePath);
								if (requestAttach.suffix.equals(GsonProtocol.SUFFIX_MP4)) {
									transformMp4Header(absolutePath, absolutePath + ".bak");
								}
								saveToMediaMapping(requestAttach.attach_type, absolutePath, attachPath);
							}
							
						} else {
							if (!ZFileSystem.isFileExists(absolutePath) && !TextUtils.isEmpty(requestAttach.filepath)) {
								ZFileSystem.renameTo(requestAttach.filepath, absolutePath);
							}
							if (requestAttach.suffix != null && requestAttach.suffix.equals(GsonProtocol.SUFFIX_MP4)) {
								transformMp4Header(absolutePath, absolutePath + ".bak");
							}
							saveToMediaMapping(requestAttach.attach_type, absolutePath, attachPath);
						}
					}
				}
			}
			
			final MyDiary diary = createNewDiary(wrapper, diaryAttach);
			final MyDiaryList diaryList = createMyDiaryList(diary);
			
			if (isSuccessfulResponse(wrapper.response)) {
				diary.sync_status = GsonProtocol.DIARY_SYNC_STATUS_STRUCTURE_CREATED;
			} else {
				diary.sync_status = GsonProtocol.DIARY_SYNC_STATUS_NOT_SYNC;
			}
			
			if (wrapper.cover != null) {
				if (!wrapper.cover.url.toLowerCase().startsWith("file://")) {
					wrapper.cover.url = "file://" + wrapper.cover.url;
				}
				diary.attachs.videocover = wrapper.cover.url;
			}
			
			diaryWrappers.remove(diary.diaryuuid);
			
			if (!wrapper.isDiaryOK) {
				wrapper.isDiaryOK = true;
				
				wrapper.handler.post(new Runnable() {
					
					@Override
					public void run() {
						wrapper.diary = diary;
						ZLog.printStarLine();
						ZLog.printObject(diary);
						ZLog.printStarLine();
						ZLog.printObject(diary.shoottime);
						ZLog.printStarLine();
						ZLog.printObject(diary.tags);
						
						if (!wrapper.isMicroShare) {
							DiaryManager.getInstance().saveDiaries(diaryList, diary);
							addToNetworkTaskManager(wrapper);
							// 埋点
							// 2014-4-8
							String addrCode = CommonInfo.getInstance().addressCode;
							String netstyle = CmmobiClickAgentWrapper.getNetworkLabel(
									NetworkTypeUtility.getNetwork(MainApplication.getAppInstance()));
							Log.d("==WR==", "【埋点】地区码:" + addrCode + ";网络类型:" + netstyle);
							// 1.上传地区
//							HashMap<String, String> area_hm = new HashMap<String, String>();
//							area_hm.put("label", ZStringUtils.nullToEmpty(addrCode));
							CmmobiClickAgentWrapper.onEvent(MainApplication.getAppInstance(), "up_area", ZStringUtils.nullToEmpty(addrCode));
							// 2.上传网络类型
//							HashMap<String, String> style_hm = new HashMap<String, String>();
//							style_hm.put("label", ZStringUtils.nullToEmpty(netstyle));
							CmmobiClickAgentWrapper.onEvent(MainApplication.getAppInstance(), "upload_content", ZStringUtils.nullToEmpty(netstyle));
						}
						
						sendMessage(wrapper, false);
					}
				});
			}
		}
	}
	
	/**
	 * 转换MP4头, 只修改媒体文件, 不操作mediamapping;
	 * @param originalPath
	 * @param newPath
	 */
	private void transformMp4Header(String originalPath, String newPath) {
		if (PluginUtils.isPluginMounted() && XUtils.MoovIsPre(originalPath) == 0) {
			XUtils.PreMoov(originalPath, newPath);
			ZFileSystem.delFile(originalPath);
			ZFileSystem.renameTo(newPath, originalPath);
		}
	}
	
	private void sendMessage(DiaryWrapper wrapper, boolean nullResponse) {
		Message message = new Message();
		message.what = DIARY_REQUEST_DONE;
		if (!nullResponse) {
			message.obj = wrapper;
		}
		wrapper.handler.sendMessage(message);
	}
	
	@Override
	public boolean handleMessage(Message message) {
		DiaryWrapper wrapper = null;
		
		switch (message.what) {
		case Requester3.RESPONSE_TYPE_CREATE_STRUCTURE:
			GsonResponse3.createStructureResponse response = (createStructureResponse) message.obj;
			
			wrapper = findWrapper(response.diaryuuid);
			wrapper.isRequestDone = true;
			wrapper.response = response;
			
			createDiary(wrapper);
			break;
			
		case CREATE_DIARY:
			wrapper = (DiaryWrapper) message.obj;
			createDiary(wrapper);
			break;
		}
		return false;
	}
	
	/**
	 * 构造一个Attach对象;
	 * @param attachID
	 * @param attachUUID
	 * @param attachType
	 * @param attachSuffix
	 * @param level
	 * @param operateType
	 * @param content
	 * @return
	 */
	public Attachs createNewAttach(
			String attachID, 
			String attachUUID, 
			String attachType, 
			String attachSuffix, 
			String level, 
			String operateType, 
			String content, 
			String filepath) {

		Attachs attach = new Attachs();
		attach = new Attachs();
		attach.issynchronized = false;
		attach.attachid = attachID;
		attach.attachuuid = attachUUID;
		attach.attach_type = attachType;
		attach.attach_latitude = getLatitude();
		attach.attach_longitude = getLongitude();
		attach.level = level;
		attach.content = content;
		attach.suffix = attachSuffix;
		attach.Operate_type = operateType;
		attach.filepath = filepath;

		return attach;
	}
	
	private void createCover(DiaryWrapper wrapper) {
		if(wrapper == null || wrapper.request == null) {
			return;
		}
		Attachs attach = getMainAttach(wrapper.request.attachs);
		
		if (wrapper.videoCoverUrl != null) {
			createVideoCover(wrapper, wrapper.videoCoverUrl);
			return;
		}
		
		if (attach != null) {
			boolean isVideoType = isVideoType(attach);
			boolean isPictureType = isPictureType(attach);
			
			if (isVideoType || isPictureType) {
				int type = 0;
				
				if (isVideoType) {
					type = 5;
				} else if (isPictureType) {
					type = 2;
				}
				
				String mediaAbsolutePath = getFullPathByType(attach.attach_type, attach.attachuuid, attach.suffix);
				wrapper.cover = MediaCoverUtils.getMediaCover(mediaAbsolutePath, type);
				
				if (wrapper.cover != null) {
					saveToMediaMapping(GsonProtocol.ATTACH_TYPE_PICTURE, wrapper.cover.url, "file://" + wrapper.cover.url);
				}
			}
		}
	}
	
	private void createVideoCover(DiaryWrapper wrapper, String url) {
		if (!url.toLowerCase().startsWith("file://")) {
			url = "file://" + url;
		}
		
		MediaCover mediaCover = new MediaCover();
		mediaCover.type = 5;
		mediaCover.url = url;
		wrapper.cover = mediaCover;
	}
	
	private Attachs getMainAttach(Attachs[] attachs) {
		Attachs main = null;
		
		if (attachs != null) {
			for (Attachs attach : attachs) {
				if (attach.isMainAttach()) {
					main = attach;
					break;
				}
			}
		}
		
		return main;
	}
	
	/**
	 * 将合并后的日记对象发送给NetworkTaskManager, 用以启动上传任务;
	 * @param wrapper
	 * @param diary
	 */
	private void addToNetworkTaskManager(DiaryWrapper wrapper) {
		INetworkTask task = null;
		
		if(isSuccessfulResponse(wrapper.response)) {
			NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(wrapper.diary, INetworkTask.TASK_TYPE_UPLOAD); // 设置数据源;
			task = new UploadNetworkTask(networktaskinfo); // 创建上传任务;
		} else {
			NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(wrapper.diary, INetworkTask.TASK_TYPE_CACHE); // 设置数据源;
			task = new CacheNetworkTask(networktaskinfo); // 创建上传任务;
		}
		
		if(!"".equals(ZStringUtils.nullToEmpty(getUID()))) {
			NetworkTaskManager networkmanager = NetworkTaskManager.getInstance(getUID());
			if(networkmanager != null) {
				networkmanager.addTask(task); // 添加网络任务;
				networkmanager.startNextTask(); // 开始任务队列;
			} else {
				ZLog.e("NetworkTaskManager is null");
			}
		} else {
			ZLog.e("getUID is null");
		}
	}
	
	/**
	 * 构造日记实例;
	 * @param wrapper
	 * @param diaryAttachs
	 * @return
	 */
	private MyDiary createNewDiary(DiaryWrapper wrapper, DiaryAttach diaryAttach) {
		AccountInfo account = getAccountInfo();
		
		MyDiary diary = new MyDiary();
		diary.active = null;
		diary.attachs = diaryAttach;
		diary.diaryid = wrapper.response.diaryid;
		diary.diarytimemilli = wrapper.request.createtime;
		diary.diaryuuid = wrapper.response.diaryuuid;
		diary.headimageurl = account.headimageurl;
		diary.nickname = account.nickname;
		diary.position_status = "0".equals(wrapper.request.position_status)?"0":"1" ;
		diary.request = wrapper.request;
		diary.sex = account.sex;
		
		diary.signature = account.signature;
		diary.updatetimemilli = getTimeString();
		diary.isMicroShare = wrapper.isMicroShare;
		diary.userid = getUID();
		diary.setDiaryTag(wrapper.request.tags);
		
		MyWeather weather = account.myWeather;
		if (weather != null && weather.desc != null && weather.desc.length > 0) {
			diary.weather = weather.desc[0].weatherurl;
			diary.weather_info = weather.desc[0].description;
		}
		
		if (!TextUtils.isEmpty(wrapper.request.shoottime)) {
			diary.shoottime = wrapper.request.shoottime;
			
		} else if (wrapper.diary != null) {
			diary.shoottime = wrapper.diary.shoottime;
			
		} else {
			diary.shoottime = String.valueOf(TimeHelper.getInstance().now());
		}
		
		replacePosition(diary, wrapper);
		
		if (wrapper.diary != null) {
			diary.sync_status = wrapper.diary.sync_status;
			diary.birthday = wrapper.diary.birthday;
			diary.diary_status = wrapper.diary.diary_status;
			diary.join_safebox = wrapper.diary.join_safebox;
			diary.offset = wrapper.diary.offset;
			diary.platformurls = wrapper.diary.platformurls;
			diary.position_source = wrapper.diary.position_source;
			diary.resourcediaryid = wrapper.request.resourcediaryid;
			diary.resourceuuid = wrapper.request.resourcediaryuuid;
			diary.share_count = wrapper.diary.share_count;
			diary.shareimageurl = wrapper.diary.shareimageurl;
			diary.shareinfo = wrapper.diary.shareinfo;
			diary.publishid = wrapper.diary.publishid;
			diary.publish_status = wrapper.diary.publish_status;
			
		} else {
			diary.birthday = GsonProtocol.EMPTY_VALUE;
			diary.diary_status = GsonProtocol.DIARY_STATUS_NEW;
			diary.join_safebox = GsonProtocol.JOIN_SAFEBOX_STATUS_NO;
			diary.offset = GsonProtocol.ZERO_VALUE;
			diary.platformurls = null;
			diary.position_source = GsonProtocol.EMPTY_VALUE;
			diary.resourcediaryid = GsonProtocol.EMPTY_VALUE;
			diary.resourceuuid = GsonProtocol.EMPTY_VALUE;
			diary.share_count = GsonProtocol.ZERO_VALUE;
			diary.shareimageurl = GsonProtocol.EMPTY_VALUE;
		}
		
		return diary;
	}
	
	private void replacePosition(MyDiary diary, DiaryWrapper wrapper) {
		if (wrapper.request.longitude_view != null && !"".equals(wrapper.request.longitude_view)) {
			diary.longitude_view = wrapper.request.longitude_view;
		} else if (wrapper.diary != null) {
			diary.longitude_view = wrapper.diary.longitude_view;
		} else {
			diary.longitude_view = "";
		}
		
		if (wrapper.request.latitude_view != null && !"".equals(wrapper.request.latitude_view)) {
			diary.latitude_view = wrapper.request.latitude_view;
		} else if (wrapper.diary != null) {
			diary.latitude_view = wrapper.diary.latitude_view;
		} else {
			diary.latitude_view = "";
		}
		
		if (wrapper.request.position_view != null && !"".equals(wrapper.request.position_view)) {
			diary.position_view = wrapper.request.position_view;
		} else if (wrapper.diary != null) {
			diary.position_view = wrapper.diary.position_view;
		} else {
			diary.position_view = "";
		}
	}
	
	private DiaryAttach getDiaryAttach(String diaryUUID) {
		DiaryAttach diaryAttach = null;
		
		MyDiary diary = DiaryManager.getInstance().findMyDiaryByUUID(diaryUUID);
		
		if (diary != null) {
			diaryAttach = diary.attachs;
		} else {
			diaryAttach = new DiaryAttach();
			diaryAttach.pic_width = GsonProtocol.EMPTY_VALUE;
			diaryAttach.pic_height = GsonProtocol.EMPTY_VALUE;
			diaryAttach.show_width = GsonProtocol.EMPTY_VALUE;
			diaryAttach.show_height = GsonProtocol.EMPTY_VALUE;
			diaryAttach.videocover = GsonProtocol.EMPTY_VALUE;
		}
		
		return diaryAttach;
	}
	
	/**
	 * 用单个日记生成一个日记组;
	 * @param diary
	 * @return
	 */
	private MyDiaryList createMyDiaryList(MyDiary diary) {
		MyDiaryList diaryList = DiaryManager.getInstance().findDiaryGroupByUUID(diary.diaryuuid);
		if (diaryList == null) {
			diaryList = new MyDiaryList();
		}
		diaryList.diaryid = diary.diaryid;
		diaryList.diaryuuid = diary.diaryuuid;
		diaryList.isgroup = "0";
		diaryList.join_safebox = diary.join_safebox;
		diaryList.create_time = diary.diarytimemilli;
		diaryList.update_time = diary.updatetimemilli;
		
		return diaryList;
	}
	
	
	/**
	 * 根据服务器响应的attach来构造日记attach实例;
	 * @param attach
	 * @param attachID
	 * @param attachUUID
	 * @param attachPath
	 * @param absolutePath
	 * @param videoCoverKey
	 * @return
	 */
	private void mergeAttachToDiaryAttach(
			DiaryWrapper wrapper, 
			DiaryAttach diaryAttach,
			Attachs attach, 
			String attachID, 
			String attachUUID, 
			String attachPath) {
		
		if (attach.level.equals(GsonProtocol.ATTACH_LEVEL_MAIN)) {
			diaryAttach.levelattach = new MainAttach();
			diaryAttach.levelattach.attachid = attachID;
			diaryAttach.levelattach.attachuuid = attachUUID;
			diaryAttach.levelattach.attachsize = GsonProtocol.ZERO_VALUE;
			diaryAttach.levelattach.attachtimemilli = getTimeString();
			diaryAttach.levelattach.attachtype = attach.attach_type;
//			if (isPictureType(attach)) {
//				diaryAttach.levelattach.attachurl_pic = attachPath;
//			} else {
//			}
			diaryAttach.levelattach.attachurl = attachPath;
			diaryAttach.levelattach.content = attach.content;
			diaryAttach.levelattach.playtime = getPlaytime(wrapper, attach);
			diaryAttach.levelattach.playtimes = GsonProtocol.ZERO_VALUE;
			diaryAttach.levelattach.subattachid = GsonProtocol.EMPTY_VALUE;
			
		} else {
			HashMap<String, AuxAttach> auxAttachs = new HashMap<String, AuxAttach>();
			
			if (diaryAttach.attach != null) {
				for (AuxAttach temp : diaryAttach.attach) {
					auxAttachs.put(temp.attachuuid, temp);
				}
			}
			
			if (attach.isDeleteOperate() && (attach.isSynchronized() || TextUtils.isEmpty(attach.attachid))) {
				auxAttachs.remove(attach.attachuuid);
				
			} else {
				AuxAttach auxAttach = new AuxAttach();
				auxAttach.attachid = attachID;
				auxAttach.attachuuid = attachUUID;
				auxAttach.attachsize = GsonProtocol.ZERO_VALUE;
				auxAttach.attachtimemilli = getTimeString();
				auxAttach.attachtype = attach.attach_type;
				auxAttach.attachurl = attachPath;
				auxAttach.content = attach.content;
				auxAttach.playtime = getPlaytime(wrapper, attach);
				auxAttach.playtimes = GsonProtocol.ZERO_VALUE;
				auxAttach.subattachid = GsonProtocol.EMPTY_VALUE;
				
				auxAttachs.put(auxAttach.attachuuid, auxAttach);
			}
			
			diaryAttach.attach = auxAttachs.values().toArray(new AuxAttach[0]);
		}
	}
	
	/**
	 * 检测是否所有日记已经走完流程 (通信失败,也算走完流程);
	 * @return
	 */
	public boolean isAllDiaryOK() {
		return diaryWrappers.size() == 0;
	}
	
	public int getWrapperSize() {
		return diaryWrappers.size();
	}
	
//	private boolean isNeedRequest(Attachs[] attachs) {
//		boolean b = false;
//		
//		if (attachs != null) {
//			for (Attachs attach : attachs) {
//				if (!attach.issynchronized) {
//					b = true;
//					break;
//				}
//			}
//		} else {
//			b = true;
//		}
//		
//		return b;
//	}
	
	private String getPlaytime(DiaryWrapper wrapper, Attachs attach) {
		String wrapperPlaytime = wrapper == null ? GsonProtocol.EMPTY_VALUE : wrapper.playtime;
		String playtime = GsonProtocol.EMPTY_VALUE;
		
		if (isVideoType(attach) || isAudioType(attach) || isVoiceType(attach)) {
			
			if (TextUtils.isEmpty(wrapperPlaytime)) {
				
				String absolutePath = null;
				
				if (!TextUtils.isEmpty(attach.filepath)) {
					absolutePath = attach.filepath;
				} else {
					absolutePath = getFullPathByType(attach.attach_type, attach.attachuuid, attach.suffix);
				}
				
				double total = new Mp4InfoUtils(absolutePath).totaltime;
				
				if (total < 1.0) {
					absolutePath = getFullPathByType(attach.attach_type, attach.attachuuid, attach.suffix);
					total = new Mp4InfoUtils(absolutePath).totaltime;
				}
				
				if (total < 1.0) {
					total = 1.0;
				}
				playtime = String.valueOf((int) (total));
				
			} else {
				playtime = wrapperPlaytime;
			}
		}
		
		return playtime;
	}
	
	/**
	 * 将attachsNew的元素合并到attachsOld中 (遇到相同attachUUID则覆盖attachsOld中的旧元素, 否则追加);
	 * @param attachsOld
	 * @param attachsNew
	 * @return
	 */
	private Attachs[] mergeAttachs(Attachs[] attachsOld, Attachs[] attachsNew) {
		HashMap<String, Attachs> hashMap = new HashMap<String, Attachs>();
		HashMap<String, Attachs> attachsOldHashMap = new HashMap<String, Attachs>();
		
		if (attachsOld != null) {
			for (Attachs attach : attachsOld) {
				if (!attach.issynchronized) {
					hashMap.put(attach.attachuuid, attach);
				}
				attachsOldHashMap.put(attach.attachuuid, attach);
			}
		}
		
		if (attachsNew != null) {
			for (Attachs attach : attachsNew) {
				if (attach.isDeleteOperate() 
						&& hashMap.containsKey(attach.attachuuid) 
						&& TextUtils.isEmpty(hashMap.get(attach.attachuuid).attachid)) {
					hashMap.remove(attach.attachuuid);
					
				} else {
					boolean flag = true;
					if (attachsOldHashMap.containsKey(attach.attachuuid) 
							&& attachsOldHashMap.get(attach.attachuuid).issynchronized) {
						flag = false;
					}
					
					hashMap.put(attach.attachuuid, attach);
					if (attach.isUpdateOperate() && flag) {
						attach.Operate_type = GsonProtocol.ATTACH_OPERATE_TYPE_ADD;
					}
				}
			}
		}
		
		String[] keys = hashMap.keySet().toArray(new String[0]);
		Attachs[] newAttachs = new Attachs[keys.length];
		for (int i=0; i<keys.length; i++) {
			newAttachs[i] = hashMap.get(keys[i]);
		}
		
		return newAttachs;
	}
	
	private boolean isVideoType(Attachs attach) {
		return attach.attach_type.equals(GsonProtocol.ATTACH_TYPE_VIDEO);
	}
	
	private boolean isVideoType(String attachType) {
		return attachType.equals(GsonProtocol.ATTACH_TYPE_VIDEO);
	}
	
	private boolean isAudioType(Attachs attach) {
		return attach.attach_type.equals(GsonProtocol.ATTACH_TYPE_AUDIO);
	}
	
	private boolean isAudioType(String attachType) {
		return attachType.equals(GsonProtocol.ATTACH_TYPE_AUDIO);
	}
	
	private boolean isPictureType(Attachs attach) {
		return attach.attach_type.equals(GsonProtocol.ATTACH_TYPE_PICTURE);
	}
	
	private boolean isPictureType(String attachType) {
		return attachType.equals(GsonProtocol.ATTACH_TYPE_PICTURE);
	}
	
	private boolean isTextType(Attachs attach) {
		return attach.attach_type.equals(GsonProtocol.ATTACH_TYPE_TEXT);
	}
	
	private boolean isTextType(String attachType) {
		return attachType.equals(GsonProtocol.ATTACH_TYPE_TEXT);
	}
	
	private boolean isVoiceType(Attachs attach) {
		return (attach.attach_type.equals(GsonProtocol.ATTACH_TYPE_VOICE) || attach.attach_type.equals(GsonProtocol.ATTACH_TYPE_VOICE_TEXT));
	}
	
	private boolean isVoiceType(String attachType) {
		return (attachType.equals(GsonProtocol.ATTACH_TYPE_VOICE) || attachType.equals(GsonProtocol.ATTACH_TYPE_VOICE_TEXT));
	}
	
	private void renameOrCopy(FileOperate fileOperate, String originalPath, String newPath) {
		if (!TextUtils.isEmpty(originalPath) && !TextUtils.isEmpty(newPath)) {
			if (fileOperate == FileOperate.RENAME) {
				ZFileSystem.renameTo(originalPath, newPath);
				
			} else {
				ZFileSystem.copy(originalPath, newPath);
			}
		}
	}
	
	
	/**
	 * 检测是否是成功的响应 (服务器未发生错误);
	 * @param response
	 * @return
	 */
	private boolean isSuccessfulResponse(createStructureResponse response) {
		return response != null && response.status.equals(GsonProtocol.RESPONSE_STATUS_OK);
	}
	
	/**
	 * 根据attach类型和attachUUID来获取attach存放路径 (形如: "/mnt/sdcard/.looklook/userId/video/attachUUID.mp4");
	 * @param attachType: GsonProtocol.ATTACH_TYPE_XXX;
	 * @param attachUUID
	 * @param attachSuffix: 附件的扩展名 (GsonProtocol.SUFFIX_XXXX);
	 * @return
	 */
	public String getFullPathByType(String attachType, String attachUUID, String attachSuffix) {
		return getFolderByType(attachType) + attachUUID + attachSuffix;
	}
	
	/**
	 * 根据attach类型来获取文件夹路径 (形如: "/mnt/sdcard/.looklook/userId/video/");
	 * @param type: GsonProtocol.ATTACH_TYPE_XXX;
	 * @return
	 */
	public String getFolderByType(String type) {
		if (type.equals(GsonProtocol.ATTACH_TYPE_VIDEO)) {
			return getAbsolutePathUnderUserDir() + "/video/";
			
		} else if (type.equals(GsonProtocol.ATTACH_TYPE_PICTURE)) {
			return getAbsolutePathUnderUserDir() + "/pic/";
			
		} else if (type.equals(GsonProtocol.ATTACH_TYPE_AUDIO) 
				|| type.equals(GsonProtocol.ATTACH_TYPE_VOICE)
				|| type.equals(GsonProtocol.ATTACH_TYPE_VOICE_TEXT)) {
			return getAbsolutePathUnderUserDir() + "/audio/";
			
		} else if (type.equals(GsonProtocol.ATTACH_TYPE_TEXT)) {
			return getAbsolutePathUnderUserDir() + "/txt/";
		}
		
		return null;
	}
	
	public static String getSuffix(String path) {
		if (path != null) {
			if (path.toLowerCase().endsWith(GsonProtocol.SUFFIX_JPG)) {
				return GsonProtocol.SUFFIX_JPG;
			}
			if (path.toLowerCase().endsWith(GsonProtocol.SUFFIX_MP3)) {
				return GsonProtocol.SUFFIX_MP3;
			}
			if (path.toLowerCase().endsWith(GsonProtocol.SUFFIX_MP4)) {
				return GsonProtocol.SUFFIX_MP4;
			}
		}
		return null;
	}
	
	/**
	 * 根据媒体类型, 媒体绝对路径和url来创建MediaValue对象;
	 *  MediaType: 文件类型:  0：未定义, 1： 文本, 2：图片, 3： 短音频, 4：长音频, 5：视频;
	 *  Direction: 同步方向:  0：未定义, 1：下载, 2：上传.
	 */
	public static void saveToMediaMapping(String attachType, String absolutePath, String url) {
		int mediaType = 0;
		
		if (attachType.equals(GsonProtocol.ATTACH_TYPE_VIDEO)) {
			mediaType = 5;
			
		} else if (attachType.equals(GsonProtocol.ATTACH_TYPE_AUDIO)) {
			mediaType = 4;
			
		} else if (attachType.equals(GsonProtocol.ATTACH_TYPE_VOICE) 
				|| attachType.equals(GsonProtocol.ATTACH_TYPE_VOICE_TEXT)) {
			mediaType = 3;
			
		} else if (attachType.equals(GsonProtocol.ATTACH_TYPE_PICTURE)) {
			mediaType = 2;
			
		} else if (attachType.equals(GsonProtocol.ATTACH_TYPE_TEXT)) {
			mediaType = 1;
		}
		
		MediaValue mediaValue = new MediaValue();
		mediaValue.UID = getUID();
		mediaValue.localpath = getRelativePath(absolutePath);
		mediaValue.totalSize = new File(absolutePath).length();
		mediaValue.realSize = mediaValue.totalSize;
		mediaValue.url = url;
		mediaValue.MediaType = mediaType;
		mediaValue.Direction = 2;
		
		if (MediaValue.checkMediaAvailable(mediaValue, mediaType)) {
			AccountInfo.getInstance(getUID()).mediamapping.delMedia(getUID(), mediaValue.localpath);
			AccountInfo.getInstance(getUID()).mediamapping.setMedia(getUID(), mediaValue.url, mediaValue);
		}
	}
	
	private void deleteFromMediaMapping(String url) {
		AccountInfo.getInstance(getUID()).mediamapping.delMedia(getUID(), url);
	}
	
	/**
	 * 获取当前user目录的绝对路径 (形如: "/mnt/sdcard/.looklook/userId/");
	 * @return
	 */
	public static String getAbsolutePathUnderUserDir() {
		return Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + getUID();
	}
	
	/**
	 * 获取绝对路径中的相对路径 (去掉绝对路径中的"/mnt/sdcard/.looklook/"部分);
	 * @param absolutePath
	 * @return
	 */
	public static String getRelativePath(String absolutePath) {
		return absolutePath.replace(Environment.getExternalStorageDirectory().getPath(), "");
	}
	
	/**
	 * 从缓存中获取包装器;
	 * @param diaryUUID
	 * @return
	 */
	public DiaryWrapper findWrapper(String diaryUUID) {
		return diaryWrappers.get(diaryUUID);
	}
	
	/**
	 * 将包装器放入缓存;
	 * @param key
	 * @param wrapper
	 */
	private void holdWrapper(String key, DiaryWrapper wrapper) {
		diaryWrappers.put(key, wrapper);
	}
	
	/**
	 * 获取一个新的UUID;
	 * @return
	 */
	public static String getNextUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	/**
	 * 获取经纬度;
	 * @return
	 */
	private String getLongitude() {
		if (CommonInfo.getInstance().myLoc != null) {
			return String.valueOf(CommonInfo.getInstance().myLoc.longitude);
		}
		return GsonProtocol.EMPTY_VALUE;
	}
	
	/**
	 * 获取经纬度;
	 * @return
	 */
	private String getLatitude() {
		if (CommonInfo.getInstance().myLoc != null) {
			return String.valueOf(CommonInfo.getInstance().myLoc.latitude);
		}
		return GsonProtocol.EMPTY_VALUE;
	}
	
	/**
	 * 获取当前用户id;
	 * @return
	 */
	public static String getUID() {
		return ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
	}
	
	/**
	 * 获取AccountInfo对象;
	 * @return
	 */
	private AccountInfo getAccountInfo() {
		return AccountInfo.getInstance(getUID());
	}
	
	/**
	 * 从AccountInfo对象中获取MyWeather对象;
	 * @return
	 */
	private MyWeather getMyWeather() {
		return getAccountInfo().myWeather;
	}
	
	/**
	 * 获取国际地址码;
	 * @return
	 */
	private String getAddrCode() {
		MyWeather weather = getMyWeather();
		if (weather != null) {
			return weather.addrCode;
		}
		return GsonProtocol.EMPTY_VALUE;
	}
	
	/**
	 * 获取地址字符串;
	 * @return
	 */
	public static String getPositionString1() {
		if (CommonInfo.getInstance().positionStr != null) {
			return CommonInfo.getInstance().positionStr;
		}
		return GsonProtocol.EMPTY_VALUE;
	}
	
	/**
	 * 获取时间字符串 (毫秒数);
	 * @return
	 */
	private String getTimeString() {
		return String.valueOf(TimeHelper.getInstance().now());
	}
	
	private String translateShootTime(String shootTime, MyDiary diary) {
		String newShootTime = shootTime;
		
		if (TextUtils.isEmpty(newShootTime)) {
			if (diary != null && !TextUtils.isEmpty(diary.shoottime)) {
				newShootTime = diary.shoottime;
				
			} else {
				newShootTime = getTimeString();
			}
		}
		
		return newShootTime;
	}
	
	public static String getAudioPath(String attachUUID) {
		String RECORD_FILE_DIR =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(MainApplication.getAppInstance()).getLookLookID() + "/audio";
		return Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + attachUUID + ".mp4" ;
	}

}
