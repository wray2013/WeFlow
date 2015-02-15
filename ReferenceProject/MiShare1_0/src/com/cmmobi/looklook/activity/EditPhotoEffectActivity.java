package com.cmmobi.looklook.activity;

import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLayoutInflater;
import cn.zipper.framwork.utils.ZGraphics;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.GsonProtocol;
import com.cmmobi.looklook.common.gson.DiaryController.DiaryWrapper;
import com.cmmobi.looklook.common.gson.GsonRequest3.Attachs;
import com.cmmobi.looklook.common.gson.GsonResponse3.MainAttach;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.utils.BitmapUtils;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DiaryEditNote;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.statistics.CmmobiClickAgent;
import com.cmmobivideo.utils.EffectBean;
import com.cmmobivideo.utils.EffectUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.utils.XUtils;
import com.google.gson.Gson;

import effect.EffectType;
import effect.XEffects;

public class EditPhotoEffectActivity extends ZActivity {

	private final String TAG = "EditPhotoEffectActivity";
	private DisplayMetrics dm = new DisplayMetrics();
	
	private String picPath = "";
//	private String diaryUUID = null;
	private MyDiary myDiary = null;
//	private String userID = null;
	private ImageView ivImage = null;
	private Bitmap originBitmap = null;
	private LinearLayout effectGroup = null;
	
	public XEffects mEffects;
	public EffectUtils effectUtils;
	public List<EffectBean> effectBeans;
	private ImageView ivBack = null;
	private ImageView ivDone = null;
	private ImageView ivSelectedView = null;
	private Bitmap savedBmp = null;
//	private Dialog finishDialog;
	
	public DiaryEditNote diaryEditNote = null;
	public int curEffectId = 0;
	private HorizontalScrollView horiScrView = null;
	
	private final float VIEW_SPACING = 7.5f;
	
	private Object lock = new Object();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_photo_effect);
		
		ivImage = (ImageView) findViewById(R.id.iv_photo);
		
		ivBack = (ImageView) findViewById(R.id.iv_edit_diary_back);
		ivDone = (ImageView) findViewById(R.id.iv_edit_diary_save);
		horiScrView = (HorizontalScrollView) findViewById(R.id.hsv_photo_effects);
		
		ivBack.setOnClickListener(this);
		ivDone.setOnClickListener(this);
		ivDone.setEnabled(false);
		dm = getResources().getDisplayMetrics();
		Log.d(TAG,"width = " + dm.widthPixels + " height = " + dm.heightPixels);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dm.widthPixels,dm.widthPixels);
		params.addRule(RelativeLayout.BELOW, R.id.rl_edit_photo_effect_top);
		ivImage.setLayoutParams(params);
		loadPicture();
		effectGroup = (LinearLayout) findViewById(R.id.effect_group);
		
		ViewTreeObserver vto1 = ivImage.getViewTreeObserver();
		vto1.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
			@Override   
			public void onGlobalLayout() {
				int height = ivImage.getMeasuredHeight(); 
		    	int width  = ivImage.getMeasuredWidth(); 
				originBitmap = BitmapUtils.readBitmapAutoSize(picPath, width, height);
				int orientation = XUtils.getExifOrientation(picPath);
				if (orientation != 0) {
					originBitmap = ZGraphics.rotate(originBitmap, XUtils.getExifOrientation(picPath), true);
				}
				Log.d(TAG,"XUtils.getExifOrientation(picPath) = " + orientation);
				savedBmp = originBitmap;
				if (originBitmap == null) {
					Prompt.Alert(EditPhotoEffectActivity.this, "图片不存在");
					EditPhotoEffectActivity.this.finish();
					return;
				}
				Bitmap bmp = originBitmap;
				if (diaryEditNote.isEffect) {
					bmp = switchEffect(diaryEditNote.effectIndex, originBitmap);
				}
				ivImage.setImageBitmap(bmp);
				ivImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//				ZDialog.show(R.layout.progressdialog, false, true, EditPhotoEffectActivity.this,false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						initEffect();
						
						/*runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								ZDialog.dismiss();
							}
						});*/
					}
				}).start();
			}   
		});
//		initDialog();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		CmmobiClickAgentWrapper.onPause(this);
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		CmmobiClickAgentWrapper.onResume(this);
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		CmmobiClickAgentWrapper.onStop(this);
		super.onStop();
	}
	
	/*private void initDialog() {
		finishDialog = new Dialog(this, R.style.networktask_dialog_style_none_background);
		Window w = finishDialog.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();

		lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		View view = ZLayoutInflater.inflate(R.layout.dialog_edit_diary_finish_menu,
				null);
		
		view.findViewById(R.id.replace).setOnClickListener(this);
		view.findViewById(R.id.save_as).setOnClickListener(this);
		view.findViewById(R.id.cancel).setOnClickListener(this);
		
		finishDialog.setContentView(view);
		
		finishDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(
				R.drawable.del_dot_big));
		finishDialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
	}*/
	
	private void initEffect() {
		int index = 0;
		int leftMargin = (int)(VIEW_SPACING * dm.density);
		int width = (dm.widthPixels - 5 * leftMargin) / 4; 
		Bitmap thumbBmp = originBitmap.createScaledBitmap(originBitmap, width,width, true);
		for (final EffectBean effect:effectBeans) {
			
			final RelativeLayout layout = (RelativeLayout) ZLayoutInflater.inflate(R.layout.include_effect_buttons_stub);
			final ImageView effectImage= (ImageView) layout.findViewById(R.id.effect_image);
			final TextView effectTv = (TextView) layout.findViewById(R.id.effect_name);
			final ImageView effectSelectedImage = (ImageView) layout.findViewById(R.id.effect_selected_image);
			LinearLayout.LayoutParams params = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(leftMargin, 0, 0, 0);
			layout.setLayoutParams(params);
			layout.setId(index);
			final Bitmap effectBmp = switchEffect(index, thumbBmp);
			EffectWrapper effectWrapper = new EffectWrapper();
			effectWrapper.effectId = index;
			effectWrapper.effectBmp = effectBmp;
			effectWrapper.effectSelectedView = effectSelectedImage;
			if (index == diaryEditNote.effectIndex) {
				ivSelectedView = effectSelectedImage;
				effectSelectedImage.setVisibility(View.VISIBLE);
			}
			
			layout.setTag(effectWrapper);
			index++;
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					effectImage.setImageBitmap(effectBmp);
					effectTv.setText(effect.getZHName());
					layout.setOnClickListener(EditPhotoEffectActivity.this);
					effectGroup.addView(layout);
				}
			});
		}
		
		/*runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ivImage.setImageBitmap(originBitmap);
			}
		});*/
	}
	
	public Bitmap switchEffect(int effectId,Bitmap srcBmp) {
		if (srcBmp == null) {
			return null;
		}
		Bitmap dstBmp = null;
		if (PluginUtils.isPluginMounted() && effectBeans != null && effectBeans != null && effectId < effectBeans.size()) {
			Log.d(TAG,"[switchEffect] srcBmp:"+srcBmp.getConfig().name());
			Log.d(TAG,"[switchEffect] srcBmp:"+srcBmp);
			
			dstBmp = Bitmap.createBitmap(srcBmp.getWidth(), srcBmp.getHeight(), Config.ARGB_8888);
			EffectBean bean = effectBeans.get(effectId);
			
			synchronized (lock) {
				if (mEffects != null && dstBmp != null) {
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
						dstBmp = srcBmp;
					}
					int id = effectId;
					if (id >= com.cmmobi.looklook.Config.MOSAIC_INDEX) {
						id += 1;
					}
					
				} else {
					dstBmp = srcBmp;
				}
			}
			
		} else {
			dstBmp = null;//srcBmp;
			
		}
		
		Log.d(TAG,"[switchEffect] dstBmp:"+dstBmp);
		return dstBmp;
	}
	
	private void loadPicture() {
//		userID =  ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
//		diaryUUID = getIntent().getStringExtra("diaryuuid");
//		myDiary = DiaryManager.getInstance().findMyDiaryByUUID(diaryUUID);
		
		String diaryString = getIntent().getStringExtra("diarystring");
		myDiary = new Gson().fromJson(diaryString,MyDiary.class);
		
		String diaryEditString = getIntent().getStringExtra(DiaryEditPreviewActivity.INTENT_ACTION_EDIT_NOTELIST);
		diaryEditNote = new Gson().fromJson(diaryEditString, DiaryEditNote.class);
		
//		picPath = myDiary.getMainPath();
		picPath = diaryEditNote.mediaPath;
		Log.d(TAG,"picPath = " + picPath);
		
		effectUtils = new EffectUtils(this);
		effectUtils.parseXml("effectcfg/effectlist.xml");
		effectBeans = effectUtils.getEffects(EffectBean.TYPE_EFFECTS_IMAGE);
		if (PluginUtils.isPluginMounted()) {
			mEffects = new XEffects();
		}
		
		if (effectBeans != null) {
			effectUtils.removeEffectBean(effectBeans,EffectType.KEF_TYPE_MOSAIC);
		}
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		/*case DiaryController.DIARY_REQUEST_DONE:
			Log.d(TAG,"DiaryController.DIARY_REQUEST_DONE in");
			MyDiary diary = ((DiaryWrapper) msg.obj).diary;
			MyDiary localDiary = DiaryManager.getInstance().findMyDiaryByUUID(diary.diaryuuid);
			ZDialog.dismiss();
			Intent intent = new Intent(DiaryPreviewActivity.DIARY_EDIT_DONE);
			intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, diaryUUID);
			LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);
			finish();
			break;*/
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		try {
			synchronized (lock) {
				if(mEffects!=null){
					mEffects.release();
					mEffects = null;
				}
			}
			
			for (int i = 0;i < effectBeans.size();i++) {
				RelativeLayout layout = (RelativeLayout)findViewById(i);
				if (layout != null && layout.getTag() != null) {
					EffectWrapper wrapper = (EffectWrapper) layout.getTag();
					if (wrapper != null && wrapper.effectBmp != null && !wrapper.effectBmp.isRecycled()) {
						wrapper.effectBmp.recycle();
						wrapper.effectBmp = null;
					}
				}
			}
			BitmapUtils.releaseBmp(savedBmp);
			BitmapUtils.releaseBmp(originBitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()) {
		case R.id.iv_edit_diary_back:
			finish();
			break;
		case R.id.iv_edit_diary_save:
//			finishDialog.show();
			modifyMainAttach();
			break;
		/*case R.id.replace:
			finishDialog.dismiss();
			modifyMainAttach();
			break;
		case R.id.save_as:
			finishDialog.dismiss();
			break;
		case R.id.cancel:
			finishDialog.dismiss();
			break;*/
		default:
			int viewId = view.getId();
			if (viewId >= 0 && viewId < effectBeans.size()) {
				EffectWrapper wrapper = (EffectWrapper) view.getTag();
				savedBmp = switchEffect(wrapper.effectId, originBitmap);
				ivImage.setImageBitmap(savedBmp);
				if (ivSelectedView != null) {
					ivSelectedView.setVisibility(View.INVISIBLE);
				}
				ivSelectedView = wrapper.effectSelectedView;
				ivSelectedView.setVisibility(View.VISIBLE);
				curEffectId = viewId;
//				savedBmp = wrapper.effectBmp;
				if (viewId == diaryEditNote.effectIndex) {
					ivDone.setEnabled(false);
				} else {
					ivDone.setEnabled(true);
				}
				
				smoothScroll(view);
			    
				HashMap<String,String> map = new HashMap<String, String>();
				map.put("label1", "2");
				map.put("label2", String.valueOf(curEffectId));
				CmmobiClickAgent.onEvent(this, "edit_effect", map);
			}
			break;
		}
	}
	
	private void smoothScroll(View view) {
		int[] location = new int[2];
	    view.getLocationOnScreen(location);
	    int x = location[0];
	    
	    if (x - 2 * VIEW_SPACING * dm.density <= 0) {
	    	horiScrView.smoothScrollBy((int)(-1 * VIEW_SPACING * dm.density) - view.getWidth(),0);
	    } else if (x + view.getWidth() + 2 * VIEW_SPACING * dm.density >= dm.widthPixels) {
	    	horiScrView.smoothScrollBy((int)(VIEW_SPACING * dm.density) + view.getWidth(), 0);
	    }
	}
	
	private void modifyMainAttach() {
		ZDialog.show(R.layout.progressdialog, false, true, this,false);
		String imageID = DiaryController.getNextUUID();
		String outPhotoPath = DiaryController.getAbsolutePathUnderUserDir()  +  "/pic/" + imageID + ".jpg" ;
		if(BitmapUtils.saveBitmap2file(savedBmp, outPhotoPath)) {
			Log.d(TAG,"saveBitmap2file sucessed");
		}
		
		myDiary.modifyMainAttach(outPhotoPath, "");
		String diaryString = new Gson().toJson(myDiary);
		Intent intent = new Intent();
		intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_DIARY_STRING, diaryString);
		intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_MEDIA_EFFECT, curEffectId);
		setResult(RESULT_OK, intent);
		finish();
		
		/*MainAttach mainAttach = myDiary.attachs.levelattach;
		String attachID = "";
		String attachUUID = "";
		if (mainAttach != null) {
			attachID = mainAttach.attachid;
			attachUUID = mainAttach.attachuuid;
		}
		Attachs attach = DiaryController.getInstanse().createNewAttach(attachID, attachUUID, GsonProtocol.ATTACH_TYPE_PICTURE, GsonProtocol.SUFFIX_JPG, GsonProtocol.ATTACH_LEVEL_MAIN, GsonProtocol.ATTACH_OPERATE_TYPE_UPDATE, "",outPhotoPath);
		Attachs[] attachs = new Attachs[1];
		attachs[0] = attach;
		DiaryController.getInstanse().updateDiary(handler, myDiary, attachs);
		DiaryController.getInstanse().diaryContentIsReady(diaryUUID);*/
	}
	
	private class EffectWrapper {
		int effectId;
		Bitmap effectBmp;
		ImageView effectSelectedView;
	}

}
