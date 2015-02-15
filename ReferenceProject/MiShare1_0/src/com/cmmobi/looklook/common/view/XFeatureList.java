package com.cmmobi.looklook.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cmmobi.looklook.R;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-10-23 主页+号按钮公用组件 需要使用按钮点击和触摸事件时请implements
 *       onFeatureClicklistener接口和onFeatureToucherListener接口
 */
public class XFeatureList extends LinearLayout implements OnClickListener,
		OnLongClickListener,OnTouchListener {

	private static final String TAG = XFeatureList.class.getSimpleName();

	public XFeatureList(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private View vPicture;
	private View vVideo;
	private View vRecord;
	private View vNote;
	private View vImport;
	private ImageView vFeature;

	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.include_feature_list, null);
		addView(view);
		vPicture = view.findViewById(R.id.btn_picture);
		vVideo = view.findViewById(R.id.btn_video);
		vRecord = view.findViewById(R.id.btn_record);
		vNote = view.findViewById(R.id.btn_note);
		vImport = view.findViewById(R.id.btn_import);

		vPicture.setOnClickListener(this);
		vVideo.setOnClickListener(this);
		vRecord.setOnClickListener(this);
		vNote.setOnClickListener(this);
		vImport.setOnClickListener(this);
		
		vPicture.setOnTouchListener(this);
		vVideo.setOnTouchListener(this);
		vRecord.setOnTouchListener(this);
		vNote.setOnTouchListener(this);
		vImport.setOnTouchListener(this);

		vPicture.setOnLongClickListener(this);
		vVideo.setOnLongClickListener(this);
		vRecord.setOnLongClickListener(this);
		vNote.setOnLongClickListener(this);
		vImport.setOnLongClickListener(this);
		vFeature=(ImageView) view.findViewById(R.id.btn_feature);
		vFeature.setOnClickListener(this);
		hide();
	}

	@Override
	public boolean onLongClick(View v) {
		Log.d(TAG, "onLongClick");
		switch (v.getId()) {
		case R.id.btn_picture:
			if (longClickListener != null)
				longClickListener.onPictureBtnLongClick(v);
			break;
		case R.id.btn_video:
			if (longClickListener != null)
				longClickListener.onVideoBtnLongClick(v);
			break;
		case R.id.btn_record:
			if (longClickListener != null)
				longClickListener.onRecordBtnLongClick(v);
			break;
		case R.id.btn_note:
			if (longClickListener != null)
				longClickListener.onNoteBtnLongClick(v);
			break;
		case R.id.btn_import:
			if (longClickListener != null)
				longClickListener.onImportBtnLongClick(v);
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		Log.d(TAG, "onClick");
		switch (v.getId()) {
		case R.id.btn_feature:
			if (null == v.getTag()) {
				show();
			} else {
				hide();
			}
			break;
		case R.id.btn_picture:
			if (onClickListener != null)
				onClickListener.onPictureBtnClick(v);
			break;
		case R.id.btn_video:
			if (onClickListener != null)
				onClickListener.onVideoBtnClick(v);
			break;
		case R.id.btn_record:
			if (onClickListener != null)
				onClickListener.onRecordBtnClick(v);
			break;
		case R.id.btn_note:
			if (onClickListener != null)
				onClickListener.onNoteBtnClick(v);
			break;
		case R.id.btn_import:
			if (onClickListener != null)
				onClickListener.onImportBtnClick(v);
			break;

		default:
			break;
		}
	}

	/**
	 *  显示功能栏
	 */
	public void show() {
		vPicture.setVisibility(View.VISIBLE);
		vVideo.setVisibility(View.VISIBLE);
		vRecord.setVisibility(View.VISIBLE);
		vNote.setVisibility(View.VISIBLE);
		vImport.setVisibility(View.VISIBLE);
		vFeature.setImageResource(R.drawable.btn_feature_2);
		vFeature.setTag("showFeature");
		if(hiddenChangedListener!=null)
			hiddenChangedListener.onHiddenChanged(false);
	}
	
	/**
	 * 判断功能栏是否显示
	 * true-显示
	 * false-隐藏
	 */
	public boolean isShow(){
		return vPicture.getVisibility()==View.VISIBLE;
	}

	/**
	 *  隐藏功能栏
	 */
	public void hide() {
		vPicture.setVisibility(View.GONE);
		vVideo.setVisibility(View.GONE);
		vRecord.setVisibility(View.GONE);
		vNote.setVisibility(View.GONE);
		vImport.setVisibility(View.GONE);
		vFeature.setImageResource(R.drawable.btn_feature_1);
		vFeature.setTag(null);
		if(hiddenChangedListener!=null)
			hiddenChangedListener.onHiddenChanged(true);
	}

	private onFeatureClicklistener onClickListener;

	public void setOnFeatureClicklistener(onFeatureClicklistener onClickListener) {
		this.onClickListener = onClickListener;
	}

	private onFeatureLongClickListener longClickListener;

	public void setOnFeatureLongClicklistener(
			onFeatureLongClickListener longClickListener) {
		this.longClickListener = longClickListener;
	}
	
	private onFeatureToucherListener onTouchListener;

	public void setOnFeatureTouchlistener(
			onFeatureToucherListener onTouchListener) {
		this.onTouchListener = onTouchListener;
	}
	
	private onHiddenChangedListener hiddenChangedListener;
	public void setOnHiddenChangedListener(onHiddenChangedListener hiddenChangedListener){
		this.hiddenChangedListener=hiddenChangedListener;
	}

	public interface onFeatureClicklistener {
		void onPictureBtnClick(View view);

		void onVideoBtnClick(View view);

		void onRecordBtnClick(View view);

		void onNoteBtnClick(View view);

		void onImportBtnClick(View view);
	}

	public interface onFeatureLongClickListener {
		void onPictureBtnLongClick(View view);

		void onVideoBtnLongClick(View view);

		void onRecordBtnLongClick(View view);

		void onNoteBtnLongClick(View view);

		void onImportBtnLongClick(View view);
	}
	
	public interface onFeatureToucherListener {
		void onPictureBtnTouch(View view, MotionEvent event);

		void onVideoBtnTouch(View view, MotionEvent event);

		void onRecordBtnTouch(View view, MotionEvent event);

		void onNoteBtnTouch(View view, MotionEvent event);

		void onImportBtnTouch(View view, MotionEvent event);
	}
	
	public interface onHiddenChangedListener{
		void onHiddenChanged(boolean isHidden);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onTouch");
		switch (v.getId()) {
		case R.id.btn_picture:
			if (onTouchListener != null)
				onTouchListener.onPictureBtnTouch(v, event);
			break;
		case R.id.btn_video:
			if (onTouchListener != null)
				onTouchListener.onVideoBtnTouch(v, event);
			break;
		case R.id.btn_record:
			if (onTouchListener != null)
				onTouchListener.onRecordBtnTouch(v, event);
			break;
		case R.id.btn_note:
			if (onTouchListener != null)
				onTouchListener.onNoteBtnTouch(v, event);
			break;
		case R.id.btn_import:
			if (onTouchListener != null)
				onTouchListener.onImportBtnTouch(v, event);
			break;
		default:
			break;
		}
		return false;
	}
	
	
}
