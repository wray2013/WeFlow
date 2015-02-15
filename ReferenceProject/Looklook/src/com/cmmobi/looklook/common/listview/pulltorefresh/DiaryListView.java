package com.cmmobi.looklook.common.listview.pulltorefresh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.zipper.framwork.utils.ZStringUtils;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryDetailActivity;
import com.cmmobi.looklook.activity.HomepageMyselfDiaryActivity;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachAudio;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachImage;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachVideo;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.DiaryManager.FilterType;

public class DiaryListView extends AbsRefreshView<ArrayList<MyDiary>> implements OnClickListener,OnLongClickListener {

	private static final String TAG="DiaryListView";
	private DiaryManager diaryManager;
	public DiaryListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DiaryListView(Context context) {
		super(context);
		init();
	}
	
	public int getBackgroundWidth(){
		return getScreenWidth();
	}
	
	public int getBackgroundHeight(){
		return (int)((float)getScreenWidth()/1.5);
	}
	
	public int getDiaryWidth(){
		return getScreenWidth()/2-20;
	}
	
	public int getDiaryHeight(){
		return (int)((float)getDiaryWidth()/((float)getScreenWidth()/getScreenHight()));
	}
	
	public ArrayList<MyDiary> getDiaryList(){
		return diaryManager.getDiaryListByUUID(itemsList);
	}
	
	public int getDiaryNum(){
		if(itemsList!=null)
			return itemsList.size();
		return 0;
	}
	
	private boolean deleteMode=false;
	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.rl_media_content:
			View rlMediaContentForceGround=(View) v.getTag(R.id.rl_media_content_forceground);
			rlMediaContentForceGround.setVisibility(View.VISIBLE);
			cancelRemoveList.add(rlMediaContentForceGround);
			String item=v.getTag(R.id.rl_media_content).toString();
			removeItemList.add(item);
			deleteMode=true;
			if(statusBarListener!=null)statusBarListener.checkedNum(removeItemList.size());
			if(removeItemList.size()>0){//显示删除状态栏
				if(statusBarListener!=null)statusBarListener.showStatusBar();
			}
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_biezhen:{//主体为图片或视频
			String diaryUUID=(String) v.getTag();
			MyDiary myDiary=diaryManager.findLocalDiaryByUuid(diaryUUID);
			if(myDiary!=null){
				((TackView)v).setAudio(myDiary.getShortRecUrl(),0);
			}else{
				Log.e(TAG, "ll_biezhen url is null");
			}
			break;}
		case R.id.rl_media_content_forceground:{
			v.setVisibility(View.GONE);
			cancelRemoveList.remove(v);
			String diaryuuid=v.getTag().toString();
			removeItemList.remove(diaryuuid);
			if(statusBarListener!=null)statusBarListener.checkedNum(removeItemList.size());
			if(0==removeItemList.size()){//隐藏删除状态栏
				deleteMode=false;
				removeItemList.clear();
				if(statusBarListener!=null)statusBarListener.dimissStatusBar();
			}
			break;}
		case R.id.rl_media_content:
			if(deleteMode){
				View rlMediaContentForceGround=(View) v.getTag(R.id.rl_media_content_forceground);
				rlMediaContentForceGround.setVisibility(View.VISIBLE);
				cancelRemoveList.add(rlMediaContentForceGround);
				String item=v.getTag(R.id.rl_media_content).toString();
				if(!removeItemList.contains(item)){
					removeItemList.add(item);
				}
				if(statusBarListener!=null)statusBarListener.checkedNum(removeItemList.size());
			}else{
				FilterType type = ((HomepageMyselfDiaryActivity)context).diaryFilterType;
				
				ArrayList<MyDiary> diarylistList=diaryManager.getDiaryListByUUID(itemsList);
				DiaryManager.getInstance().setDetailDiaryList(diarylistList);
				
				String diaryuuid=v.getTag(R.id.rl_media_content).toString();
				Bundle bundle = new Bundle();
				bundle.putString(DiaryDetailActivity.INTENT_ACTION_DIARY_UUID, diaryuuid);
				bundle.putSerializable(DiaryDetailActivity.INTENT_EXTRA_DIARY_FILTER, type);
				Intent intent = new Intent(context,DiaryDetailActivity.class);
				intent.putExtras(bundle);
				context.startActivity(intent);
				
//				context.startActivity(new Intent(context,DiaryDetailActivity.class)
//				.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_ID, diaryid)
//				.putExtra(DiaryDetailActivity.INTENT_EXTRA_DIARY_FILTER, type));
			}
			break;
		default:
			break;
		}
	}
	
	//保存选中的view
	ArrayList<View> cancelRemoveList=new ArrayList<View>();
	public void cancelRemove(){
		int len=cancelRemoveList.size();
		for(int i=0;i<len;i++){
			cancelRemoveList.get(i).setVisibility(View.GONE);
		}
		cancelRemoveList.clear();
		deleteMode=false;
		removeItemList.clear();
		if(statusBarListener!=null)statusBarListener.dimissStatusBar();
	}
	
	//保存选中的数据信息
	ArrayList<String> removeItemList=new ArrayList<String>();
//	public ArrayList<String> getRemoveItem(){
//		return removeItemList;
//	}
	
	public ArrayList<String> getLocalRemoveItem(){
		ArrayList<String> localDiaryIDs=new ArrayList<String>();
		for(int i=0;i<removeItemList.size();i++){
			String diaryuuid=removeItemList.get(i);
			MyDiary myDiary=diaryManager.findLocalDiaryByUuid(diaryuuid);
			if(myDiary!=null&&null==ZStringUtils.emptyToNull(myDiary.diaryid)){
				localDiaryIDs.add(diaryuuid);
			}
		}
		return localDiaryIDs;
	}
	
	public ArrayList<String> getServerRemoveItem(){
		ArrayList<String> serverDiaryIDs=new ArrayList<String>();
		for(int i=0;i<removeItemList.size();i++){
			String diaryuuid=removeItemList.get(i);
			MyDiary myDiary=diaryManager.findLocalDiaryByUuid(diaryuuid);
			if(myDiary!=null&&null!=ZStringUtils.emptyToNull(myDiary.diaryid)){
				serverDiaryIDs.add(myDiary.diaryid);
			}
		}
		return serverDiaryIDs;
	}
	
	private StatusBarListener statusBarListener;
	public void setStatusBarListener(StatusBarListener statusBarListener){
		this.statusBarListener=statusBarListener;
	}
	
	public interface StatusBarListener{
		void showStatusBar();
		void dimissStatusBar();
		void checkedNum(int count);
	}

	private View rootView;
	private RelativeLayout rlListView;
	private int marginTop;
	private void init(){
		diaryManager=DiaryManager.getInstance();
		marginTop=getResources().getDimensionPixelSize(R.dimen.size_20dip);
		rootView = inflater.inflate(
				R.layout.activity_homepage_myself_diary_content, null);
		rlListView=(RelativeLayout) rootView.findViewById(R.id.rl_diarylist);
		rlListView.setTag(new params());
		addChild(rootView, 1);
	}
	
	/**
	 * 获取日记包裹区域layout
	 */
	public RelativeLayout getContentLayout(){
		return rlListView;
	}
	
	/*public void clearList(){
		reset();
		((params)rlListView.getTag()).reset();
	}*/

	@Override
	protected void initContent(ArrayList<MyDiary> items) {
		Log.d(TAG, "initContent");
		rlListView.removeAllViews();
		((params)rlListView.getTag()).reset();
		itemsList.clear();
		addView(items);
	}

	static int[] types={
			0x10000000,//主体 视频
			0x1000000,//主体 音频
			0x100000,//主体 图片
			0x10000,//主体 文字
			0x10000100,//主体 视频+辅 音频
			0x10000101,//主体 视频+辅 音频+文字
			0x1000100,//主体 音频+辅 音频
			0x1000101,//主体 音频+辅 音频+文字
			0x100100,//主体 图片+辅 音频
			0x100101,//主体 图片+辅 音频+文字
			0x101,//辅 音频+文字
			0x1,//辅 文字
	};
	
	private ArrayList<String> itemsList=new ArrayList<String>();
	public ArrayList<String> getItemsList(){
		return itemsList;
	}
	
	@Override
	protected void addView(ArrayList<MyDiary> items) {
		Log.d(TAG, "addView");
		if(null==items){
			Log.e(TAG, "items is null");
			return;
		}
//		itemsList.addAll(items);
		params p=(params) rlListView.getTag();
		for (int i = 0; i < items.size(); i++) {
			
			int type=getDiaryType(items.get(i).attachs);//获取日记类型
			itemsList.add(items.get(i).diaryuuid);
			View v = inflater.inflate(
					R.layout.activity_homepage_main_list_item, null);
			v.setId(++p.viewId);
			v.setTag(items.get(i));
			View rlMediaContentForceGround=v.findViewById(R.id.rl_media_content_forceground);
			View rlMediaContent=v.findViewById(R.id.rl_media_content);//日记layout
			rlMediaContent.setTag(R.id.rl_media_content_forceground,rlMediaContentForceGround);
			rlMediaContent.setTag(R.id.rl_media_content,items.get(i).diaryuuid);
			rlMediaContent.setTag(R.id.ll_content,type);
			rlMediaContent.setOnLongClickListener(this);
			rlMediaContent.setOnClickListener(this);
			rlMediaContentForceGround.setOnClickListener(this);
			rlMediaContentForceGround.setTag(items.get(i).diaryuuid);
			View llDescription=v.findViewById(R.id.ll_pic_description);//文字描述layout
			TextView tvDescription=(TextView) v.findViewById(R.id.tv_description);//文字描述
			WebImageView ivPic=(WebImageView) v.findViewById(R.id.iv_pic);//图片类型
			ViewGroup.LayoutParams picParams =ivPic.getLayoutParams();
			picParams.height=getDiaryHeight();
			ivPic.setLayoutParams(picParams);
			ImageView ivVideoButton=(ImageView) v.findViewById(R.id.iv_video_play_button);//视频播放按钮
			View llContent=v.findViewById(R.id.ll_content);//主体layout
			View rlMainTextContent=v.findViewById(R.id.rl_main_text_content);//主体layout(文字)
			TextView tvMainText=(TextView) v.findViewById(R.id.tv_main_text_content);//主体中的文字
			ImageView ivCidai=(ImageView) v.findViewById(R.id.iv_cidai);//主体中的磁带图片
			
			TackView llBiezhen=(TackView) v.findViewById(R.id.ll_biezhen);//别针layout
			llBiezhen.setOnClickListener(this);
			
			WebImageView ivWeather=(WebImageView) v.findViewById(R.id.iv_tianqi);//天气图片
			ivWeather.setImageUrl(0, 1, items.get(i).weather, false);
			ImageView ivStick=(ImageView) v.findViewById(R.id.iv_stick);//天气图片下面的小棒子
			
			tvMainText.setText(null);
			tvDescription.setText(null);
			Log.d(TAG, "addView diaryID="+items.get(i).diaryid);
			Log.d(TAG, "addView diaryuuid="+items.get(i).diaryuuid);
			Log.d(TAG, "addView updatetimemilli="+items.get(i).updatetimemilli);
			for(int l=0;l<items.get(i).attachs.length;l++){
				int show_width=0;
				int show_heigh=0;
				if(DateUtils.isNum(items.get(i).attachs[l].show_width))
					show_width=Integer.parseInt(items.get(i).attachs[l].show_width);
				if(DateUtils.isNum(items.get(i).attachs[l].show_height))
					show_heigh=Integer.parseInt(items.get(i).attachs[l].show_height);
				Log.d(TAG, "show_width="+show_width);
				Log.d(TAG, "show_heigh="+show_heigh);
				
				String attachType=items.get(i).attachs[l].attachtype;
				String attachLevel=items.get(i).attachs[l].attachlevel;
				String playtime=items.get(i).attachs[l].playtime;
				String videoCover=items.get(i).attachs[l].videocover;
				
				String imageUrl=getAttachUrl(items.get(i).attachs[l].attachimage);
				String audioUrl=getAttachUrl(items.get(i).attachs[l].attachaudio);
				String videoUrl=getAttachUrl(items.get(i).attachs[l].attachvideo);
				String mainText=items.get(i).attachs[l].content;
				
				if(imageUrl!=null&&imageUrl.length()>0&&imageUrl.startsWith("http")){
					imageUrl+="&width="+show_width+"&heigh="+show_heigh;
				}else if(imageUrl!=null&&imageUrl.length()>0){
					imageUrl+="&width="+getDiaryWidth();
				}
				Log.d(TAG, "videoCover="+videoCover);
				Log.d(TAG, "imageUrl="+imageUrl);
				Log.d(TAG, "audioUrl="+audioUrl);
				Log.d(TAG, "videoUrl="+videoUrl);
				
				if("1".equals(attachLevel)&&"1".equals(attachType)){//主内容为视频
					ivPic.setImageUrl(0, 1, videoCover, false);
					ivPic.setTag(videoCover);
					android.view.ViewGroup.LayoutParams params=ivPic.getLayoutParams();
					if(show_width!=0)
						params.width=show_width;
					if(show_heigh!=0)
						params.height=show_heigh;
					ivPic.setLayoutParams(params);
				}
				if("1".equals(attachLevel)&&"3".equals(attachType)){//主内容为图片
					if(imageUrl!=null&&imageUrl.length()>0){
						ivPic.setImageUrl(0, 1, imageUrl, false);
						ivPic.setTag(imageUrl);
					}else{
						ivPic.setImageUrl(0, 1, items.get(i).attachs[l].attachuuid, false);
						ivPic.setTag(items.get(i).attachs[l].attachuuid);
					}
					android.view.ViewGroup.LayoutParams params=ivPic.getLayoutParams();
					if(show_width!=0)
						params.width=show_width;
					if(show_heigh!=0)
						params.height=show_heigh;
					ivPic.setLayoutParams(params);
				}
				if("1".equals(attachLevel)&&"2".equals(attachType)){//主内容为音频  附件类型，1视频、2音频、3图片、4文字
					tvMainText.setText(DateUtils.getPlayTime(playtime));
				}
				if("1".equals(attachLevel)&&"4".equals(attachType)){//主内容为文字
					replacedExpressions(mainText, tvMainText);
				}
				
				if("0".equals(attachLevel)&&"2".equals(attachType)){//辅内容为音频
					llBiezhen.setPlaytime(DateUtils.getPlayTime(playtime));
					if(null==audioUrl||0==audioUrl.length()){
						audioUrl=items.get(i).attachs[l].attachuuid;
					}
					llBiezhen.setTag(items.get(i).diaryuuid);
				}
				
				if("0".equals(attachLevel)&&"4".equals(attachType)){//辅内容为文字
					replacedExpressions(mainText, tvDescription);
				}
				
				if(type==0x101||type==0x1){//辅文字显示在主体上
					if("0".equals(attachLevel)&&"4".equals(attachType)){//辅内容为文字
						replacedExpressions(mainText, tvMainText);
					}
				}
			}
			switch (type) {
			case 0x10000000://主体 视频
				rlMainTextContent.setVisibility(View.GONE);
				llDescription.setVisibility(View.GONE);
				llBiezhen.setVisibility(View.GONE);
				break;
			case 0x10000100:{//主体 视频+辅 音频
				rlMainTextContent.setVisibility(View.GONE);
				ivCidai.setVisibility(View.GONE);
				llDescription.setVisibility(View.GONE);
				android.widget.RelativeLayout.LayoutParams biezhenPar=new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
				biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,llContent.getId());
				llBiezhen.setLayoutParams(biezhenPar);
				break;}
			case 0x10000101://主体 视频+辅 音频+文字
				rlMainTextContent.setVisibility(View.GONE);
				ivCidai.setVisibility(View.GONE);
				break;
			case 0x10000001://主体 视频+文字
				rlMainTextContent.setVisibility(View.GONE);
				llBiezhen.setVisibility(View.GONE);
				break;
				
				
			case 0x1000000://主体 音频
				ivPic.setVisibility(View.GONE);
				llDescription.setVisibility(View.GONE);
				llBiezhen.setVisibility(View.GONE);
				ivVideoButton.setVisibility(View.GONE);
				break;
			case 0x1000100:{//主体 音频+辅 音频
				ivPic.setVisibility(View.GONE);
				llDescription.setVisibility(View.GONE);
				ivVideoButton.setVisibility(View.GONE);
				android.widget.RelativeLayout.LayoutParams biezhenPar=new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
				biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,llContent.getId());
				llBiezhen.setLayoutParams(biezhenPar);
				break;
				}
			case 0x1000101://主体 音频+辅 音频+文字
				ivPic.setVisibility(View.GONE);
				ivVideoButton.setVisibility(View.GONE);
				break;
			case 0x1000001://主体音频+文字
				ivPic.setVisibility(View.GONE);
				llBiezhen.setVisibility(View.GONE);
				ivVideoButton.setVisibility(View.GONE);
				break;
				
				
			case 0x100000://主体 图片
				ivVideoButton.setVisibility(View.GONE);
				rlMainTextContent.setVisibility(View.GONE);
				llDescription.setVisibility(View.GONE);
				llBiezhen.setVisibility(View.GONE);
				break;
			case 0x100001://主体 图片 +文字
				ivVideoButton.setVisibility(View.GONE);
				rlMainTextContent.setVisibility(View.GONE);
				llBiezhen.setVisibility(View.GONE);
				break;
			case 0x100100:{//主体 图片+辅 音频
				ivVideoButton.setVisibility(View.GONE);
				rlMainTextContent.setVisibility(View.GONE);
				llDescription.setVisibility(View.GONE);
				android.widget.RelativeLayout.LayoutParams biezhenPar=new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
				biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,llContent.getId());
				llBiezhen.setLayoutParams(biezhenPar);
				break;}
			case 0x100101://主体 图片+辅 音频+文字
				ivVideoButton.setVisibility(View.GONE);
				rlMainTextContent.setVisibility(View.GONE);
				break;
				
			case 0x10000://主体 文字
				ivVideoButton.setVisibility(View.GONE);
				ivPic.setVisibility(View.GONE);
				llDescription.setVisibility(View.GONE);
				llBiezhen.setVisibility(View.GONE);
				ivCidai.setVisibility(View.GONE);
				break;
			case 0x10001://主体 文字+文字
				ivVideoButton.setVisibility(View.GONE);
				ivPic.setVisibility(View.GONE);
				ivCidai.setVisibility(View.GONE);
				llBiezhen.setVisibility(View.GONE);
				break;
			case 0x10100:{//主体 文字+辅 音频
				ivVideoButton.setVisibility(View.GONE);
				ivPic.setVisibility(View.GONE);
				llBiezhen.setVisibility(View.GONE);
				ivCidai.setVisibility(View.GONE);
				llDescription.setVisibility(View.GONE);
				android.widget.RelativeLayout.LayoutParams biezhenPar=new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
				biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,llContent.getId());
				llBiezhen.setLayoutParams(biezhenPar);
				break;}
			case 0x10101://主体 文字+辅 音频+文字
				ivVideoButton.setVisibility(View.GONE);
				ivPic.setVisibility(View.GONE);
				ivCidai.setVisibility(View.GONE);
				break;
			case 0x100:{//辅 音频
				ivVideoButton.setVisibility(View.GONE);
				ivPic.setVisibility(View.GONE);
				llDescription.setVisibility(View.GONE);
				ivCidai.setVisibility(View.GONE);
				android.widget.RelativeLayout.LayoutParams biezhenPar=new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
				biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,llContent.getId());
				llBiezhen.setLayoutParams(biezhenPar);
				break;}
			case 0x101:{//辅 音频+文字
				ivVideoButton.setVisibility(View.GONE);
				ivPic.setVisibility(View.GONE);
				llDescription.setVisibility(View.GONE);
				ivCidai.setVisibility(View.GONE);
				android.widget.RelativeLayout.LayoutParams biezhenPar=new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
				biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,llContent.getId());
				llBiezhen.setLayoutParams(biezhenPar);
				break;}
			case 0x1://辅 文字
				ivVideoButton.setVisibility(View.GONE);
				ivPic.setVisibility(View.GONE);
				llBiezhen.setVisibility(View.GONE);
				llDescription.setVisibility(View.GONE);
				ivCidai.setVisibility(View.GONE);
				break;
			default:
				break;
			}
			
			ImageView ivPoine=new ImageView(getContext());
			android.widget.RelativeLayout.LayoutParams poinePar=new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			poinePar.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
			poinePar.addRule(RelativeLayout.ALIGN_TOP,v.getId());
			android.widget.RelativeLayout.LayoutParams weatherPar=new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			android.widget.RelativeLayout.LayoutParams stickPar=new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			Bitmap bmStick=BitmapFactory.decodeResource(getResources(), R.drawable.ding);
			weatherPar.width=context.getResources().getDimensionPixelSize(
					R.dimen.size_30dip);
			weatherPar.height=context.getResources().getDimensionPixelSize(
					R.dimen.size_30dip);
			if(p.isLeft){//左边 修改中间的箭头指示图标，天气和小棒子的位置及小棒子的方向
				ivPoine.setImageResource(R.drawable.poine_2);
				weatherPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
				weatherPar.leftMargin=context.getResources().getDimensionPixelSize(
						R.dimen.size_15dip);
				stickPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
				Matrix m=new Matrix();
				m.setRotate(-50);
				bmStick=Bitmap.createBitmap(bmStick, 0, 0, bmStick.getWidth(), bmStick.getHeight(), m, true);
			}else{//右边
				ivPoine.setImageResource(R.drawable.point_3);
				weatherPar.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
				weatherPar.rightMargin=context.getResources().getDimensionPixelSize(
						R.dimen.size_15dip);
				stickPar.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
			}
			ivWeather.setLayoutParams(weatherPar);
			ivStick.setImageBitmap(bmStick);
			ivStick.setLayoutParams(stickPar);
			ivPoine.setLayoutParams(poinePar);
			android.widget.RelativeLayout.LayoutParams par=getParams(v,p);
			rlListView.addView(v, par);
			if(1001 == p.viewId)ivPoine.setPadding(0, marginTop, 0, 0);
			rlListView.addView(ivPoine, poinePar);
		}
	}
	
	public static String getAttachUrl(MyAttachImage[] attachImages){
		if(null==attachImages||0==attachImages.length){
			Log.e(TAG, "attachImages is null");
			return null;
		}
		if(attachImages.length>1){
			for(int i=0;i<attachImages.length;i++){
				if("0".equals(attachImages[i].imagetype)){
					return attachImages[i].imageurl;
				}
			}
		}
		return attachImages[0].imageurl;
	}
	
	public static String getAttachUrl(MyAttachAudio[] attachAudios){
		if(null==attachAudios||0==attachAudios.length){
			Log.e(TAG, "attachAudios is null");
			return null;
		}
		//TODO 根据类型返回需要的url
		return attachAudios[0].audiourl;
	}
	
	public static String getAttachUrl(MyAttachVideo[] attachVideos){
		if(null==attachVideos||0==attachVideos.length){
			Log.e(TAG, "attachVideos is null");
			return null;
		}
		//TODO 根据类型返回需要的url
		return attachVideos[0].playvideourl;
	}
	
	public static int getDiaryType(diaryAttach[] attachs){
		int diaryType=0;
		if(null==attachs||0==attachs.length){
			Log.e(TAG, "diary attachs is null");
			return diaryType;
		}
		
		List<diaryAttach> attachList=Arrays.asList(attachs);
		int len=attachList.size();
		for(int i=0;i<len;i++){
			diaryAttach attach=attachList.get(i);
			if(attach!=null&&"1".equals(attach.attachlevel)){//主内容
				int attachType=Integer.parseInt(attach.attachtype);
				switch (attachType) {
				case 1://视频
					diaryType=diaryType|0x10000000;
					break;
				case 2://音频
					diaryType=diaryType|0x1000000;
					break;
				case 3://图片
					diaryType=diaryType|0x100000;
					break;
				case 4://文字
					diaryType=diaryType|0x10000;
					break;

				default:
					break;
				}
			}
			if(attach!=null&&"0".equals(attach.attachlevel)){//副内容
				int attachType=Integer.parseInt(attach.attachtype);
				switch (attachType) {
				case 1://视频
					diaryType=diaryType|0x1000;
					break;
				case 2://音频
					diaryType=diaryType|0x100;
					break;
				case 3://图片
					diaryType=diaryType|0x10;
					break;
				case 4://文字
					diaryType=diaryType|0x1;
					break;

				default:
					break;
				}
			}
		}
		Log.d(TAG, String.format("DiaryType=0x%x", diaryType));
		return diaryType;
	}
	
	class params{
		int lId = 0;
		int rId = 0;
		boolean isLeft = true;
		int lHeight = 0;
		int viewId = 999;
		boolean isMarginTop=false;
		
		public void reset(){
			lId = 0;
			rId = 0;
			isLeft = true;
			lHeight = 0;
			viewId = 999;
		}
	}
	
	int width;
	int height;
	public int getVedioWidth(){
		return width;
	}
	
	public int getVedioHeight(){
		return height;
	}
	
	int margin=2;
	private android.widget.RelativeLayout.LayoutParams getParams(View v,params p) {
		measureView(v);
		int height = v.getMeasuredHeight();
		int width=getScreenWidth()/2-20;
		int size_3 = context.getResources().getDimensionPixelSize(
				R.dimen.size_3dip);
		android.widget.RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(
				width, LayoutParams.WRAP_CONTENT);
		params.setMargins(0, size_3, 0, 0);
		if (1000 == p.viewId || 1001 == p.viewId) {
			if (1000 == p.viewId) {
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
						RelativeLayout.TRUE);
				params.addRule(RelativeLayout.ALIGN_PARENT_TOP,
						RelativeLayout.TRUE);
				p.lHeight = height;
				p.lId = v.getId();
				p.isLeft = false;
			} else {
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
						RelativeLayout.TRUE);
				params.addRule(RelativeLayout.ALIGN_PARENT_TOP,
						RelativeLayout.TRUE);
				if (height >= p.lHeight) {
					p.isLeft = true;
					p.lHeight = height - p.lHeight;
				} else {
					p.lHeight = p.lHeight - height;
					p.isLeft = false;
				}
				p.rId = v.getId();
				v.setPadding(0, marginTop, 0, 0);
			}
		} else {
			if (p.isLeft) {
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
						RelativeLayout.TRUE);
				params.addRule(RelativeLayout.BELOW, p.lId);
				if (height >= p.lHeight) {
					p.lHeight = height - p.lHeight;
					p.isLeft = false;
				} else {
					p.lHeight = p.lHeight - height;
					p.isLeft = true;
				}
				p.lId = v.getId();
			} else {
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
						RelativeLayout.TRUE);
				params.addRule(RelativeLayout.BELOW, p.rId);
				if (height >= p.lHeight) {
					p.lHeight = height - p.lHeight;
					p.isLeft = true;
				} else {
					p.lHeight = p.lHeight - height;
					p.isLeft = false;
				}
				p.rId = v.getId();
			}
		}
		return params;
	}
}
