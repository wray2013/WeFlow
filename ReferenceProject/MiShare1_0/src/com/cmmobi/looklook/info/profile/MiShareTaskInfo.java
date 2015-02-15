package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;
import cn.zipper.framwork.utils.ZStringUtils;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.common.gson.GsonProtocol;
import com.cmmobi.looklook.common.gson.GsonResponse3.MicListItem;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.UserObj;
import com.cmmobi.looklook.common.gson.GsonResponse3.VshareDiary;
import com.cmmobi.looklook.misharetask.MiShareTaskManager;
import com.cmmobi.looklook.networktask.INetworkTask;

/**
 * 需要持久化的微享信息 
 * 一个MiShareTaskInfo对应一条微享上传任务
 */
public final class MiShareTaskInfo {

	private transient static final String TAG = "MiShareTaskInfo";
	
	public String userid;
	
	public String mishareuuid; //微享uuid
	public int state; //任务当前状态
	public long importsize = 0;
	
	public List<NetworkTaskInfo> networktaskinfos; //微享包含的导入任务
	public List<String> remainDiaryuuids; //剩余未完成上传的日记（包括空间里的日记）
	public List<String> allDiaryuuids; //微享任务所有包含的日记（包括空间里的日记）
	
	public MishareParams mishareParams; //微享任务完成后请求离线微享任务
	// 构造方法，微享结构作为输入
	/**
	 * @param mishare
	 *  public VshareDiary[] diarys;  //本微享中所有日记（本地及已上传的）的缩略图所需信息
		public MyDiary[] diarysLocal; //导入生成的本地日记，待上传
	 */
	public MiShareTaskInfo(MicListItem mishare) {
		
		userid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		remainDiaryuuids = Collections.synchronizedList(new ArrayList<String>());
		allDiaryuuids = Collections.synchronizedList(new ArrayList<String>());
		networktaskinfos = Collections.synchronizedList(new ArrayList<NetworkTaskInfo>());
		mishareParams = new MishareParams();
		mishareParams.mishareid = mishare.uuid;
		mishareParams.content = mishare.content;
		mishareParams.misharetitle = mishare.mic_title;
		mishareParams.userobj = mishare.userobj;
		mishareParams.longitude = mishare.longitude;
		mishareParams.latitude = mishare.latitude;
		mishareParams.position = mishare.position;
		mishareParams.position_status = mishare.position_status;
		mishareParams.diaryid = Collections.synchronizedList(new ArrayList<String>());
		mishareParams.capsule = mishare.capsule;
		mishareParams.burn_after_reading = mishare.burn_after_reading;
		mishareParams.capsule_time = mishare.capsule_time;
		
		if(ZStringUtils.nullToEmpty(userid).equals("")) {
			Log.e(TAG, "ERROR : userid is null");
			return;
		}
		
		if (mishare != null && mishare.diarys != null
				&& mishare.diarys.length > 0) {
			this.mishareuuid = mishare.uuid;
			VshareDiary[] alldiaries = mishare.diarys;
			// 导入微享包含的日记id组
			for(VshareDiary d : alldiaries) {
				remainDiaryuuids.add(d.diaryuuid);
				allDiaryuuids.add(d.diaryuuid);
				// 离线日记任务暂时填入uuid，当转换成正常任务时修改
				if(ZStringUtils.nullToEmpty(d.diaryid).equals("")) {
					mishareParams.diaryid.add(d.diaryuuid);
				} else {
					mishareParams.diaryid.add(d.diaryid);
				}
			}
			// 遍历导入日记
			if (mishare.diarysLocal != null) {
				for (MyDiary diary : mishare.diarysLocal) {
					if(!diary.isSychorized()) {
						try {
							long s = Long.parseLong(diary.getDiarySize());
							importsize += s;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					// 判断该日记是否已经在其他微享任务中 &&是否已上传完成(同步)
					if (MiShareTaskManager.getInstance(userid).isContainImportDiaryByDiaryUUID(diary.diaryuuid)
							|| diary.isSychorized()) {
						Log.d(TAG, "日记已经在其他微享任务中 或 已上传完成(同步)");
						remainDiaryuuids.remove(diary.diaryuuid);
						continue;
					}
					NetworkTaskInfo info = null;
					if(diary.isCreated()) {
						// 没有微享过，生成新日记任务
						info = new NetworkTaskInfo(diary, INetworkTask.TASK_TYPE_UPLOAD);
					} else {
						info = new NetworkTaskInfo(diary, INetworkTask.TASK_TYPE_CACHE);
					}
					info.source = 1;
					// 加入微享任务
					networktaskinfos.add(info);
				}
			}
		}
	}
	
	public static class MishareParams {
		public List<String> diaryid;
		public String mishareid;
		public String content;
		public String misharetitle;
		public UserObj[] userobj;
		public String longitude;
		public String latitude;
		public String position;
		public String position_status;
		public String capsule;//时光胶囊标识0 不是 1是
		public String burn_after_reading;//阅后即焚标识0 不是 1是
		public String capsule_time;//"1272212321";时光胶囊开启时间
	}
	
}
