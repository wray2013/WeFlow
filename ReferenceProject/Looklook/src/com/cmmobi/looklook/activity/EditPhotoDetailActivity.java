package com.cmmobi.looklook.activity;

import java.util.List;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.utils.ZGraphics;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.createStructureResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.modTagsOrPositionResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.BitmapUtils;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.EffectTransCodeUtil;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.MyPositionLayout;
import com.cmmobi.looklook.fragment.EditMediaBeautifySoundFragment;
import com.cmmobi.looklook.fragment.EditMediaPositionFragment;
import com.cmmobi.looklook.fragment.EditMediaTagFragment;
import com.cmmobi.looklook.fragment.EditMediaTextInputFragment;
import com.cmmobi.looklook.fragment.EditPhotoCropFragment;
import com.cmmobi.looklook.fragment.EditPhotoDetailMainFragment;
import com.cmmobi.looklook.fragment.EditPhotoEffectFragment;
import com.cmmobi.looklook.fragment.EditPhotoMainFragment;
import com.cmmobi.looklook.info.location.MyLocationInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.map.MyMapView;
import com.cmmobivideo.utils.EffectBean;
import com.cmmobivideo.utils.EffectUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.utils.XUtils;

import effect.EffectType;
import effect.XEffects;

public class EditPhotoDetailActivity extends EditMediaDetailActivity implements OnClickListener, Callback{
	
	private final String TAG = "EditPhotoDetailActivity";
	private FragmentManager fm;
	private EditPhotoDetailMainFragment detailMainFragment;
	private EditMediaTagFragment tagFragment;
	private EditMediaPositionFragment posFragment;
	private EditMediaBeautifySoundFragment soundFragment;
	private EditMediaTextInputFragment textFragment;
	private EditPhotoMainFragment mainFragment;
	private EditPhotoCropFragment cropFragment;
	private EditPhotoEffectFragment effectFragment;
	public final static int FRAGMENT_CROP_VIEW = 0xff;// 图片裁剪fragment
	public final static int FRAGMENT_EFFECT_VIEW = 0xfe;// 图片特效fragment
	public LinearLayout mainLayout = null;
	public LinearLayout detailMainLayout = null;
	private ImageView thumbImg = null;
	
	public ImageView previewImage = null;
	public Bitmap originBitmap = null;
	public Bitmap effectBitmap = null;
	public int effectId = 0;
	public boolean isPhotoCroped = false;
	public boolean isAddEffect = false;
	
	public Rect mRect = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_photo_detail);
		
		detailMainFragment = new EditPhotoDetailMainFragment();
		tagFragment = new EditMediaTagFragment();
		posFragment = new EditMediaPositionFragment();
		soundFragment = new EditMediaBeautifySoundFragment();
		textFragment = new EditMediaTextInputFragment();
		mainFragment = new EditPhotoMainFragment();
		cropFragment = new EditPhotoCropFragment();
		effectFragment = new EditPhotoEffectFragment();
		
		fm = getSupportFragmentManager();
		
		ivBack = (ImageView) findViewById(R.id.iv_back);
		ivDone = (ImageView) findViewById(R.id.iv_edit_done);
		ivBack.setOnClickListener(this);
		ivDone.setOnClickListener(this);
		
		thumbImg = (ImageView) findViewById(R.id.iv_edit_photo_thumbnails);
		thumbImg.setOnClickListener(this);
		
		previewImage = (ImageView) findViewById(R.id.iv_edit_photo_preview);
		
		myLocInfo = MyLocationInfo.getInstance(this);
		myLoc =  myLocInfo.getLocation();

		initView(this);
		mMapView = (MyMapView) findViewById(R.id.bmv_activity_edit_photo_detail_position);
		mMapView.setVisibility(View.GONE);
//        mLocOverlay = new MyLocationOverlay(getMapView());
//		mLocOverlay.enableCompass();
//		mLocOverlay.setMarker(getResources().getDrawable(R.drawable.wodeweizhi));
		myPositionLayout = (MyPositionLayout) findViewById(R.id.rl_activity_edit_media_my_position);
		myPositionLayout.setVisibility(View.GONE);
		myPositionLayout.setWillNotDraw(false);
		myPositionTv = (TextView) findViewById(R.id.tv_edit_media_myposition);
		myPositionTv.setVisibility(View.GONE);
		
		mainLayout = (LinearLayout) findViewById(R.id.ll_activity_edit_photo_main);
		detailMainLayout = (LinearLayout) findViewById(R.id.ll_activity_edit_photo_detail_main);
		
		mainLayout.setOnClickListener(this);
		diaryUUID = getIntent().getStringExtra(
				DiaryDetailActivity.INTENT_ACTION_DIARY_UUID);
		diaryString = getIntent().getStringExtra(
				DiaryDetailActivity.INTENT_ACTION_DIARY_STRING);
		
		initAttachs();
		
		ViewTreeObserver vto1 = previewImage.getViewTreeObserver();   
		vto1.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
			@Override   
			public void onGlobalLayout() { 
				previewImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				if (pictureAttachPath != null) {
			    	int height = previewImage.getHeight(); 
			    	int width  = previewImage.getWidth(); 
			    	
			    	originBitmap = BitmapUtils.readBitmapAutoSize(pictureAttachPath, width, height);
			    	int orientaiton = XUtils.getExifOrientation(pictureAttachPath);
			    	
			    	if(orientaiton > 0 && originBitmap != null){
			    		Log.d(TAG,"getConfig："+originBitmap.getConfig().name() + " width = " + width + " height = " + height + " bmpWidth = " + originBitmap.getWidth() + " bmpHeight = " + originBitmap.getHeight());
			    		Bitmap originBitmap2 = rotate(originBitmap, orientaiton, true);
			    		if(originBitmap2 != null){
			    			originBitmap.recycle();
			    			originBitmap = originBitmap2;
			    		}
			    	}
			    	
			    	if (originBitmap != null) {
			    		Log.d(TAG,"originwidth = " + originBitmap.getWidth() + " originheight = " + originBitmap.getHeight()+"getConfig："+originBitmap.getConfig().name());
			    		previewImage.setImageBitmap(originBitmap);
						thumbImg.setImageBitmap(originBitmap);
					}
				} 
			}   
		}); 
		
		
		
		/*isMainAttachChanged = getIntent().getBooleanExtra(EditDiaryActivity.INTENT_ACTION_DIARY_IS_EDIT, false);
		if (isMainAttachChanged) {
			editAttachPath = getIntent().getStringExtra(EditDiaryActivity.INTENT_ACTION_DIARY_DEIT_PATH);
		}*/
		
		
		llXiaLa = (LinearLayout) findViewById(R.id.ll_edit_diary_style);
		llXiaLa.setOnClickListener(this);
		diaryEditStyle = (TextView) findViewById(R.id.tv_edit_name);
		inflater = LayoutInflater.from(this);
		rlTitleBar = findViewById(R.id.rl_title);
		
		goToPage(FRAGMENT_DETAIL_MAIN_VIEW, false);
		loadPhotoEffectLib();
		
		handler = new Handler(this);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);
	}
	  
	public static Bitmap rotate(Bitmap bitmap, float rotate, boolean filter){
		Bitmap resizedBitmap = null;
		if(bitmap != null){
			Matrix matrix = new Matrix();
			matrix.setRotate(rotate);
//			resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, filter);
			resizedBitmap = BitmapUtils.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, filter);
		}
		return resizedBitmap;
	}
	
	private void loadPhotoEffectLib() {
		if(effectUtils == null){
			effectUtils = new EffectUtils(this);
			effectUtils.parseXml("effectcfg/effectlist.xml");
			effectBeans = effectUtils.getEffects(EffectBean.TYPE_EFFECTS_IMAGE);
			if (PluginUtils.isPluginMounted()) {
				mEffects = new XEffects();
			}
			
//			effectBeans.remove(6); // 6 is mosaic effect;
			if (effectBeans != null) {
				effectUtils.removeEffectBean(effectBeans,EffectType.KEF_TYPE_MOSAIC);
			}
		}
//		effectBeans.remove(com.cmmobi.looklook.Config.MOSAIC_INDEX); // 6 is mosaic effect;
	}
	
	@Override
	public void onResume() {
		mMapView.onResume();
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
	}
	
	public Bitmap getPreViewBitmap() {
		if (previewImage != null) {
			BitmapDrawable mDrawable =  (BitmapDrawable) previewImage.getDrawable();
			Bitmap bitmap = mDrawable.getBitmap();
			return bitmap;
		}
		return null;
	}
	
	public Bitmap getOriginBmp() {
		return originBitmap;
	}
	
	public ImageView getPreView() {
		return previewImage;
	}
	
	public ImageView getThumbView() {
		return thumbImg;
	}
	
	public boolean isMainAttachChanged() {
		return (isPhotoCroped || isAddEffect);
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()) {
		/*case R.id.ll_edit_media_sound_distinguish:
			goToPage(FRAGMENT_TEXT_INPUT, false);
			edit.setHorizontallyScrolling(false);
			break;*/
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_edit_done:
			if (isEditButtonPressed) {
				break;
			}
			isEditButtonPressed = true;
			Dialog dialog = ZDialog.show(R.layout.progressdialog, false, true, this,true);
			shortRecMediaPlayer.stop();
			dialogTitle = (TextView) dialog.findViewById(R.id.progress_dialog_tv);
			isMainAttachChanged = isMainAttachChanged();
			if (getString(R.string.edit_media_cover).equals(getEditDiaryStyle())) {
				if (isDiaryCreated()) {
					modTagsOrPosition();
					addAttach();
				} else {// 若日记未创建成功
					removeDiaryTask();
					createOfflineDiary();
				}
				saveLocalDiaryAndFinish();
			} else {
				saveAsNewDiary();
			}
			
			break;
		case R.id.ll_edit_diary_style:
			Log.d(TAG,"ll_edit_diary_style ");
			showDuplicateDiaryName();
			break;
		case R.id.ll_biezhen:
			if (shortRecMediaPlayer.isPlaying()) {
				tackView.stopAudio(shortRecMediaPlayer);
			} else {
				tackView.playAudio(audioPath,shortRecMediaPlayer);
			}
			break;
		case R.id.iv_edit_photo_thumbnails:
			if (fragmentType == FRAGMENT_DETAIL_MAIN_VIEW 
				|| fragmentType == FRAGMENT_TAG_VIEW 
				|| fragmentType == FRAGMENT_TEXT_INPUT) {
				mainLayout.setVisibility(View.VISIBLE);
				detailMainLayout.setVisibility(View.GONE);
			}
			break;
		case R.id.ll_activity_edit_photo_main:
			if (fragmentType == FRAGMENT_DETAIL_MAIN_VIEW 
				|| fragmentType == FRAGMENT_TAG_VIEW 
				|| fragmentType == FRAGMENT_TEXT_INPUT) {
				mainLayout.setVisibility(View.GONE);
				detailMainLayout.setVisibility(View.VISIBLE);
			}
			break;
		}
	}
	
	@Override
	public List<Integer> getCheckedList() {
		return checkedList;
	}
	
	@Override
	public void goToPage(int type, boolean record) {
		Fragment dst;
		String mViewName = null;
		dst = null;
		if (type == FRAGMENT_DETAIL_MAIN_VIEW) {
			dst = detailMainFragment;
		} else if (type == FRAGMENT_TAG_VIEW) {
			dst = tagFragment;
		} else if (type == FRAGMENT_POS_VIEW) {
			dst = posFragment;
		} else if (type == FRAGMENT_SOUND_VIEW) {
			dst = soundFragment;
		} else if (type == FRAGMENT_MAIN_VIEW) {
			dst = mainFragment;
		} else if (type == FRAGMENT_CROP_VIEW) {
			if (thumbImg.isShown()) {
				onClick(thumbImg);
			}
			dst = cropFragment;
		} else if (type == FRAGMENT_EFFECT_VIEW) {
			if (thumbImg.isShown()) {
				onClick(thumbImg);
			}
			dst = effectFragment;
		} else if (type == FRAGMENT_TEXT_INPUT) {
			dst = textFragment;
		} else {
			Log.e(TAG, "unknow fragment - type:" + type);
			return;
		}
		
		fragmentType = type;

		FragmentTransaction ft = fm.beginTransaction();

		if (fm.findFragmentById(R.id.fl_edit_phone_detail_fragment) != null) {
			ft.replace(R.id.fl_edit_phone_detail_fragment, dst, mViewName);
		} else {
			ft.add(R.id.fl_edit_phone_detail_fragment, dst, mViewName);
		}
		if (record) {
			ft.addToBackStack(null);
		}
		ft.commit();
		if (type == FRAGMENT_DETAIL_MAIN_VIEW || type == FRAGMENT_MAIN_VIEW) {
			ivDone.setVisibility(View.VISIBLE);
			ivBack.setVisibility(View.VISIBLE);
		} else {
			ivDone.setVisibility(View.GONE);
			ivBack.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void setMapViewVisibility(int visible) {
		mMapView.setVisibility(visible);
	}
	
	public MyMapView getMapView() {
		return mMapView;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		try {
			if (shortRecMediaPlayer != null) {
				shortRecMediaPlayer.release();
				shortRecMediaPlayer = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (PluginUtils.isPluginMounted() && mEffects != null) {
			mEffects.release();
		}
		mMapView.destroy();
		if(mEffects != null)mEffects.release();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mMapView.onPause();
		super.onPause();
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	public void onStop(){
		if (shortRecMediaPlayer.isPlaying()) {
			tackView.stopAudio(shortRecMediaPlayer);
		}
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}
	
	/**
	 * 切换特效;
	 * 
	 * @param effect: 目标特效;
	 */
	/*public void switchEffect(int effectId) {
//		loadPhotoEffectLib();
		
		if (PluginUtils.isPluginMounted() && effectId < effectBeans.size()) {
			EffectBean bean = effectBeans.get(effectId);
			
			effectUtils.changeEffectdWithEffectBean(
					mEffects, 
					bean, 
					originBitmap.getWidth(), 
					originBitmap.getHeight(), 
					0, 
					null);
			Log.d(TAG,"width = " + originBitmap.getWidth() + " height = " + originBitmap.getHeight());
			mEffects.processBitmap(originBitmap, effectBitmap);
			
			if (effectId == 0) {
				previewImage.setImageBitmap(originBitmap);
				thumbImg.setImageBitmap(originBitmap);
			} else {
				previewImage.setImageBitmap(effectBitmap);
				thumbImg.setImageBitmap(effectBitmap);
			}
			
		} else {
//			showPluginDownloadDialog();
		}
	}*/
	
	public Bitmap switchEffect(int effectId,Bitmap srcBmp) {
		Bitmap dstBmp = null;
		if (PluginUtils.isPluginMounted() && effectBeans != null && effectBeans != null && effectId < effectBeans.size()) {
			
			Log.d(TAG,"[switchEffect] srcBmp:"+srcBmp.getConfig().name());
			
			dstBmp = Bitmap.createBitmap(srcBmp.getWidth(), srcBmp.getHeight(), Config.ARGB_8888);
			EffectBean bean = effectBeans.get(effectId);
			
			effectUtils.changeEffectdWithEffectBean(
					mEffects, 
					bean, 
					srcBmp.getWidth(), 
					srcBmp.getHeight(), 
					0, 
					null);
			Log.d(TAG,"width = " + srcBmp.getWidth() + " height = " + srcBmp.getHeight() + " name = " + bean.getZHName());
			mEffects.processBitmap(srcBmp, dstBmp);
			
			if (effectId == 0) {
				dstBmp = null;
			}
			int id = effectId;
			if (id >= com.cmmobi.looklook.Config.MOSAIC_INDEX) {
				id += 1;
			}
			CmmobiClickAgentWrapper.onEvent(this, "my_effect", String.valueOf(id));
		} else {
			dstBmp = null;//srcBmp;
			
		}
		return dstBmp;
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester2.RESPONSE_TYPE_MODIFY_TAG:
			Log.d(TAG,"handleMessage RESPONSE_TYPE_MODIFY_TAG");
			GsonResponse2.modTagsOrPositionResponse tagandpositionResponse = (modTagsOrPositionResponse) msg.obj;
			if (tagandpositionResponse !=null && "0".equals(tagandpositionResponse.status)) {
				Log.d(TAG,"handleMessage RESPONSE_TYPE_MODIFY_TAG successed");
				DiaryManager diarymanager = DiaryManager.getInstance();
				MyDiary myLocalDiary = diarymanager.findLocalDiaryByUuid(diaryUUID);
				if (myLocalDiary !=  null) {
					Log.d(TAG,"save myLocalDiary not null");
					myLocalDiary.position = myDiary.position;
					myLocalDiary.tags = getMyDiary().tags;
					DiaryManager.getInstance().diaryDataChanged(myDiary.diaryuuid);
				}
			}
			isTagPositionChangeDone = true;
			saveLocalDiaryAndFinish();
			break;
		case Requester2.RESPONSE_TYPE_CREATE_STRUCTURE:
			Log.d(TAG,"handleMessage RESPONSE_TYPE_CREATE_STRUCTURE");
			structureResponse = (createStructureResponse) msg.obj;
			
			if (getString(R.string.edit_media_cover).equals(getEditDiaryStyle())) {// 覆盖模式
				if (isDiaryCreated()) {
					if (structureResponse != null && "0".equals(structureResponse.status)) {// 创建日记结构成功
						new CreateDiarySucessedThread().start();
					} else { // 创建日记结构失败
						new CreateDiaryFailedThread().start();
					}
				} else {
					new CreateOfflineDiaryThread().start();
				}
			} else {// 另存为模式
				if (structureResponse != null) {// 创建另存为日记结构成功
					new CreateNewDiaryThread().start();
				}
			}
			break;
		case EditMediaDetailActivity.HANDLER_DISMISS_PROCESS_DIALOG:
			saveLocalDiaryAndFinish();
			break;
		case EditMediaDetailActivity.HANDLER_SOUND_DELETE:
			isDeleteSound = true;
			hasShortSound = false;
			isAddSoundAttach = false;
			checkAuxiliaryAttachEmpty();
			detailMainFragment.processDelShortSoundMsg();
			break;
		case EffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_FINISH:
			Log.d(TAG,"VideoEffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_FINISH");
			if (msg.arg1 == EffectTransCodeUtil.MAIN_IMAGE) {
				
			} else if (msg.arg1 == EffectTransCodeUtil.SHORT_AUDIO) {
				if (msg.obj != null) {
					audioPath = (String) msg.obj;
				}
				shortAudioSignal.countDown();
			}
			break;
		case EffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_SCHEDULE:
			int percent = msg.arg2;
			Log.d(TAG,"VideoEffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_SCHEDULE percent = " + percent);
			if (dialogTitle != null) {
				String titel = "";
				if (msg.arg1 == EffectTransCodeUtil.MAIN_IMAGE) {
//					titel = getString(R.string.process_video_effect) + " " + percent + "%";
				} else if (msg.arg1 == EffectTransCodeUtil.SHORT_AUDIO) {
					titel = getString(R.string.process_short_record_effect) + " " + percent + "%";
				}
				dialogTitle.setText(titel);
			}
			break;
		}
		return false;
	}

	@Override
	protected void modifyMainAttach() {
		// TODO Auto-generated method stub
		if (isMainAttachChanged) {
			Bitmap bmp = getPreViewBitmap();
			long currentTime = TimeHelper.getInstance().now();
			String imageID = String.valueOf(currentTime);
			String RECORD_FILE_DIR =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(this).getLookLookID() + "/pic";
			String outputFile = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + imageID +  "/" + imageID + ".jpg" ;
			Log.d(TAG,"outputFile = " + outputFile);
			if(BitmapUtils.saveBitmap2file(bmp, outputFile)) {
				Log.d(TAG,"saveBitmap2file sucessed");
				if (editAttachPath != null) {
					EditMediaDetailActivity.delFile(editAttachPath);
				}
				editAttachPath = outputFile;
			}
		}
	}

	@Override
	protected String getMontageDuration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void copyMainAttach() {
		// TODO Auto-generated method stub
		String imageID = String.valueOf(TimeHelper.getInstance().now());
		String dstFilePath = Environment.getExternalStorageDirectory().getPath() + Constant.SD_STORAGE_ROOT + "/" 
				+ ActiveAccount.getInstance(this).getLookLookID() + "/pic/" + imageID + "/" + imageID + ".jpg";
		
		long fileLength = copyFile2(pictureAttachPath, dstFilePath);
		if (fileLength > 0) {
			editAttachPath = dstFilePath;
		}
	}

	@Override
	public void setSoundTrackText(String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBeautifulSoundText(String text) {
		// TODO Auto-generated method stub
		detailMainFragment.setBeautifulSoundText(text);
	}
	
	public void setEffectText(String text) {
		mainFragment.setEffectText(text);
	}
}
