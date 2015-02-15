package com.cmmobi.looklook.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;

import com.baidu.mapapi.map.LocationData;
import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonRequest2;
import com.cmmobi.looklook.common.gson.GsonRequest2.Attachs;
import com.cmmobi.looklook.common.gson.GsonRequest2.createStructureRequest;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttach;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachAudio;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachImage;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachVideo;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDuplicate;
import com.cmmobi.looklook.common.gson.GsonResponse2.TAG;
import com.cmmobi.looklook.common.gson.GsonResponse2.createStructureResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.EffectTransCodeUtil;
import com.cmmobi.looklook.common.utils.MediaCoverUtils;
import com.cmmobi.looklook.common.view.MyPositionLayout;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.info.location.MyLocationInfo;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.info.weather.MyWeather;
import com.cmmobi.looklook.map.MapItemHelper;
import com.cmmobi.looklook.map.MyMapView;
import com.cmmobi.looklook.networktask.CacheNetworkTask;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobi.looklook.networktask.NetworkTaskManager;
import com.cmmobi.looklook.networktask.UploadNetworkTask;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobivideo.utils.EffectBean;
import com.cmmobivideo.utils.EffectUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.workers.XMediaPlayer;
import com.google.gson.Gson;

import effect.XEffects;

public abstract class EditMediaDetailActivity extends FragmentActivity implements OnClickListener{
	
	public final static int FRAGMENT_DETAIL_MAIN_VIEW = 0;// 主fragment
	public final static int FRAGMENT_TAG_VIEW = 1;// 标签fragment
	public final static int FRAGMENT_POS_VIEW = 2;// 位置fragment
	public final static int FRAGMENT_SOUND_VIEW = 3;// 美化声音fragment
	public final static int FRAGMENT_MAIN_VIEW = 4;// 主fragment
	public final static int FRAGMENT_TEXT_INPUT = 5;// 文字输入fragment
	
	public final static int HANDLER_DISMISS_PROCESS_DIALOG = 0xfc;
	public final static int HANDLER_SOUND_DELETE = 0xfa;
	
	public final static int TEXT_MAX = 110;
	
	public final String TAG = this.getClass().getSimpleName();
	public List<Integer> checkedList = new ArrayList<Integer>(3);
	public MyMapView mMapView = null;
	public MyLocationInfo myLocInfo;
	public LocationData myLoc;
//	public MyLocationOverlay mLocOverlay = null;
	public TextView positionView = null;
	public String positionStr = null;// 位置信息
	public TackView tackView = null;// 录音播放控件
	private boolean isRecorded = false;
	public EditText edit = null; // 文字描述编辑框
//	public LinearLayout distinguishLayout = null;
//	public TextView distinguishTv = null;
	protected int[] SOUND_ICONS = {R.drawable.sound_1,R.drawable.sound_2,R.drawable.sound_3};
	protected ImageView ivBack;// 返回按钮
	protected ImageView ivDone;// 确认按钮
	public TextView tagTextView = null;// 标签视图
	public TextView dateTv = null;
	public String tagStr = null;// 标签信息
	
	protected String diaryUUID;
	protected String diaryString;
	private String newDiaryuuid = "";
	public MyDiary myDiary;
	protected List<TAG> tagList = new ArrayList<TAG>();
	
	protected Handler handler = null;
	public String audioPath = "";// 按住说话音频文件路径
	public boolean isDiaryDetailChanged = false;// 日记详情是否修改，这里指位置和标签两项
	public boolean isAddSoundAttach = false; // 标志是否按住录音生成附件
	public boolean isDeleteSound = false; // 标志是否删除录音
	public abstract void goToPage(int type, boolean record);
	public abstract List<Integer> getCheckedList();
	public abstract void setMapViewVisibility(int visible);
	protected boolean isEditButtonPressed = false;
	
	protected boolean isTagPositionChangeDone = true;// 标签，位置信息是否已经修改完成
	protected boolean isAddSoundAttachDone = true;// 按住说话生成附件是否已经修改完成
	protected boolean isExtraModifyDone = true;
	public boolean isMainAttachChanged = false;// 主附件是否编辑过
	public String editAttachPath = null;// 主附件路径
	
	protected boolean hasMainAttach = false;
	public boolean hasShortSound = false;
	protected boolean hasText = false;
	
	protected Attachs attachsDeleteShortAudio = null; // 删除辅录音附件(用在覆盖模式)
	protected Attachs attachsText = null;// 文本附件(用在覆盖模式)
	protected Attachs attachsAddSound = null;//添加辅录音附件(用在覆盖模式)
	protected Attachs attachsDeleteMainAudio = null;// 删除主录音附件
	protected Attachs attachsAddMainAudio = null;// 添加主录音附件
	protected Attachs attachsDeleteVideo = null;// 删除主视频附件
	protected Attachs attachsAddVideo = null;// 添加主视频附件
	protected Attachs attachsDeletePicture = null;// 删除主图片附件
	protected Attachs attachsAddPicture = null;// 添加主图片附件
	
	protected Attachs mainAudioAttach = null;//添加主录音附件(用在另存为模式)
	protected Attachs shortAudioAttach = null;//添加辅录音附件(用在另存为模式)
	protected Attachs newTextAttach = null;// 添加文字附件(用在另存为模式)
	protected Attachs videoAttach = null;// 添加主视频附件(用在另存为模式)
	protected Attachs pictureAttach = null;// 添加图片附件(用在另存为模式)
	
	public String mainAudioPath = null;// 主录音附件路径
	protected String shortAudioPath = null;// 辅录音附件路径
	public String videoAttachPath = null;//主视频路径
	public String pictureAttachPath = null;// 主图片路径
	protected String mainAudioDuration = "";// 主录音时长
	protected String shortAudioDuration = "";// 辅录音时长
	protected String videoDuration = "";// 视频时长
	protected String videoType = "0";// 视频类别（0高清、1普清）
	protected String pictureType = "0";// 图片类别，1有标 0无标
	public String textContent = "";//文字附件内容
	public long startAudioTime = 0;
	public long audioDuration = 0;
	public boolean isDiaryMontaged = false;
	public float montageStartTime = 0;
	public float montageEndTime = 0;
	
	protected LayoutInflater inflater;
	protected View rlTitleBar;
	protected ArrayList<String> diaryStyle = new ArrayList<String>();
	
	protected LinearLayout llXiaLa = null;//下拉菜单
	protected PopupWindow portraitMenu;//下拉菜单PopupWindow
	protected TextView diaryEditStyle = null;
	protected String userID = null;
	protected GsonResponse2.createStructureResponse structureResponse = null;
	protected CountDownLatch threadsSignal = new CountDownLatch(1);
	protected CountDownLatch shortAudioSignal = new CountDownLatch(1);
	protected String videoFrontCoverUrl = null;
	protected diaryAttach videoAttachCover = null;
	
	public XMediaPlayer mMediaPlayer;
	public XEffects mEffects;
	public EffectUtils effectUtils;
	public List<EffectBean> effectBeans;
	public int effectState = -1;// 特效处理状态（-1，无特效，0，有特效，1，特效处理文件生成完成
	
	public EffectUtils shortRecEffectUtils;
	public XEffects shortRecEffect;
	public List<EffectBean> shortRecEffectBeans;
	public XMediaPlayer shortRecMediaPlayer;
	public MyPositionLayout myPositionLayout;
	public TextView myPositionTv;
	
	public TextView dialogTitle;
	public boolean isAttachInited = false;
	
	public int fragmentType = 0;
	public int lastFragmentType = 0;
	
	public int trackId = 0;
	public double longRecPercent = 0.5;
	public int beautifulSoundId = 0;
	public TextView tvTextNum = null;
	public boolean isAddSoundTrack = false;
	private GsonRequest2.createStructureRequest createStructure = null;
	public boolean isLongPressed = false; // 是否在录制短录音
	
	public void setPositionText(String text) {
		if (positionView != null) {
			positionView.setText(text);
			positionStr = text;
			Log.d(TAG,"setPositionText text = " + text);
		}
	}
	
	public void setTagText(String text) {
		if (tagTextView != null) {
			tagTextView.setText(text);
			tagStr = text;
			Log.d(TAG,"tagTextView text = " + text);
		}
	}
	
	public void setSoundText(String text) {
		if (edit != null) {
			edit.setText(text);
			if (text != null) {
        		int laveNum = TEXT_MAX - text.toString().length();
        		tvTextNum.setText(laveNum + "/" + TEXT_MAX);
        	}
		}
	}
	
	public void addSoundText(String text) {
		if (edit != null) {
			edit.append(text);
		}
	}
	
	public String getSoundText() {
		if (edit != null) {
			return edit.getText().toString();
		}
		return null;
	}
	
	public void setRecord(boolean flag) {
		isRecorded = flag;
	}
	
	public boolean getRecord() {
		return isRecorded;
	}
	
	public void addTag(TAG tag) {
		tagList.add(tag);
	}
	
	public void clearTag() {
		tagList.clear();
	}
	
	public List<TAG> getTagList() {
		return tagList;
	}
	
	public static String getTagString(MyDiary diary) {
		if (diary != null) {
			TAG[] tags = diary.tags;
			if (tags != null && tags.length >0) {
				String tagsStr = "";
				for (int i = 0;i < tags.length;i++) {
					TAG tag = tags[i];
					tagsStr += tag.id;
					if (i != tags.length - 1) {
						tagsStr += ",";
					}
				}
				return tagsStr;
			}
		}
		return "";
	}
	
	protected void initView(Activity act) {
		tackView = (TackView) act.findViewById(R.id.ll_biezhen);
        tackView.setSoundIcons(SOUND_ICONS);
        tackView.setBackground(R.drawable.sound_3);
        tackView.setBackground(R.drawable.bg_edit_media_sound_play);
        tackView.setOnClickListener(this);
        tackView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				View view = LayoutInflater.from(EditMediaDetailActivity.this).inflate(
						R.layout.activity_edit_media_delete_sound_pop,
						null);
				final PopupWindow popupWindow = new PopupWindow(view,
						LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT){
					
				};
				popupWindow.setBackgroundDrawable(new BitmapDrawable());
				popupWindow.setFocusable(true);
				popupWindow.setTouchable(true);
				popupWindow.setOutsideTouchable(true);

				// popupWindow.showAsDropDown(viewHolder.vedioRelativeLayout,
				// -40, 0);
				int[] location = new int[2];
				v.getLocationInWindow(location);
				
				int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
		    	int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
		    	view.measure(w, h);  
		    	int popWidth = view.getMeasuredWidth();
		    	int popHeight = view.getMeasuredHeight();
		    	
		    	int vw = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
		    	int vh = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
		    	v.measure(vw, vh);
		    	int viewWidth = v.getMeasuredWidth();
		    	
				popupWindow.showAtLocation(v, 0, location[0] + (viewWidth - popWidth)/2, location[1] - popHeight);
				view.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Log.d(TAG,"tackview sound delete onclick in");
						handler.obtainMessage(HANDLER_SOUND_DELETE).sendToTarget();
						popupWindow.dismiss();
						tackView.setVisibility(View.INVISIBLE);
					}
				});
				return false;
			}
		});
        dateTv = (TextView) act.findViewById(R.id.tv_edit_media_date);
        edit = (EditText) act.findViewById(R.id.et_edit_media_record_text);
        edit.addTextChangedListener(mTextWatcher);
//        distinguishLayout = (LinearLayout) act.findViewById(R.id.ll_edit_media_sound_distinguish);
//        distinguishTv = (TextView) act.findViewById(R.id.tv_edit_media_sound_distinguish);
//        distinguishLayout.setOnClickListener(this);
        tvTextNum = (TextView) act.findViewById(R.id.tv_edit_media_text_num);
	}
	
	public void hideSoftKeyboard() {
		((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(edit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);   
	}
	
	private TextWatcher mTextWatcher = new TextWatcher() {  
		  
		 private CharSequence temp;
         private boolean isEdit = true;
         private int selectionStart ;
         private int selectionEnd ; 
  
        public void afterTextChanged(Editable s) { 
        	Log.d(TAG,"onTextChanged in hasMainAttach = " + hasMainAttach + " hasShortSound = " + hasShortSound);
        	if (!isAttachInited) {
        		return;
        	}
        	if (s == null || "".equals(s.toString())) {
        		hasText = false;
        		if (!hasMainAttach && !hasShortSound) {
        			ivDone.setClickable(false);
        			ivDone.setImageResource(R.drawable.wancheng_pressed);
        		}
            } else {
            	hasText = true;
            	ivDone.setImageResource(R.drawable.btn_activity_edit_photo_done);
            	ivDone.setClickable(true);
            }
        	
        	if (s != null) {
        		int laveNum = TEXT_MAX - s.toString().length();
        		tvTextNum.setText(laveNum + "/" + TEXT_MAX);
        	}
        }  
  
        public void beforeTextChanged(CharSequence s, int start, int count,  
                int after) {
        }  
  
        public void onTextChanged(CharSequence s, int start, int before,  
                int count) { 
        	Log.d(TAG,"onTextChanged in");
            
        }  
    };
    
    public void checkAuxiliaryAttachEmpty() {
    	Log.d(TAG,"hasMainAttach = " + hasMainAttach + " hasShortSound = " + hasShortSound + " hasText = " + hasText);
    	if (!hasMainAttach && !hasShortSound && !hasText) {
    		ivDone.setImageResource(R.drawable.wancheng_pressed);
    		ivDone.setClickable(false);
    	} else {
    		ivDone.setImageResource(R.drawable.btn_activity_edit_photo_done);
    		ivDone.setClickable(true);
    	}
    }
	
	public String getDiaryUUID() {
		return diaryUUID;
	}
	public String getDiaryString() {
		return diaryString;
	}
	public MyDiary getMyDiary() {
		return myDiary;
	}
	
	public String getEditDiaryStyle() {
		if (diaryEditStyle != null) {
			return diaryEditStyle.getText().toString();
		}
		return null;
	}
	
	/**
	 * 创建日记副本，发送请求
	 */
	protected void saveAsNewDiary() {
		String textAttachContent = null;
		
		Attachs[] attachs = null;
		ArrayList<Attachs> attachsList = new ArrayList<Attachs>();
		String diaryUUID = UUID.randomUUID().toString().replace("-", "");
		
		/*if (mainAudioPath != null) {// 填充主音频附件Attach结构
			mainAudioAttach = new Attachs();
			mainAudioAttach.attachid = "";
			mainAudioAttach.attachuuid = UUID.randomUUID().toString().replace("-", "");
			mainAudioAttach.attach_type = "2";
			mainAudioAttach.level = "1";
			mainAudioAttach.audio_type = "1";
			if (CommonInfo.getInstance().myLoc != null) {
				mainAudioAttach.attach_latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
				mainAudioAttach.attach_logitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			}
			mainAudioAttach.content = "";
			mainAudioAttach.suffix = ".mp4";
			mainAudioAttach.Operate_type = "1";
			attachsList.add(mainAudioAttach);
		}
		
		if (videoAttachPath != null) {
			videoAttach = new Attachs();
			videoAttach.attachid = "";
			videoAttach.attachuuid = UUID.randomUUID().toString().replace("-", "");
			videoAttach.attach_type = "1";
			videoAttach.level = "1";
			videoAttach.video_type = videoType;
			if (CommonInfo.getInstance().myLoc != null) {
				videoAttach.attach_latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
				videoAttach.attach_logitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			}
			videoAttach.content = "";
			videoAttach.suffix = ".mp4";
			videoAttach.Operate_type = "1";
			attachsList.add(videoAttach);
		}
		
		if (pictureAttachPath != null) {
			pictureAttach = new Attachs();
			pictureAttach.attachid = "";
			pictureAttach.attachuuid = UUID.randomUUID().toString().replace("-", "");
			pictureAttach.attach_type = "3";
			pictureAttach.level = "1";
			pictureAttach.photo_type = pictureType;
			if (CommonInfo.getInstance().myLoc != null) {
				pictureAttach.attach_latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
				pictureAttach.attach_logitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			}
			pictureAttach.content = "";
			pictureAttach.suffix = ".jpg";
			pictureAttach.Operate_type = "1";
			attachsList.add(pictureAttach);
		}
		
		if (isAddSoundAttach) {
			fillAddSoundAttach();
			attachsList.add(shortAudioAttach);
			shortAudioPath = audioPath;
		} else if (audioPath != null && hasShortSound){
			String dstShortAudioPath = null;
			if (audioPath != null) {
				String audioID = String.valueOf(TimeHelper.getInstance().now());
				dstShortAudioPath = Environment.getExternalStorageDirectory().getPath() + Constant.SD_STORAGE_ROOT + "/" 
						+ ActiveAccount.getInstance(this).getLookLookID() + "/audio/" + audioID + "/" + audioID + ".mp4";
			}
			long shortAudioLength = copyFile2(audioPath, dstShortAudioPath);
			if (shortAudioLength > 0) {
				fillAddSoundAttach();
				attachsList.add(shortAudioAttach);
				shortAudioPath = dstShortAudioPath;
			}
		}
		
		if (getSoundText() != null && !"".equals(getSoundText())) {
			fillAddTextAttach(getSoundText());
			attachsList.add(newTextAttach);
		} else if (textAttachContent != null) {
			fillAddTextAttach(textAttachContent);
			attachsList.add(newTextAttach);
		}*/
		
		createNewRequesterDiaryAttach(attachsList);
		
		attachs = attachsList.toArray(new Attachs[attachsList.size()]);
		String userSelectedPosition = null;
		if (myDiary != null) {
		    userSelectedPosition = myDiary.position;
		}
		String tags = getTags();
		String resourcediaryid = "";
		String resourcediaryuuid = "";
		final DiaryManager diaryManager = DiaryManager.getInstance();
		MyDiary localDiary = diaryManager.findLocalDiary(myDiary.diaryid);
		if (localDiary != null && localDiary.sync_status != 0) {
			if (myDiary.resourcediaryid != null && !"".equals(myDiary.resourcediaryid)) {
				resourcediaryid = myDiary.resourcediaryid;
			} else {
				resourcediaryid = myDiary.diaryid;
			}
		}
		
		if (myDiary.resourceuuid != null && !"".equals(myDiary.resourceuuid)) {
			resourcediaryuuid = myDiary.resourceuuid;
		} else {
			resourcediaryuuid = myDiary.diaryuuid;
		}
		Log.d(TAG,"saveAsNewDiary createStructure attachsSize = " + attachs.length);
		String longitude = "";
		String latitude = "";
		if (CommonInfo.getInstance().myLoc != null) {
			longitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
		}
		String addressCode = "";
		AccountInfo account = AccountInfo.getInstance(userID);
		MyWeather weather = account.myWeather;
		if (weather != null) {
			addressCode = weather.addrCode;
		}
		if (attachs != null && attachs.length > 0) {
			createStructure = Requester2.createStructure(handler, "", diaryUUID, "3", resourcediaryid, resourcediaryuuid,
					longitude,latitude,
					tags,userSelectedPosition,longitude,latitude,String.valueOf(TimeHelper.getInstance().now()),
					addressCode,attachs);
		} else {
			ZDialog.dismiss();
			finish();
		}
	}
	
	private void createNewRequesterDiaryAttach(ArrayList<Attachs> attachsList) {
		if (mainAudioPath != null) {// 填充主音频附件Attach结构
			mainAudioAttach = new Attachs();
			mainAudioAttach.attachid = "";
			mainAudioAttach.attachuuid = UUID.randomUUID().toString().replace("-", "");
			mainAudioAttach.attach_type = "2";
			mainAudioAttach.level = "1";
			mainAudioAttach.audio_type = "1";
			if (CommonInfo.getInstance().myLoc != null) {
				mainAudioAttach.attach_latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
				mainAudioAttach.attach_logitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			}
			mainAudioAttach.content = "";
			mainAudioAttach.suffix = ".mp4";
			mainAudioAttach.Operate_type = "1";
			attachsList.add(mainAudioAttach);
		}
		
		if (videoAttachPath != null) {
			videoAttach = new Attachs();
			videoAttach.attachid = "";
			videoAttach.attachuuid = UUID.randomUUID().toString().replace("-", "");
			videoAttach.attach_type = "1";
			videoAttach.level = "1";
			videoAttach.video_type = videoType;
			if (CommonInfo.getInstance().myLoc != null) {
				videoAttach.attach_latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
				videoAttach.attach_logitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			}
			videoAttach.content = "";
			videoAttach.suffix = ".mp4";
			videoAttach.Operate_type = "1";
			attachsList.add(videoAttach);
		}
		
		if (pictureAttachPath != null) {
			pictureAttach = new Attachs();
			pictureAttach.attachid = "";
			pictureAttach.attachuuid = UUID.randomUUID().toString().replace("-", "");
			pictureAttach.attach_type = "3";
			pictureAttach.level = "1";
			pictureAttach.photo_type = pictureType;
			if (CommonInfo.getInstance().myLoc != null) {
				pictureAttach.attach_latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
				pictureAttach.attach_logitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			}
			pictureAttach.content = "";
			pictureAttach.suffix = ".jpg";
			pictureAttach.Operate_type = "1";
			attachsList.add(pictureAttach);
		}
		
		if (isAddSoundAttach) {
			fillAddSoundAttach();
			attachsList.add(shortAudioAttach);
			shortAudioPath = audioPath;
		} else if (audioPath != null && hasShortSound){
			String dstShortAudioPath = null;
			if (audioPath != null) {
				String audioID = String.valueOf(TimeHelper.getInstance().now());
				dstShortAudioPath = Environment.getExternalStorageDirectory().getPath() + Constant.SD_STORAGE_ROOT + "/" 
						+ ActiveAccount.getInstance(this).getLookLookID() + "/audio/" + audioID + "/" + audioID + ".mp4";
			}
			long shortAudioLength = copyFile2(audioPath, dstShortAudioPath);
			if (shortAudioLength > 0) {
				fillAddSoundAttach();
				attachsList.add(shortAudioAttach);
				shortAudioPath = dstShortAudioPath;
			}
		}
		
		if (getSoundText() != null && !"".equals(getSoundText())) {
			fillAddTextAttach(getSoundText());
			attachsList.add(newTextAttach);
		}
	}
	
	protected void createOfflineDiary() {
		isAddSoundAttachDone = false;
		Attachs[] attachs = null;
		ArrayList<Attachs> attachsList = new ArrayList<Attachs>();
		createNewRequesterDiaryAttach(attachsList);
		attachs = attachsList.toArray(new Attachs[attachsList.size()]);
		String userSelectedPosition = null;
		if (myDiary != null) {
		    userSelectedPosition = myDiary.position;
		}
		String tags = getTags();
		String resourcediaryid = "";
		String resourcediaryuuid = "";
		Log.d(TAG,"saveAsNewDiary createStructure attachsSize = " + attachs.length);
		String longitude = "";
		String latitude = "";
		if (CommonInfo.getInstance().myLoc != null) {
			longitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
		}
		String addressCode = "";
		AccountInfo account = AccountInfo.getInstance(userID);
		MyWeather weather = account.myWeather;
		if (weather != null) {
			addressCode = weather.addrCode;
		}
		if (attachs != null && attachs.length > 0) {
			createStructure = Requester2.createStructure(handler, "", myDiary.diaryuuid, "1", resourcediaryid, resourcediaryuuid,
					longitude,latitude,
					tags,userSelectedPosition,longitude,latitude,String.valueOf(TimeHelper.getInstance().now()),
					addressCode,attachs);
		} else {
			isAddSoundAttachDone = true;
			ZDialog.dismiss();
			finish();
		}
	}
	
	protected boolean isDiaryCreated() {
		if (myDiary == null) {
			 return false;
		}
		DiaryManager diarymanager = DiaryManager.getInstance();
		MyDiary myLocalDiary = diarymanager.findLocalDiaryByUuid(myDiary.diaryuuid);
		if (myLocalDiary != null) {
			Log.d(TAG,"isDiaryCreated sync_status = " + myLocalDiary.sync_status);
			return (myLocalDiary.sync_status != 0);
		}
		return false;
	}
	
	protected void removeDiaryTask() {
		NetworkTaskManager netTaskManager = NetworkTaskManager.getInstance(userID);
		if (myDiary != null) {
			netTaskManager.removeTask(myDiary.diaryuuid);
		}
	}
	
	/**
	 * 填充辅录音附件
	 */
	protected void fillAddSoundAttach() {
		shortAudioAttach = new Attachs();
		shortAudioAttach.attachid = "";
		shortAudioAttach.attachuuid = UUID.randomUUID().toString().replace("-", "");
		shortAudioAttach.attach_type = "2";
		shortAudioAttach.level = "0";
		shortAudioAttach.audio_type = "1";
		if (CommonInfo.getInstance().myLoc != null) {
			shortAudioAttach.attach_latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
			shortAudioAttach.attach_logitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
		}
		shortAudioAttach.suffix = ".mp4";
		shortAudioAttach.Operate_type = "1";
	}
	
	/**
	 * 填充文字附件
	 */
	protected void fillAddTextAttach(String context) {
		newTextAttach = new Attachs();
		newTextAttach.attachid = "";
		newTextAttach.attachuuid = UUID.randomUUID().toString().replace("-", "");
		newTextAttach.attach_type = "4";
		newTextAttach.level = "0";
		newTextAttach.content = context;
		if (CommonInfo.getInstance().myLoc != null) {
			newTextAttach.attach_latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
			newTextAttach.attach_logitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
		}
		newTextAttach.Operate_type = "1";// 若以前没有文字描述附件，则添加文字附件
	}
	
	public static void fillAddTextAttach(Attachs textAttach,String context) {
		textAttach.attachid = "";
		textAttach.attachuuid = UUID.randomUUID().toString().replace("-", "");
		textAttach.attach_type = "4";
		textAttach.level = "0";
		textAttach.content = context;
		if (CommonInfo.getInstance().myLoc != null) {
			textAttach.attach_latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
			textAttach.attach_logitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
		}
		textAttach.Operate_type = "1";// 若以前没有文字描述附件，则添加文字附件
	}
	
	public String getTags() {
		String tags = "";
		for (int i = 0;i < tagList.size();i++) {
			tags += tagList.get(i).id;
			if (i != tagList.size() - 1) {
				tags += ",";
			}
		}
		return tags;
	}
	
	public String getTagNames() {
		String tags = "";
		for (int i = 0;i < tagList.size();i++) {
			tags += tagList.get(i).name;
			if (i != tagList.size() - 1) {
				tags += ",";
			}
		}
		return tags;
	}
	/**
	 * 另存为创建日记结构（非管理结构）并保存到本地缓存
	 * @param structureResponse
	 * @return
	 */
	class CreateNewDiaryThread extends Thread {
		@Override
		public void run() {
			if (structureResponse == null || myDiary == null) {
				return;
			}
			
			Log.d(TAG,"createDiaryStructure in");
			AccountInfo account = AccountInfo.getInstance(userID);
			final GsonResponse2.MyDiary diary = new GsonResponse2.MyDiary();
			
			diary.weather_info = "";
			diary.weather = "";
			MyWeather weather = account.myWeather;
			if (weather != null) {
				if(weather.desc != null && weather.desc.length > 0) {
					diary.weather_info = weather.desc[0].description;
					diary.weather = weather.desc[0].weatherurl;
				}
			}
			diary.mood = "0";
			if (account.mood != null && !"".equals(account.mood)) {
				diary.mood = account.mood;
			}
			diary.userid = userID;
			diary.diaryuuid = structureResponse.diaryuuid;
			if (structureResponse.diaryid != null && !"".equals(structureResponse.diaryid)) {
				diary.diaryid = structureResponse.diaryid;
			} 
			/*else {
				diary.diaryid = diary.diaryuuid;// diaryID为空时将diaryuuid赋值给diaryID
			}*/
			diary.diarytimemilli = String.valueOf(TimeHelper.getInstance().now());
			diary.updatetimemilli = String.valueOf(TimeHelper.getInstance().now());
			diary.diary_status = "1";
			diary.nickname = account.nickname;
			diary.publish_status = account.setmanager.getDiary_type();
			if (CommonInfo.getInstance().myLoc != null) {
				diary.latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
				diary.longitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			}
			diary.position = "";
			if (MapItemHelper.locateAvailable(diary)) {
				diary.position = CommonInfo.getInstance().positionStr;
			}
			diary.sex = ActiveAccount.getInstance(ZApplication.getInstance()).sex;
			diary.signature = account.signature;
			diary.headimageurl = account.headimageurl;
			if (myDiary != null) {
				diary.join_safebox = myDiary.join_safebox;
			} else {
				diary.join_safebox = "0";
			}
			String tags = getTags();
			diary.setTags(tags);
			if (myDiary != null) {
				diary.position = myDiary.position;
			}
			
			if (myDiary.resourcediaryid != null && !"".equals(myDiary.resourcediaryid)) {
				diary.resourcediaryid = myDiary.resourcediaryid;
			} else {
				diary.resourcediaryid = myDiary.diaryid;
			}
			
			if (myDiary.resourceuuid != null && !"".equals(myDiary.resourceuuid)) {
				diary.resourceuuid = myDiary.resourceuuid;
			} else {
				diary.resourceuuid = myDiary.diaryuuid;
			}
			
			int attachsSize = 0;
			if (structureResponse.attachs != null) {
				attachsSize = structureResponse.attachs.length;
			}
			Log.d(TAG,"attachsSize  = " + attachsSize);
			
			if (isMainAttachChanged) {
				modifyMainAttach();
			} else {
				copyMainAttach();
			}
			
			Log.d(TAG,"editAttachPath = " + editAttachPath);
			
			/*diaryAttach audioAttach = null;// 主音频附件
			diaryAttach subAudioAttach = null;// 辅录音附件
			diaryAttach textAttach = null;// 文本附件
			diaryAttach videoAttachTemp = null;// 视频附件
			diaryAttach pictureAttachTemp = null;// 图片附件
			if ("0".equals(structureResponse.status)) {
				if (structureResponse.attachs != null && attachsSize > 0) {// 创建日记结构成功
					for (int i = 0;i < attachsSize; i++) {
						MyAttach myAttach = structureResponse.attachs[i];
						if (mainAudioAttach != null && myAttach.attachuuid.equals(mainAudioAttach.attachuuid)) {// 主录音
							audioAttach = new diaryAttach();
							audioAttach.playtime = getMontageDuration();
							fillAttachAndMapping(userID,audioAttach,myAttach,"2",4,editAttachPath,"1");
						} else if (videoAttach != null && myAttach.attachuuid.equals(videoAttach.attachuuid)){
							videoAttachTemp = new diaryAttach();
							videoAttachTemp.playtime = getMontageDuration();
							fillAttachAndMapping(userID,videoAttachTemp, myAttach, "1", 5, editAttachPath,videoType);
							videoAttachCover = videoAttachTemp;
						} else if (pictureAttach != null && myAttach.attachuuid.equals(pictureAttach.attachuuid)){
							pictureAttachTemp = new diaryAttach();
							fillAttachAndMapping(userID,pictureAttachTemp, myAttach, "3", 2, editAttachPath,pictureType);
						} else if (shortAudioAttach != null && myAttach.attachuuid.equals(shortAudioAttach.attachuuid)) {// 辅录音
							subAudioAttach = new diaryAttach();
							if (audioDuration != 0) {
								subAudioAttach.playtime = String.valueOf((audioDuration + 999)/ 1000);
							} else if (shortAudioDuration != null && !"".equals(shortAudioDuration)) {
								subAudioAttach.playtime = shortAudioDuration;
							} else {
								subAudioAttach.playtime = "0";
							}
							
							fillAttachAndMapping(userID,subAudioAttach, myAttach,"2", 3, shortAudioPath,"1");
						} else if (newTextAttach != null && myAttach.attachuuid.equals(newTextAttach.attachuuid)) {// 辅文字
							textAttach = new diaryAttach();
							textAttach.attachid = myAttach.attachid;
							textAttach.attachuuid = myAttach.attachuuid;
							textAttach.attachtype = "4";
							textAttach.attachlevel = "0";
							if (newTextAttach != null) {
								textAttach.content = newTextAttach.content;
							} 
						}
					}
				}
				
				if (textAttach == null && newTextAttach != null) {
					textAttach = new diaryAttach();
					textAttach.attachid = "";
					textAttach.attachuuid = newTextAttach.attachuuid;
					textAttach.attachtype = "4";
					textAttach.attachlevel = "0";
					textAttach.content = newTextAttach.content;
				}
				diary.sync_status = 1;
			} else {// 创建日记结构失败
				if (mainAudioAttach != null) {
					audioAttach = new diaryAttach();
					audioAttach.playtime = getMontageDuration();
					fillAttachAndMapping(userID,audioAttach,mainAudioAttach,"2",4,editAttachPath,"1");
				}
				
				if (videoAttach != null) {
					videoAttachTemp = new diaryAttach();
					videoAttachTemp.playtime = getMontageDuration();
					fillAttachAndMapping(userID,videoAttachTemp, videoAttach, "1", 5, editAttachPath,videoType);
				}
				
				if (pictureAttach != null) {
					pictureAttachTemp = new diaryAttach();
					fillAttachAndMapping(userID,pictureAttachTemp, pictureAttach, "3", 2, editAttachPath,pictureType);
				}
				
				if (shortAudioAttach != null) {
					subAudioAttach = new diaryAttach();
					if (audioDuration != 0) {
						subAudioAttach.playtime = String.valueOf(audioDuration / 1000);
					} else if (!"".equals(shortAudioDuration)) {
						subAudioAttach.playtime = shortAudioDuration;
					}
					fillAttachAndMapping(userID,subAudioAttach, shortAudioAttach,"2",3, shortAudioPath,"1");
				}
				
				if (newTextAttach != null) {
					textAttach = new diaryAttach();
					textAttach.attachuuid = newTextAttach.attachuuid;
					textAttach.attachtype = "4";
					textAttach.attachlevel = "0";
					textAttach.content = newTextAttach.content;
				}
			}*/
			
			GsonResponse2.diaryAttach[] attachs;
			ArrayList<diaryAttach> attachsList = new ArrayList<diaryAttach>();
			createNewDiaryAttach(attachsList, diary);
			/*if (audioAttach != null) {
				attachsList.add(audioAttach);
			}
			if (subAudioAttach != null) {
				attachsList.add(subAudioAttach);
			}
			if (textAttach != null) {
				attachsList.add(textAttach);
			}
			if (videoAttachTemp != null) {
				attachsList.add(videoAttachTemp);
			}
			if (pictureAttachTemp != null) {
				attachsList.add(pictureAttachTemp);
			}*/
			attachs = attachsList.toArray(new diaryAttach[attachsList.size()]);
			diary.attachs = attachs;
			
			if (createStructure != null) {
				diary.request = createStructure;
			}
			
			final DiaryManager diaryManager = DiaryManager.getInstance();
			diaryManager.saveDiaries(diary, true);
			newDiaryuuid = diary.diaryuuid;
			MyDiary localDiary = diaryManager.findLocalDiary(diary.resourcediaryid);
			MyDuplicate[] duplicates = null;
			if (localDiary != null) {
				duplicates = localDiary.duplicate;
			}
			int dupLen = 0;
			if (duplicates != null) {
				dupLen = duplicates.length;
			}
			
			MyDuplicate[] newDups = new MyDuplicate[dupLen + 1];
			int i = 0;
			for (i = 0;i < dupLen;i++) {
				newDups[i] = duplicates[i];
			}
			newDups[i] = new GsonResponse2().new MyDuplicate();
			newDups[i].diaryid = diary.diaryid;
			newDups[i].duplicatename = getString(R.string.str_activit_detail_diary_duplicate) + dupLen; 
			if (localDiary != null) {
				localDiary.duplicate = newDups;
			}
			
			// 日记上传
			if (diary.diaryid == null || "".equals(diary.diaryid)/*diary.diaryuuid.equals(diary.diaryid)*/) {// 创建日记结构失败
//				Log.d("==WR==", "gAttachs  size = " + attachsUpload.length);
				NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(diary, INetworkTask.TASK_TYPE_CACHE);//设置数据源
				CacheNetworkTask cachetask = new CacheNetworkTask(networktaskinfo, createStructure, networktaskinfo.caMedias);//创建上传/下载任务
				NetworkTaskManager.getInstance(userID).addTask(cachetask);//添加网络任务
				NetworkTaskManager.getInstance(userID).startNextTask();    //开始任务队列
			} else {// 创建日记结构成功
				NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(diary, INetworkTask.TASK_TYPE_UPLOAD);//设置数据源
				UploadNetworkTask uploadtask = new UploadNetworkTask(networktaskinfo);//创建上传/下载任务
				NetworkTaskManager.getInstance(userID).addTask(uploadtask);//添加网络任务
				NetworkTaskManager.getInstance(userID).startNextTask();    //开始任务队列
			}
			isAddSoundAttachDone = true;
			handler.sendEmptyMessage(HANDLER_DISMISS_PROCESS_DIALOG);
		}
	}
	
	private void createNewDiaryAttach(ArrayList<diaryAttach> attachsList,MyDiary diary) {
		int attachsSize = 0;
		if (structureResponse.attachs != null) {
			attachsSize = structureResponse.attachs.length;
		}
		diaryAttach audioAttach = null;// 主音频附件
		diaryAttach subAudioAttach = null;// 辅录音附件
		diaryAttach textAttach = null;// 文本附件
		diaryAttach videoAttachTemp = null;// 视频附件
		diaryAttach pictureAttachTemp = null;// 图片附件
		if ("0".equals(structureResponse.status)) {
			if (structureResponse.attachs != null && attachsSize > 0) {// 创建日记结构成功
				for (int i = 0;i < attachsSize; i++) {
					MyAttach myAttach = structureResponse.attachs[i];
					if (mainAudioAttach != null && myAttach.attachuuid.equals(mainAudioAttach.attachuuid)) {// 主录音
						audioAttach = new diaryAttach();
						audioAttach.playtime = getMontageDuration();
						fillAttachAndMapping(userID,audioAttach,myAttach,"2",4,editAttachPath,"1");
						attachsList.add(audioAttach);
					} else if (videoAttach != null && myAttach.attachuuid.equals(videoAttach.attachuuid)){
						videoAttachTemp = new diaryAttach();
						videoAttachTemp.playtime = getMontageDuration();
						fillAttachAndMapping(userID,videoAttachTemp, myAttach, "1", 5, editAttachPath,videoType);
						videoAttachCover = videoAttachTemp;
						attachsList.add(videoAttachTemp);
					} else if (pictureAttach != null && myAttach.attachuuid.equals(pictureAttach.attachuuid)){
						pictureAttachTemp = new diaryAttach();
						fillAttachAndMapping(userID,pictureAttachTemp, myAttach, "3", 2, editAttachPath,pictureType);
						attachsList.add(pictureAttachTemp);
					} else if (shortAudioAttach != null && myAttach.attachuuid.equals(shortAudioAttach.attachuuid)) {// 辅录音
						subAudioAttach = new diaryAttach();
						if (audioDuration != 0) {
							subAudioAttach.playtime = String.valueOf((audioDuration + 999)/ 1000);
						} else if (shortAudioDuration != null && !"".equals(shortAudioDuration)) {
							subAudioAttach.playtime = shortAudioDuration;
						} else {
							subAudioAttach.playtime = "0";
						}
						fillAttachAndMapping(userID,subAudioAttach, myAttach,"2", 3, shortAudioPath,"1");
						attachsList.add(subAudioAttach);
					} else if (newTextAttach != null && myAttach.attachuuid.equals(newTextAttach.attachuuid)) {// 辅文字
						textAttach = new diaryAttach();
						textAttach.attachid = myAttach.attachid;
						textAttach.attachuuid = myAttach.attachuuid;
						textAttach.attachtype = "4";
						textAttach.attachlevel = "0";
						if (newTextAttach != null) {
							textAttach.content = newTextAttach.content;
						} 
						attachsList.add(textAttach);
					}
				}
			}
			
			if (textAttach == null && newTextAttach != null) {
				textAttach = new diaryAttach();
				textAttach.attachid = "";
				textAttach.attachuuid = newTextAttach.attachuuid;
				textAttach.attachtype = "4";
				textAttach.attachlevel = "0";
				textAttach.content = newTextAttach.content;
				attachsList.add(textAttach);
			}
			diary.sync_status = 1;
		} else {// 创建日记结构失败
			if (mainAudioAttach != null) {
				audioAttach = new diaryAttach();
				audioAttach.playtime = getMontageDuration();
				fillAttachAndMapping(userID,audioAttach,mainAudioAttach,"2",4,editAttachPath,"1");
				attachsList.add(audioAttach);
			}
			
			if (videoAttach != null) {
				videoAttachTemp = new diaryAttach();
				videoAttachTemp.playtime = getMontageDuration();
				fillAttachAndMapping(userID,videoAttachTemp, videoAttach, "1", 5, editAttachPath,videoType);
				attachsList.add(videoAttachTemp);
			}
			
			if (pictureAttach != null) {
				pictureAttachTemp = new diaryAttach();
				fillAttachAndMapping(userID,pictureAttachTemp, pictureAttach, "3", 2, editAttachPath,pictureType);
				attachsList.add(pictureAttachTemp);
			}
			
			if (shortAudioAttach != null) {
				subAudioAttach = new diaryAttach();
				if (audioDuration != 0) {
					subAudioAttach.playtime = String.valueOf(audioDuration / 1000);
				} else if (!"".equals(shortAudioDuration)) {
					subAudioAttach.playtime = shortAudioDuration;
				}
				fillAttachAndMapping(userID,subAudioAttach, shortAudioAttach,"2",3, shortAudioPath,"1");
				attachsList.add(subAudioAttach);
			}
			
			if (newTextAttach != null) {
				textAttach = new diaryAttach();
				textAttach.attachuuid = newTextAttach.attachuuid;
				textAttach.attachtype = "4";
				textAttach.attachlevel = "0";
				textAttach.content = newTextAttach.content;
				attachsList.add(textAttach);
			}
		}
	}
	
	/**
	 * 创建日记管理结构返回成功时，填充音频附件并再MediaMapping里面做登记
	 * @param attach
	 * @param resAttach
	 * @param audioType
	 * @param absolutePath
	 */
	public static void fillAttachAndMapping(String userID,diaryAttach attach,MyAttach resAttach,String attachType,int mediaType,String absolutePath,String mediaStyle) {
		if (absolutePath == null) {
			return;
		}
		
		attach.attachtype = attachType;
		MediaValue mediaValue = new MediaValue();
		String relativePath = absolutePath.replace(HomeActivity.SDCARD_PATH, "");
		String mappingKey = "";
		if ("1".equals(attachType)) {
			attach.attachlevel = "1";
			GsonResponse2.MyAttachVideo[] videoAttachs = new MyAttachVideo[1];
			videoAttachs[0] = new MyAttachVideo();
			videoAttachs[0].videotype = mediaStyle;
			videoAttachs[0].playvideourl = resAttach.path;
			attach.attachvideo = videoAttachs;
			attach.videocover = relativePath + ".vc";
			mappingKey = resAttach.path;
		} else if ("2".equals(attachType)) {
			if (mediaType == 3) {
				attach.attachlevel = "0";
			} else {
				attach.attachlevel = "1";
			}
			
			GsonResponse2.MyAttachAudio[] audioAttachs = new MyAttachAudio[1];
			audioAttachs[0] = new MyAttachAudio();
			audioAttachs[0].audiotype = mediaStyle;
			audioAttachs[0].audiourl = resAttach.path;
			attach.attachaudio = audioAttachs;
			mappingKey = resAttach.path;
		} else if ("3".equals(attachType)) {
			attach.attachlevel = "1";
			GsonResponse2.MyAttachImage[] imageAttachs = new MyAttachImage[1];
			imageAttachs[0] = new MyAttachImage();
			imageAttachs[0].imagetype = mediaStyle;
			imageAttachs[0].imageurl = resAttach.path;
			attach.attachimage = imageAttachs;
			mappingKey = resAttach.path;
		}
		
		attach.attachid = resAttach.attachid;
		attach.attachuuid = resAttach.attachuuid;
		
		if (attach.videocover != null) {// 登记视频封面
			String newVideoCover = MediaCoverUtils.getMediaCoverUrl(absolutePath);
			if(newVideoCover != null) {
	
				MediaValue mediaValueCover = new MediaValue();
				mediaValueCover.UID = userID;
				mediaValueCover.path = newVideoCover.replace(Environment.getExternalStorageDirectory().getPath(), "");
				mediaValueCover.totalSize = new File(newVideoCover).length();
				mediaValueCover.realSize = mediaValueCover.totalSize;
				mediaValueCover.MediaType = 2;
				mediaValueCover.url = attach.videocover;
				ZLog.printObject(mediaValueCover);
				if (MediaValue.checkMediaAvailable(mediaValueCover, 2)) {
					AccountInfo.getInstance(userID).mediamapping.setMedia(userID, mediaValueCover.url, mediaValueCover);
				}
			}
		}
		
		mediaValue.UID = userID;
		mediaValue.path = relativePath;
		mediaValue.totalSize = new File(absolutePath).length();
		mediaValue.realSize = mediaValue.totalSize;
		mediaValue.MediaType = mediaType;// 文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
		mediaValue.Direction = 2;
		mediaValue.url = resAttach.path;
		mediaValue.Belong = 1;
		
		if (MediaValue.checkMediaAvailable(mediaValue, mediaValue.MediaType)) {// 登记音频文件
			Log.d("=AAA=","mappingKey = " + mappingKey + " path = " + mediaValue.path);
			AccountInfo.getInstance(userID).mediamapping.setMedia(userID, mappingKey, mediaValue);
			if (AccountInfo.getInstance(userID).mediamapping.getMedia(userID, mediaValue.path) != null) {
				AccountInfo.getInstance(userID).mediamapping.delMedia(userID,mediaValue.path);
			}
		}
	}
	
	/**
	 * 创建日记管理结构返回失败时，填充音频附件并再MediaMapping里面做登记
	 * @param attach
	 * @param reqAttach
	 * @param audioType
	 * @param absolutePath
	 */
	public static void fillAttachAndMapping(String userID,diaryAttach attach,Attachs reqAttach,String attachType,int mediaType,String absolutePath,String mediaStyle) {
		if (absolutePath == null) {
			return;
		}
		
		attach.attachtype = attachType;
		attach.attachuuid = reqAttach.attachuuid;
		String relativePath = absolutePath.replace(HomeActivity.SDCARD_PATH, "");
		
		if ("1".equals(attachType)) {
			attach.attachlevel = "1";
			GsonResponse2.MyAttachVideo[] videoAttachs = new MyAttachVideo[1];
			videoAttachs[0] = new MyAttachVideo();
			videoAttachs[0].videotype = mediaStyle;
			attach.attachvideo = videoAttachs;
			attach.videocover = relativePath + ".vc";
		} else if ("2".equals(attachType)) {
			if (mediaType == 3) {
				attach.attachlevel = "0";
			} else {
				attach.attachlevel = "1";
			}
			
			GsonResponse2.MyAttachAudio[] audioAttachs = new MyAttachAudio[1];
			audioAttachs[0] = new MyAttachAudio();
			audioAttachs[0].audiotype = mediaStyle;
			attach.attachaudio = audioAttachs;
			attach.attachuuid = reqAttach.attachuuid;
		} else if ("3".equals(attachType)) {
			attach.attachlevel = "1";
			GsonResponse2.MyAttachImage[] imageAttachs = new MyAttachImage[1];
			imageAttachs[0] = new MyAttachImage();
			imageAttachs[0].imagetype = mediaStyle;
			attach.attachimage = imageAttachs;
		}
		
		
		if (attach.videocover != null) {// 登记视频封面
			String newVideoCover = MediaCoverUtils.getMediaCoverUrl(absolutePath);
			if(newVideoCover != null) {
	
				MediaValue mediaValueCover = new MediaValue();
				mediaValueCover.UID = userID;
				mediaValueCover.path = newVideoCover.replace(Environment.getExternalStorageDirectory().getPath(), "");
				mediaValueCover.totalSize = new File(newVideoCover).length();
				mediaValueCover.realSize = mediaValueCover.totalSize;
				mediaValueCover.MediaType = 2;
				mediaValueCover.url = attach.videocover;
				ZLog.printObject(mediaValueCover);
				if (MediaValue.checkMediaAvailable(mediaValueCover, 2)) {
					AccountInfo.getInstance(userID).mediamapping.setMedia(userID, mediaValueCover.url, mediaValueCover);
				}
			}
		}
		
		String mappingKey = "";
		
		MediaValue mediaValue = new MediaValue();
		mediaValue.UID = userID;
		mediaValue.path = relativePath;
		mediaValue.totalSize = new File(absolutePath).length();
		mediaValue.realSize = mediaValue.totalSize;
		mediaValue.MediaType = mediaType;// 文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
		mediaValue.Direction = 2;
		mappingKey = reqAttach.attachuuid;
		mediaValue.Belong = 1;
		
		if (MediaValue.checkMediaAvailable(mediaValue, mediaValue.MediaType)) {// 登记音频文件
			Log.d("=AAA=","mappingKey = " + mappingKey + " path = " + mediaValue.path);
			AccountInfo.getInstance(userID).mediamapping.setMedia(userID, mappingKey, mediaValue);
			if (AccountInfo.getInstance(userID).mediamapping.getMedia(userID, mediaValue.path) != null) {
				AccountInfo.getInstance(userID).mediamapping.delMedia(userID,mediaValue.path);
			}
		}
	}
	
	protected abstract void modifyMainAttach();
	
	protected abstract void copyMainAttach();
	
	private void modifyshortAudioAttach() {
		if (isAddSoundAttach && attachsAddSound != null && shortRecEffect != null && shortRecEffect.getEffectsCount() > 0) {
			String infilename = audioPath;
			Log.d(TAG,"infilename = " + infilename);
			if (infilename != null) {
				EffectTransCodeUtil transCode = new EffectTransCodeUtil(handler, shortRecEffect, infilename, this,EffectTransCodeUtil.SHORT_AUDIO);
				transCode.start("audio");
				try {
					shortAudioSignal.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 覆盖模式时创建日记管理结构成功，保存日记附件，登记媒体文件，上传日记
	 */
	class CreateDiarySucessedThread extends Thread {

		@Override
		public void run() {
			int attachsSize = 0;
			if (structureResponse.attachs != null) {
				attachsSize = structureResponse.attachs.length;
			}
			
			Log.d(TAG,"CreateDiarySucessedThread in");
			
			GsonResponse2.diaryAttach[] attachs = new diaryAttach[attachsSize + 1];
			ArrayList<diaryAttach> diaryAttachsList = new ArrayList<GsonResponse2.diaryAttach>();
			GsonResponse2.diaryAttach mainAttach = null;
			GsonResponse2.diaryAttach textAttach = null;
			GsonResponse2.diaryAttach shortAudioAttach = null;
			DiaryManager diarymanager = DiaryManager.getInstance();
			MyDiary myLocalDiary = diarymanager.findLocalDiaryByUuid(myDiary.diaryuuid);
			if (myLocalDiary != null) {
				diaryAttach[] diaryAttachs = myLocalDiary.attachs;
				for (diaryAttach attach:diaryAttachs) { 
					if ("1".equals(attach.attachlevel) && !isMainAttachChanged) {
						diaryAttachsList.add(attach);
					} else if ("2".equals(attach.attachtype) && "0".equals(attach.attachlevel)) {
						if (attachsAddSound == null && !isDeleteSound) {// 未添加短录音也未删除短录音
							diaryAttachsList.add(attach);
						}
					} else if ("4".equals(attach.attachtype) && attachsText == null) {
						diaryAttachsList.add(attach);
					}
				}
			}
			
			modifyMainAttach();
			
			modifyshortAudioAttach();
			
			for (int i = 0; i < attachsSize; i++) {
				MyAttach gsonAttach = structureResponse.attachs[i];
				if (attachsAddSound != null && gsonAttach.attachuuid.equals(attachsAddSound.attachuuid)) {
					String absolutePath = audioPath;
					shortAudioAttach = new diaryAttach();
					fillAttachAndMapping(userID,shortAudioAttach, gsonAttach, "2", 3, absolutePath,"1");
					if (audioDuration == 0) {
						if (shortAudioDuration != null && shortRecEffect != null && shortRecEffect.getEffectsCount() > 0) {
							shortAudioAttach.playtime = shortAudioDuration;
						} else {
							shortAudioAttach.playtime = "0";
						}
					} else {
						shortAudioAttach.playtime = String.valueOf((audioDuration + 999 )/ 1000);
					}
					diaryAttachsList.add(shortAudioAttach);
				} else if (attachsText != null && gsonAttach.attachuuid.equals(attachsText.attachuuid)){
					textAttach = new diaryAttach();
					textAttach.attachid = gsonAttach.attachid;
					textAttach.attachtype = attachsText.attach_type;
					textAttach.attachlevel = attachsText.level;
					textAttach.attachuuid = gsonAttach.attachuuid;
					textAttach.content = attachsText.content;
					if (!"".equals(textAttach.content)) {
						diaryAttachsList.add(textAttach);
					}
				} else if (attachsAddMainAudio != null && gsonAttach.attachuuid.equals(attachsAddMainAudio.attachuuid)) {
					mainAttach = new diaryAttach();
					fillAttachAndMapping(userID,mainAttach, gsonAttach, "2", 4, editAttachPath, "1");
					mainAttach.playtime = getMontageDuration();
					Log.d(TAG,"playtime = " + mainAttach.playtime);
					
					diaryAttachsList.add(mainAttach);
				} else if (attachsAddVideo != null && gsonAttach.attachuuid.equals(attachsAddVideo.attachuuid)) {
					mainAttach = new diaryAttach();
					fillAttachAndMapping(userID,mainAttach, gsonAttach, "1", 5, editAttachPath, videoType);
					mainAttach.playtime = getMontageDuration();
					Log.d(TAG,"playtime = " + mainAttach.playtime);
					
					String oldVideoCoverPath = videoAttachPath.replace(HomeActivity.SDCARD_PATH, "") + ".vc";
					MediaValue oldCoverValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, oldVideoCoverPath);
					if (oldCoverValue != null) {
						String oldFilePath = oldCoverValue.path;
						if (oldFilePath != null) {//删除原封面文件
							String abFilePath = HomeActivity.SDCARD_PATH + "/" + oldFilePath;
							delFile(abFilePath);
						}
						AccountInfo.getInstance(userID).mediamapping.delMedia(userID,oldVideoCoverPath);
					}
					videoAttachCover = mainAttach;
					diaryAttachsList.add(mainAttach);
				} else if (attachsAddPicture != null && gsonAttach.attachuuid.equals(attachsAddPicture.attachuuid)) {
					mainAttach = new diaryAttach();
					fillAttachAndMapping(userID,mainAttach, gsonAttach, "3", 2, editAttachPath, "0");
					diaryAttachsList.add(mainAttach);
					Log.d(TAG,"CreateDiarySucessedThread editAttachPath = " + editAttachPath);
				}
			}
			
			if (textAttach == null && attachsText != null) {
				textAttach = new diaryAttach();
				textAttach.attachid = "";
				textAttach.attachtype = attachsText.attach_type;
				textAttach.attachlevel = attachsText.level;
				textAttach.attachuuid = attachsText.attachuuid;
				textAttach.content = attachsText.content;
				if (!"".equals(textAttach.content)) {
					diaryAttachsList.add(textAttach);
				}
			}
			
			attachs = diaryAttachsList.toArray(new diaryAttach[diaryAttachsList.size()]);
			myLocalDiary = diarymanager.findLocalDiaryByUuid(myDiary.diaryuuid);
			if (myLocalDiary !=  null) {
				myLocalDiary.attachs = attachs;
				myLocalDiary.updatetimemilli = String.valueOf(TimeHelper.getInstance().now());
				Log.d(TAG,"save myLocalDiary not null");
				myLocalDiary.sync_status = 1;
			}
			
			
			if (myLocalDiary != null && createStructure != null) {
				myLocalDiary.request = createStructure;
			}
			
			
			NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(myLocalDiary, INetworkTask.TASK_TYPE_UPLOAD);//设置数据源
			UploadNetworkTask uploadtask = new UploadNetworkTask(networktaskinfo);//创建上传/下载任务
			NetworkTaskManager.getInstance(userID).addTask(uploadtask);//添加网络任务
			NetworkTaskManager.getInstance(userID).startNextTask();    //开始任务队列
			
//			MyDiary localDiary = diarymanager.findLocalDiary(diaryID);
//			Log.d(TAG,"attachSize = " + localDiary.attachs.length);
			
			isAddSoundAttachDone = true;
			handler.sendEmptyMessage(HANDLER_DISMISS_PROCESS_DIALOG);
		}
	}
	
	class CreateOfflineDiaryThread extends Thread {
		@Override
		public void run() {
			if (isMainAttachChanged) {
				modifyMainAttach();
			} else {
				if (mainAudioPath != null) {
					editAttachPath = mainAudioPath;
				} else if (videoAttachPath != null) {
					editAttachPath = videoAttachPath;
				} else if (pictureAttachPath != null) {
					editAttachPath = pictureAttachPath;
				}
			}
			DiaryManager diarymanager = DiaryManager.getInstance();
			MyDiary myLocalDiary = diarymanager.findLocalDiaryByUuid(myDiary.diaryuuid);
			GsonResponse2.diaryAttach[] attachs;
			ArrayList<diaryAttach> attachsList = new ArrayList<diaryAttach>();
			createNewDiaryAttach(attachsList, myLocalDiary);
			attachs = attachsList.toArray(new diaryAttach[attachsList.size()]);
			if (myLocalDiary != null) {
				myLocalDiary.diarytimemilli = String.valueOf(TimeHelper.getInstance().now());
				myLocalDiary.updatetimemilli = String.valueOf(TimeHelper.getInstance().now());
				String tags = getTags();
				myLocalDiary.setTags(tags);
				if (myDiary != null) {
					myLocalDiary.position = myDiary.position;
				}
				myLocalDiary.attachs = attachs;
				if (createStructure != null) {
					myLocalDiary.request = createStructure;
				}
			}
			// 日记上传
			if (!"0".equals(structureResponse.status)) {// 创建日记结构失败
				NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(myLocalDiary, INetworkTask.TASK_TYPE_CACHE);//设置数据源
				CacheNetworkTask cachetask = new CacheNetworkTask(networktaskinfo, createStructure, networktaskinfo.caMedias);//创建上传/下载任务
				NetworkTaskManager.getInstance(userID).addTask(cachetask);//添加网络任务
				NetworkTaskManager.getInstance(userID).startNextTask();    //开始任务队列
			} else {// 创建日记结构成功
				NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(myLocalDiary, INetworkTask.TASK_TYPE_UPLOAD);//设置数据源
				UploadNetworkTask uploadtask = new UploadNetworkTask(networktaskinfo);//创建上传/下载任务
				NetworkTaskManager.getInstance(userID).addTask(uploadtask);//添加网络任务
				NetworkTaskManager.getInstance(userID).startNextTask();    //开始任务队列
			}
			isAddSoundAttachDone = true;
			handler.sendEmptyMessage(HANDLER_DISMISS_PROCESS_DIALOG);
		}
	}
	
	public static void addAttachToList(ArrayList<Attachs> attachList,Attachs attachs) {
		if (attachs != null) {
			attachList.add(attachs);
		}
	}
	
	/**
	 * 覆盖模式时创建日记管理结构失败，保存日记附件，登记媒体文件，上传日记到缓存队列
	 */
	class CreateDiaryFailedThread extends Thread {
		@Override
		public void run() {
			GsonResponse2.diaryAttach[] attachs = null;
			GsonResponse2.diaryAttach audioAttach = null;
			GsonResponse2.diaryAttach textAttach = null;
			GsonResponse2.diaryAttach mainAttach = null;
			
			ArrayList<diaryAttach> diaryAttachsList = new ArrayList<GsonResponse2.diaryAttach>();
			Log.d(TAG,"CreateDiaryFailedThread in");
			DiaryManager diarymanager = DiaryManager.getInstance();
			MyDiary myLocalDiary = diarymanager.findLocalDiaryByUuid(myDiary.diaryuuid);
			if (myLocalDiary != null) {
				diaryAttach[] diaryAttachs = myLocalDiary.attachs;
				for (diaryAttach attach:diaryAttachs) { 
					if ("1".equals(attach.attachlevel) && !isMainAttachChanged) {
						diaryAttachsList.add(attach);
					} else if ("2".equals(attach.attachtype) && "0".equals(attach.attachlevel)) {
						if (attachsAddSound == null && !isDeleteSound) {
							diaryAttachsList.add(attach);
						}
					} else if ("4".equals(attach.attachtype) && attachsText == null) {
						diaryAttachsList.add(attach);
					}
				}
			}
			
			modifyMainAttach();
			
			if (attachsAddSound != null) {
				audioAttach = new diaryAttach();
				fillAttachAndMapping(userID,audioAttach, attachsAddSound, "2", 3, audioPath,"1");
				if (audioDuration == 0) {
					if (shortAudioDuration != null && shortRecEffect != null && shortRecEffect.getEffectsCount() > 0) {
						audioAttach.playtime = shortAudioDuration;
					} else {
						audioAttach.playtime = "0";
					}
				} else {
					audioAttach.playtime = String.valueOf((audioDuration + 999 )/ 1000);
				}
				diaryAttachsList.add(audioAttach);
			}
			
			if (attachsText != null) {
				textAttach = new diaryAttach();
				textAttach.attachtype = attachsText.attach_type;
				textAttach.attachlevel = attachsText.level;
				textAttach.attachuuid = attachsText.attachuuid;
				textAttach.content = attachsText.content;
				if (!"".equals(textAttach.content)) {
					diaryAttachsList.add(textAttach);
				}
			}
			
			if (attachsAddMainAudio != null) {
				mainAttach = new diaryAttach();
				fillAttachAndMapping(userID,mainAttach, attachsAddMainAudio, "2", 4, editAttachPath, "1");
				mainAttach.playtime = getMontageDuration();
				diaryAttachsList.add(mainAttach);
			}
			
			if (attachsAddVideo != null) {
				mainAttach = new diaryAttach();
				fillAttachAndMapping(userID,mainAttach, attachsAddVideo, "1", 5, editAttachPath, "1");
				mainAttach.playtime = getMontageDuration();
				diaryAttachsList.add(mainAttach);
				videoAttachCover = mainAttach;
				
				String oldVideoCoverPath = videoAttachPath.replace(HomeActivity.SDCARD_PATH, "") + ".vc";
				MediaValue oldCoverValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, oldVideoCoverPath);
				if (oldCoverValue != null) {
					String oldFilePath = oldCoverValue.path;
					if (oldFilePath != null) {//删除原封面文件
						String abFilePath = HomeActivity.SDCARD_PATH + "/" + oldFilePath;
						delFile(abFilePath);
					}
					AccountInfo.getInstance(userID).mediamapping.delMedia(userID,oldVideoCoverPath);
				}
			}
			
			if (attachsAddPicture != null) {
				mainAttach = new diaryAttach();
				fillAttachAndMapping(userID,mainAttach, attachsAddPicture, "3", 2, editAttachPath, "0");
				diaryAttachsList.add(mainAttach);
			}
			
			attachs = diaryAttachsList.toArray(new diaryAttach[diaryAttachsList.size()]);
			myLocalDiary = diarymanager.findLocalDiaryByUuid(myDiary.diaryuuid);
			
			if (myLocalDiary !=  null) {
				Log.d(TAG,"save myLocalDiary not null");
				myLocalDiary.attachs = attachs;
				myLocalDiary.sync_status = 0;
			}
			
			if (myLocalDiary != null && createStructure != null) {
				myLocalDiary.request = createStructure;
			}
			
//			Log.d("==WR==", "gAttachs size = " + attachsUpload.length);
			NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(myLocalDiary, INetworkTask.TASK_TYPE_CACHE);//设置数据源
			CacheNetworkTask cachetask = new CacheNetworkTask(networktaskinfo, createStructure, networktaskinfo.caMedias);//创建上传/下载任务
			NetworkTaskManager.getInstance(userID).addTask(cachetask); //添加网络任务
			NetworkTaskManager.getInstance(userID).startNextTask();    //开始任务队列
			
			isAddSoundAttachDone = true;
			handler.sendEmptyMessage(HANDLER_DISMISS_PROCESS_DIALOG);
		}
		
	}
	
	/**
	 * 复制文件到指定路径
	 */
	public static long copyFile2(String srcFilePath, String dstFilePath) {  
        long copySizes = 0;  
        if (srcFilePath == null) {
        	return -1;
        }
        File srcFile = new File(srcFilePath);
        if (!srcFile.exists()) {  
            System.out.println("源文件不存在");  
            return -1;  
        } else if (dstFilePath == null) {
        	return -1;
        }
        File dstFile = new File(dstFilePath);
        if(!dstFile.getParentFile().exists()) {
        	dstFile.getParentFile().mkdirs();
		}
        if (!dstFile.exists()) {
        	try {
				if (!dstFile.createNewFile()) {
					return -1;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        try {  
            FileChannel fcin = new FileInputStream(srcFile).getChannel();  
            FileChannel fcout = new FileOutputStream(dstFile).getChannel();  
            long size = fcin.size();  
            fcin.transferTo(0, fcin.size(), fcout);  
            fcin.close();  
            fcout.close();  
            copySizes = size;  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return copySizes; 
	}
	
	protected void initAttachs() {
		Log.d(TAG,"initAttachs in");
		userID =  ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		
		if (diaryString != null && diaryUUID != null) {
			myDiary = new Gson().fromJson(diaryString, MyDiary.class);
			if (myDiary != null) {
				diaryAttach[] attachs = myDiary.attachs;
				for (diaryAttach attach:attachs) {
					if ("2".equals(attach.attachtype) && "0".equals(attach.attachlevel)) {
						MediaValue mediaValue = null;
						String keyStr = null;
						if (attach.attachaudio != null && attach.attachaudio.length > 0 ) {
							for(MyAttachAudio audioAttach:attach.attachaudio) {
								if (audioAttach.audiourl != null && !"".equals(audioAttach.audiourl)) {
									keyStr = audioAttach.audiourl;
									mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, keyStr);
									if (mediaValue != null) {
										break;
									}
								}
							}
						}
						if (keyStr == null) {
							keyStr = attach.attachuuid;
							mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, keyStr);
						}
						Log.d(TAG,"keyStr = " + keyStr);
						
						if (mediaValue != null) {
							audioPath = HomeActivity.SDCARD_PATH + mediaValue.path;
							tackView.setVisibility(View.VISIBLE);
							tackView.setPlaytime(attach.playtime + "\"");
							shortAudioDuration = attach.playtime;
							hasShortSound = true;
						}
						
						Log.d(TAG,"audioPath = " + audioPath);
					} else if ("4".equals(attach.attachtype)) {
						setSoundText(attach.content);
						edit.setSelection(getSoundText().length());
						textContent = attach.content;
						hasText = true;
					} else if ("1".equals(attach.attachtype)) {
						String keyStr = null;
						MediaValue mediaValue = null;
						if (attach.attachvideo != null && attach.attachvideo.length > 0 ) {
							for (MyAttachVideo videoAttach:attach.attachvideo) {
								if (videoAttach.playvideourl != null && !"".equals(videoAttach.playvideourl)) {
									keyStr = videoAttach.playvideourl;
									videoType = videoAttach.videotype;
									
									mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, keyStr);
									if (mediaValue != null) {
										break;
									}
								}
							}
						} else {
							keyStr = attach.attachuuid;
							videoType = "1";
						}
						
						if (keyStr == null) {
							keyStr = attach.attachuuid;
							mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, keyStr);
						}
						videoFrontCoverUrl = attach.videocover;
						videoAttachCover = attach;
						
						if (mediaValue != null) {
							videoAttachPath = HomeActivity.SDCARD_PATH + mediaValue.path;
							hasMainAttach = true;
						} else {
							Prompt.Alert(this, "视频不存在");
							finish();
							return;
						}
						Log.d(TAG,"videokeyStr = " + keyStr + " videoFrontCoverUrl = " + videoFrontCoverUrl + " videoAttachPath = " + videoAttachPath);
						videoDuration = attach.playtime;
						if (videoDuration == null) {
							videoDuration = "0";
						}
						montageStartTime = 0;
						try {
							montageEndTime = Integer.valueOf(videoDuration);
						} catch (Exception e) {
							montageEndTime = 0;
							videoDuration = "0";
						}
						Log.d(TAG,"initAttach videoDuration = " + videoDuration);
					} else if ("3".equals(attach.attachtype)) {
						String keyStr = null;
						pictureType = "0";
						if (attach.attachimage != null && attach.attachimage.length > 0) {
							for (MyAttachImage imageAttach:attach.attachimage) {
								if ("0".equals(imageAttach.imagetype) && imageAttach.imageurl != null && !"".equals(imageAttach.imageurl)) {
									keyStr = imageAttach.imageurl;
									pictureType = imageAttach.imagetype;
									pictureType = "0";
								}
							}
						} 
						
						if (keyStr == null) {
							keyStr = attach.attachuuid;
						}
						
						MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, keyStr);
						if (mediaValue != null) {
							pictureAttachPath = HomeActivity.SDCARD_PATH + mediaValue.path;
							hasMainAttach = true;
						} else {
							Prompt.Alert(this, "图片不存在");
							finish();
							return;
						}
						Log.d(TAG,"imageattach keyStr = " + keyStr + " pictureAttachPath = " + pictureAttachPath);
					} else if ("2".equals(attach.attachtype) && "1".equals(attach.attachlevel)) {
						String keyStr = null;
						MediaValue mediaValue = null;
						if (attach.attachaudio != null && attach.attachaudio.length > 0 ) {
							for(MyAttachAudio audioAttach:attach.attachaudio) {
								if ("1".equals(audioAttach.audiotype) && audioAttach.audiourl != null && !"".equals(audioAttach.audiourl)) {
									keyStr = audioAttach.audiourl;
									mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, keyStr);
									if (mediaValue != null) {
										break;
									}
								}
							}
						} 
						
						if (keyStr == null) {
							keyStr = attach.attachuuid;
							mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, keyStr);
						}
						
						if (mediaValue != null) {
							mainAudioPath = HomeActivity.SDCARD_PATH + mediaValue.path;
							hasMainAttach = true;
						} else {
							Prompt.Alert(this, "主音频不存在");
							finish();
							return;
						}
						
						mainAudioDuration = attach.playtime;
						Log.d(TAG,"longaudioAttach keyStr = " + keyStr + " mainAudioPath = " + mainAudioPath + " mainAudioDuration = " + mainAudioDuration);
						if (mainAudioDuration == null) {
							mainAudioDuration = "0";
						}
						montageStartTime = 0;
						try {
							montageEndTime = Integer.valueOf(mainAudioDuration);
						} catch (Exception e) {
							montageEndTime = 0;
							mainAudioDuration = "0";
						}
					}
				}
				String dateString = DateUtils.getStringFromMilli(myDiary.updatetimemilli, "yyyy-MM-dd HH:mm");
				if (dateString != null && !"".equals(dateString)) {
					dateTv.setText(dateString);
				}
			}
			
			shortRecEffectUtils = new EffectUtils(this);
			shortRecEffectUtils.parseXml("effectcfg/effectlist.xml");
			shortRecEffectBeans = shortRecEffectUtils.getEffects(EffectBean.TYPE_EFFECTS_AUDIO);
			if (PluginUtils.isPluginMounted()) {
				shortRecEffect = new XEffects();
			}
			shortRecMediaPlayer = new XMediaPlayer(this, shortRecEffect, true);
			Log.d(TAG, "diaryID=" + diaryUUID);
		} else {
			Prompt.Alert(this, "日记不存在");
			finish();
			Log.e(TAG, "diaryID is null");
			Log.e(TAG, "diaryArray is null");
		}
		isAttachInited = true;
		
		
		/*if (textContent != null) {
			setSoundText(textContent);
		}*/
		
	}
	
	protected abstract String getMontageDuration();
	
	public static boolean isFileExists(String filepath) {
		if (filepath == null || "".equals(filepath)) {
			return false;
		}
		File file = new File(filepath);
		return file.exists();
	}
	/**
	 * 修改tag和位置信息
	 */
	public void modTagsOrPosition() {
		if (diaryUUID == null || myDiary == null) {
			return;
		}
		
		DiaryManager diarymanager = DiaryManager.getInstance();
		MyDiary myLocalDiary = diarymanager.findLocalDiaryByUuid(myDiary.diaryuuid);
		if (!getTagString(myDiary).equals(getTagString(myLocalDiary)) 
				|| (myDiary.position != null && !myDiary.position.equals(myLocalDiary.position))) {
			isDiaryDetailChanged = true;
		} else {
			isDiaryDetailChanged = false;
		}
		
		if (isDiaryDetailChanged) {
			String tags = getTags();
			isTagPositionChangeDone = false;
			if (myLocalDiary !=  null) {
				Log.d(TAG,"save myLocalDiary not null");
				myLocalDiary.position = myDiary.position;
				myLocalDiary.setTags(tags);
			}
			OfflineTaskManager.getInstance().addPositionOrTagTask("",myDiary.diaryuuid, tags, myDiary.position);
//			Requester2.modTagsOrPosition(handler, diaryID, tags, myDiary.position);
		}
	}
	
	/**
	 * 添加附件请求，用在覆盖模式
	 */
	public void addAttach() {
		Log.d(TAG,"addAttach in **************");
		if (diaryUUID == null) {
			return;
		}
		
		isAddSoundAttachDone = false;
		boolean isHasTextAttach = false;
		ArrayList<Attachs> attachsList = new ArrayList<GsonRequest2.Attachs>();
		String mainAttachType = "";
		
		if (myDiary != null) {
			diaryAttach[] attachs = myDiary.attachs;
			for (diaryAttach attach:attachs) { // 查找日记附件
				if ("2".equals(attach.attachtype) && "0".equals(attach.attachlevel) && (isAddSoundAttach || isDeleteSound)) {// 短录音附件
					attachsDeleteShortAudio = new Attachs();
					fillDeleteAttachs(attachsDeleteShortAudio, attach);
					attachsList.add(attachsDeleteShortAudio);
				} else if ("4".equals(attach.attachtype) 
						&& getSoundText() != null && !getSoundText().equals(attach.content)) {// 文字附件
					attachsText = new Attachs();
					attachsText.attachid = attach.attachid;
					attachsText.attachuuid = attach.attachuuid;
					attachsText.attach_type = attach.attachtype;
					attachsText.level = attach.attachlevel;
					attachsText.content = getSoundText();
					if ("".equals(getSoundText())) {
						attachsText.Operate_type = "3";
					} else {
						attachsText.Operate_type = "2";// 若以前有文字描述附件，则更新
					}
					isHasTextAttach = true;
					attachsList.add(attachsText);
				} else if ("1".equals(attach.attachtype) && "1".equals(attach.attachlevel)) {// 主视频附件
					if (isMainAttachChanged) {
						attachsDeleteVideo = new Attachs();
						fillDeleteAttachs(attachsDeleteVideo, attach);
						attachsList.add(attachsDeleteVideo);
						mainAttachType = "1";
					}
				} else if ("2".equals(attach.attachtype) && "1".equals(attach.attachlevel)) {// 主音频附件
					if (isMainAttachChanged) {
						attachsDeleteMainAudio = new Attachs();
						fillDeleteAttachs(attachsDeleteMainAudio,attach);
						attachsList.add(attachsDeleteMainAudio);
						mainAttachType = "2";
					}
				} else if ("3".equals(attach.attachtype) && "1".equals(attach.attachlevel)) {// 主图片附件
					if (isMainAttachChanged) {
						attachsDeletePicture = new Attachs();
						fillDeleteAttachs(attachsDeletePicture, attach);
						attachsList.add(attachsDeletePicture);
						mainAttachType = "3";
					}
				}
			}
		}
		
		if (getSoundText() != null && !getSoundText().equals(textContent) && !isHasTextAttach) {// 添加文字附件
			attachsText = new Attachs();
			attachsText.attachid = "";
			attachsText.attachuuid = UUID.randomUUID().toString().replace("-", "");
			attachsText.attach_type = "4";
			attachsText.level = "0";
			attachsText.content = getSoundText();
			if (CommonInfo.getInstance().myLoc != null) {
				attachsText.attach_latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
				attachsText.attach_logitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			}
			
			attachsText.Operate_type = "1";// 若以前没有文字描述附件，则添加文字附件
			attachsList.add(attachsText);
		}
		
		if (isAddSoundAttach) {
			attachsAddSound = new Attachs();
			fillAddAttachs(attachsAddSound, "2", "1", ".mp4","0");
			attachsList.add(attachsAddSound);
		}
		
		if (isMainAttachChanged) {
			if ("1".equals(mainAttachType)) {
				attachsAddVideo = new Attachs();
				fillAddAttachs(attachsAddVideo, mainAttachType, videoType, ".mp4", "1");
				attachsList.add(attachsAddVideo);
			} else if ("2".equals(mainAttachType)) {
				attachsAddMainAudio = new Attachs();
				fillAddAttachs(attachsAddMainAudio, mainAttachType, "1", ".mp4", "1");
				attachsList.add(attachsAddMainAudio);
			} else if ("3".equals(mainAttachType)) {
				attachsAddPicture = new Attachs();
				fillAddAttachs(attachsAddPicture, mainAttachType, "0", ".jpg", "1");
				attachsList.add(attachsAddPicture);
			}
		}
		
		String userSelectedPosition = null;
		if (myDiary != null) {
		    userSelectedPosition = myDiary.position;
		}
		
		Attachs[] attachs = null;
		attachs = attachsList.toArray(new Attachs[attachsList.size()]);
		
		String longitude = "";
		String latitude = "";
		if (CommonInfo.getInstance().myLoc != null) {
			longitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
		}
		String addressCode = "";
		AccountInfo account = AccountInfo.getInstance(userID);
		MyWeather weather = account.myWeather;
		DiaryManager diarymanager = DiaryManager.getInstance();
		MyDiary myLocalDiary = diarymanager.findLocalDiaryByUuid(myDiary.diaryuuid);
		if (myLocalDiary != null) {
			myDiary.diaryid = myLocalDiary.diaryid;
		}
		if (weather != null) {
			addressCode = weather.addrCode;
		}
		if (attachs != null && attachs.length > 0) {
			Log.d(TAG,"addAttach createStructure");
			createStructure = Requester2.createStructure(handler,  myDiary.diaryid, myDiary.diaryuuid, "2", "", "",
					"","","",userSelectedPosition,longitude,latitude,"",
					addressCode,attachs);
		} else {
			isAddSoundAttachDone = true;
		}
	}
	
	/**
	 * 填充删除附件结构（主附件的删除用在覆盖模式）
	 * @param reqAttach
	 * @param attach
	 */
	public static void fillDeleteAttachs(Attachs reqAttach,diaryAttach attach) {
		if (reqAttach == null || attach == null) {
			return;
		}
		reqAttach.attachid = attach.attachid;
		reqAttach.attachuuid = attach.attachuuid;
		reqAttach.attach_type = attach.attachtype;
		reqAttach.level = attach.attachlevel;
		if (attach.attachaudio != null && attach.attachaudio.length > 0) {
			reqAttach.audio_type = attach.attachaudio[0].audiotype;
		}
		if (attach.attachvideo != null && attach.attachvideo.length > 0) {
			reqAttach.video_type = attach.attachvideo[0].videotype;
		}
		if (attach.attachimage != null && attach.attachimage.length > 0) {
			reqAttach.photo_type = attach.attachimage[0].imagetype;
		}
		reqAttach.Operate_type = "3";// 删除主附件
	}
	
	/**
	 * 填充附件请求结构
	 * @param reqAttach 附件请求结构
	 * @param attachType 附件类型
	 * @param mediaType 媒体类型
	 * @param suffix 媒体后缀名
	 * @param level 附件等级（主，辅）
	 */
	public static void fillAddAttachs(Attachs reqAttach,String attachType,String mediaType,String suffix,String level) {
		reqAttach.attachid = "";
		reqAttach.attachuuid = UUID.randomUUID().toString().replace("-", "");
		reqAttach.attach_type = attachType;
		reqAttach.level = level;
		if ("1".equals(attachType)) {
			reqAttach.video_type = mediaType;
		} else if ("2".equals(attachType)) {
			reqAttach.audio_type = mediaType;
		} else if ("3".equals(attachType)) {
			reqAttach.photo_type = mediaType;
		}
		if (CommonInfo.getInstance().myLoc != null) {
			reqAttach.attach_latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
			reqAttach.attach_logitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
		}
		reqAttach.suffix = suffix;
		reqAttach.Operate_type = "1";
	}
	
	/** 
	 * 删除单个文件 
	 * @param   sPath    被删除文件的文件名 
	 * @return 单个文件删除成功返回true，否则返回false 
	 */  
	public static boolean delFile(String sPath) {  
	    boolean flag = false;  
	    File file = new File(sPath);
	    File parentFile = file.getParentFile();
	    // 路径为文件且不为空则进行删除  
	    if (file.isFile() && file.exists()) {
	        file.delete();  
	        flag = true;  
	    }
	    File[] files = parentFile.listFiles();  
	    if (files.length == 0) {
	    	parentFile.delete();
	    }
	    
	    return flag;  
	} 
	
	public static void deleteFile(File file){ 
		if(file.exists()){ 
			if(file.isFile()){ 
				file.delete(); 
			} else if(file.isDirectory()) {
				File files[] = file.listFiles(); 
				for(int i=0;i<files.length;i++) { 
					deleteFile(files[i]); 
				}
			}
			file.delete();
		} else { 
			System.out.println("所删除的文件不存在！"+'\n'); 
		} 
	} 
	
	public static GsonRequest2.createStructureRequest addShortRecordAttach(Handler handler,MyDiary myDiary,
			String textContent,String absolutePath,String playTime) {
		Log.d("Z_TAG","EditMediaDetailActivity addShortRecordAttach");
		if (handler == null || myDiary == null) {
			return null;
		}
		
		String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		ArrayList<Attachs> attachsList = new ArrayList<GsonRequest2.Attachs>();
		
		MyDiary localDiary = DiaryManager.getInstance().findLocalDiaryByUuid(myDiary.diaryuuid);
		if (localDiary != null && localDiary.sync_status == 0) {
			NetworkTaskManager netTaskManager = NetworkTaskManager.getInstance(uid);
			if (myDiary != null) {
				netTaskManager.removeTask(myDiary.diaryuuid);
			}
			createStructureRequest offlineRequest = localDiary.request;
			if (offlineRequest != null) {
				Attachs [] attachs = offlineRequest.attachs;
				if (attachs != null) {
					for (Attachs attach:attachs) {
						attachsList.add(attach);
					}
				}
			}
		}
		Attachs shortRecordAttach = new Attachs();
		fillAddAttachs(shortRecordAttach, "2", "1", ".mp4","0");
		attachsList.add(shortRecordAttach);
		if (textContent != null && !"".equals(textContent)) {
			Attachs textAttach = new Attachs();
			fillAddTextAttach(textAttach,textContent);
			attachsList.add(textAttach);
		}
		
		Attachs[] attachs = null;
		attachs = attachsList.toArray(new Attachs[attachsList.size()]);
		String longitude = "";
		String latitude = "";
		if (CommonInfo.getInstance().myLoc != null) {
			longitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
		}
		String addressCode = "";
		
		AccountInfo account = AccountInfo.getInstance(uid);
		MyWeather weather = account.myWeather;
		if (weather != null) {
			addressCode = weather.addrCode;
		}
		if (attachs != null && attachs.length > 0) {
			String operateType = "";
			if (localDiary != null && localDiary.sync_status != 0) {
				operateType = "2";
			} else {
				operateType = "1";
			}
			createStructureRequest addSoundStructure = Requester2.createStructure(handler,  myDiary.diaryid, myDiary.diaryuuid, operateType, "", "",
					"","","",myDiary.position,longitude,latitude,"",
					addressCode,attachs);
			Log.d("EditMediaDetailActivity","createStructureRequest in");
			addShortRecordDiaryAttach(addSoundStructure, myDiary, absolutePath, playTime);
			return addSoundStructure;
		}
		return null;
	}
	
	public static boolean addShortRecordDiaryAttach(createStructureRequest addSoundStructure,MyDiary myDiary,String absolutePath,String playTime) {
		if (myDiary == null || addSoundStructure == null) {
			return false;
		}
		MyDiary localDiary = DiaryManager.getInstance().findLocalDiaryByUuid(myDiary.diaryuuid);
		if (localDiary == null) {
			Log.d("DiaryDetaiActivity","not find local diary");
			return false;
		}
		Attachs [] attachs = null;
		Attachs shortAttach = null;
		Attachs textAttach = null;
		
		//遍历请求结构
		if (addSoundStructure != null) {
			attachs = addSoundStructure.attachs;
			for (Attachs attach:attachs) {
				if ("2".equals(attach.attach_type) && "0".equals(attach.level)) {
					shortAttach = attach;
				} else if ("4".equals(attach.attach_type)) {
					textAttach = attach;
				}
			}
		}
		
		String userID =  ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		diaryAttach shortRecAttach = null;
		diaryAttach soundTextAttach = null;
		if (shortAttach != null) {
			shortRecAttach = new diaryAttach();
			fillAttachAndMapping(userID, shortRecAttach, shortAttach, "2", 3, absolutePath, "1");
			shortRecAttach.playtime = playTime;
		}
		
		if (textAttach != null) {
			soundTextAttach = new diaryAttach();
			soundTextAttach.attachid = "";
			soundTextAttach.attachuuid = textAttach.attachuuid;
			soundTextAttach.attachtype = "4";
			soundTextAttach.attachlevel = "0";
			soundTextAttach.content = textAttach.content;
		}
		
		ArrayList<diaryAttach> diaryAttachList = new ArrayList<GsonResponse2.diaryAttach>();
		for (diaryAttach diaryatc:localDiary.attachs) {
			if ("1".equals(diaryatc.attachlevel)) {
				diaryAttachList.add(diaryatc);
				break;
			}
		}
		
		if (shortRecAttach != null) {
			diaryAttachList.add(shortRecAttach);
		}
		
		if (soundTextAttach != null) {
			diaryAttachList.add(soundTextAttach);
		}
		
		diaryAttach [] diaryAttachs = diaryAttachList.toArray(new diaryAttach[diaryAttachList.size()]);
		localDiary.attachs = diaryAttachs;
		localDiary.request = addSoundStructure;
		DiaryManager.getInstance().diaryDataChanged(localDiary.diaryuuid);
		return true;
	}
	
	public static boolean addShortRecordDiaryAttach(createStructureResponse structureResponse,
			createStructureRequest addSoundStructure,MyDiary myDiary,String absolutePath,String playTime) {
		if (myDiary == null || structureResponse == null) {
			return false;
		}
		MyDiary localDiary = DiaryManager.getInstance().findLocalDiaryByUuid(myDiary.diaryuuid);
		if (localDiary == null) {
			Log.d("DiaryDetaiActivity","not find local diary");
			return false;
		}
		
		int attachsSize = 0;
		if (structureResponse.attachs != null) {
			attachsSize = structureResponse.attachs.length;
		}
		String userID =  ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		Attachs [] attachs = null;
		Attachs shortAttach = null;
		Attachs textAttach = null;
		Attachs mainAttach = null;
		String attachType = "";
		
		//遍历请求结构
		if (addSoundStructure != null) {
			attachs = addSoundStructure.attachs;
			for (Attachs attach:attachs) {
				if ("2".equals(attach.attach_type) && "0".equals(attach.level)) {
					shortAttach = attach;
				} else if ("4".equals(attach.attach_type)) {
					textAttach = attach;
				} else if ("1".equals(attach.level)) {
					mainAttach = attach;
				}
			}
		}
		// 遍历返回结构
		diaryAttach shortRecAttach = null;
		diaryAttach soundTextAttach = null;
		diaryAttach diaryMainAttach = null;
		
		for (diaryAttach diaryatc:localDiary.attachs) {
			if ("1".equals(diaryatc.attachlevel)) {
				diaryMainAttach = diaryatc;
				break;
			}
		}
		
		if ("0".equals(structureResponse.status)) {
			for (int i = 0; i < attachsSize; i++) {
				MyAttach gsonAttach = structureResponse.attachs[i];
				if (shortAttach != null && gsonAttach.attachuuid.equals(shortAttach.attachuuid)) {
					shortRecAttach = new diaryAttach();
					fillAttachAndMapping(userID,shortRecAttach, gsonAttach, "2", 3, absolutePath,"1");
					shortRecAttach.playtime = playTime;
				} else if (textAttach != null && gsonAttach.attachuuid.equals(textAttach.attachuuid)) {
					soundTextAttach = new diaryAttach();
					soundTextAttach.attachid = gsonAttach.attachid;
					soundTextAttach.attachuuid = gsonAttach.attachuuid;
					soundTextAttach.attachtype = "4";
					soundTextAttach.attachlevel = "0";
					soundTextAttach.content = textAttach.content;
				} else if (mainAttach != null && gsonAttach.attachuuid.equals(mainAttach.attachuuid)) {
					fillMediaAttach(diaryMainAttach, gsonAttach, userID);
				}
			}
		} else {
			if (shortAttach != null) {
				shortRecAttach = new diaryAttach();
				fillAttachAndMapping(userID, shortRecAttach, shortAttach, "2", 3, absolutePath, "1");
				shortRecAttach.playtime = playTime;
			}
		}
		
		
		if (soundTextAttach == null && textAttach != null) {
			soundTextAttach = new diaryAttach();
			soundTextAttach.attachid = "";
			soundTextAttach.attachuuid = textAttach.attachuuid;
			soundTextAttach.attachtype = "4";
			soundTextAttach.attachlevel = "0";
			soundTextAttach.content = textAttach.content;
		}
		
		ArrayList<diaryAttach> diaryAttachList = new ArrayList<GsonResponse2.diaryAttach>();

		if (diaryMainAttach != null) {
			diaryAttachList.add(diaryMainAttach);
		}
		
		if (shortRecAttach != null) {
			diaryAttachList.add(shortRecAttach);
		}
		
		if (soundTextAttach != null) {
			diaryAttachList.add(soundTextAttach);
		}
		
		diaryAttach [] diaryAttachs = diaryAttachList.toArray(new diaryAttach[diaryAttachList.size()]);
		localDiary.attachs = diaryAttachs;
		localDiary.request = addSoundStructure;
		
		if ("0".equals(structureResponse.status)) {
			NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(localDiary, INetworkTask.TASK_TYPE_UPLOAD);//设置数据源
			UploadNetworkTask uploadtask = new UploadNetworkTask(networktaskinfo);//创建上传/下载任务
			NetworkTaskManager.getInstance(userID).addTask(uploadtask);//添加网络任务
			NetworkTaskManager.getInstance(userID).startNextTask();    //开始任务队列
		} else {
			NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(localDiary, INetworkTask.TASK_TYPE_CACHE);//设置数据源
			CacheNetworkTask cachetask = new CacheNetworkTask(networktaskinfo, addSoundStructure, networktaskinfo.caMedias);//创建上传/下载任务
			NetworkTaskManager.getInstance(userID).addTask(cachetask);//添加网络任务
			NetworkTaskManager.getInstance(userID).startNextTask();    //开始任务队列
		}
//		DiaryManager.getInstance().diaryDataChanged(localDiary.diaryid);
		return true;
	}
	
	public static void fillMediaAttach(diaryAttach diaryatc,MyAttach myAttach,String userID) {
		if (diaryatc == null || myAttach == null) {
			return;
		}
		MediaValue mediaValue = null;
		if ("1".equals(diaryatc.attachtype)) {
			MyAttachVideo[] videoAttachs = diaryatc.attachvideo;
			for(MyAttachVideo attach:videoAttachs) {
				mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, diaryatc.attachuuid);
				attach.playvideourl = myAttach.path;
				if (mediaValue != null) {
					AccountInfo.getInstance(userID).mediamapping.setMedia(userID, myAttach.path, mediaValue);
					break;
				}
			}
			
		} else if ("2".equals(diaryatc.attachtype)) {
			MyAttachAudio[] audioAttachs = diaryatc.attachaudio;
			for(MyAttachAudio attach:audioAttachs) {
				mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, diaryatc.attachuuid);
				attach.audiourl = myAttach.path;
				if (mediaValue != null) {
					AccountInfo.getInstance(userID).mediamapping.setMedia(userID, myAttach.path, mediaValue);
					break;
				}
			}
			
		} else if ("3".equals(diaryatc.attachtype)) {
			MyAttachImage[] imageAttachs = diaryatc.attachimage;
			for(MyAttachImage attach:imageAttachs) {
				mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, diaryatc.attachuuid);
				attach.imageurl = myAttach.path;
				if (mediaValue != null) {
					AccountInfo.getInstance(userID).mediamapping.setMedia(userID, myAttach.path, mediaValue);
					break;
				}
			}
		}
	}
	
	/**
	 * 保存本地日记并退出编辑页
	 */
	protected void saveLocalDiaryAndFinish() {
		if (isAddSoundAttachDone && /*isTagPositionChangeDone && */isExtraModifyDone) {
			if (myDiary != null) {
				if (getString(R.string.edit_media_cover).equals(getEditDiaryStyle())) {
					DiaryManager.getInstance().diaryDataChanged(myDiary.diaryuuid);
				} else {
					/*String resourcediaryid = "";
					if (myDiary.resourcediaryid != null && !"".equals(myDiary.resourcediaryid)) {
						resourcediaryid = myDiary.resourcediaryid;
					} else {
						resourcediaryid = myDiary.diaryid;
					}*/
					DiaryManager.getInstance().diaryListChanged(newDiaryuuid);
				}
			}
			ZDialog.dismiss();
			Prompt.Alert(this, "编辑成功");
			finish();
		}
	}
	
	// 点击显示下拉框
	protected void showDuplicateDiaryName() {
//		if (myDiary.resourceuuid != null && !"".equals(myDiary.resourceuuid) && !myDiary.resourceuuid.equals(myDiary.diaryuuid)) {
//			return;
//		}
		if (portraitMenu == null) {
			diaryStyle.add(getString(R.string.edit_media_cover));
			diaryStyle.add(getString(R.string.edit_media_saveas));
			final String[] strDiaryNames = diaryStyle
					.toArray(new String[diaryStyle.size()]);
			View view = inflater.inflate(
					R.layout.activity_diarydetail_duplicate_menu, null);
			ListView duplicateList = (ListView) view
					.findViewById(R.id.lv_duplicate_list);
			duplicateList.setAdapter(new ArrayAdapter<String>(this,
					R.layout.activity_activitydetail_duplicate_list_item,
					R.id.tv_item, strDiaryNames));
			duplicateList.setSelected(true);
			duplicateList.setSelector(R.drawable.xiala_xuanzhong);
			duplicateList.setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// 切换到对应的副本日记页
					if (position >= 0) {
						diaryEditStyle.setText(diaryStyle.get(position));
						portraitMenu.dismiss();
					}
				}
			});
			portraitMenu = new PopupWindow(view, rlTitleBar.getWidth() / 3,
					LayoutParams.WRAP_CONTENT, true);
		}
		portraitMenu.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		portraitMenu.showAsDropDown(rlTitleBar, rlTitleBar.getWidth() / 3,0);
		portraitMenu.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
			}
		});
	}
	
	public void showPluginDownloadDialog() {
		ZDialog.dismiss();
		ZDialog.show(R.layout.dialog_ask_download_plugin, true, true, this);
		ZDialog.getZViewFinder().findTextView(R.id.yes_download_plugin).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		ZDialog.getZViewFinder().findTextView(R.id.no_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ZDialog.dismiss();
			}
		});
	}
	
	public abstract void setSoundTrackText(String text);
	
	public abstract void setBeautifulSoundText(String text);
}
