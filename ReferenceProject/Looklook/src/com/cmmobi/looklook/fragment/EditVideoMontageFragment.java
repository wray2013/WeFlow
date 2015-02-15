package com.cmmobi.looklook.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.EditVideoDetailActivity;
import com.cmmobi.looklook.common.adapter.ImageAdapter;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.view.VideoMontageView;
import com.cmmobi.looklook.common.view.VideoMontageView.VideoMontageChangeListener;
import com.cmmobi.looklook.prompt.Prompt;

public class EditVideoMontageFragment extends Fragment implements VideoMontageChangeListener, OnClickListener {

	private EditVideoDetailActivity mActivity;
	private final String TAG = "EditVideoMontageFragment";
	private TextView startTimeTv = null;
	private TextView endTimeTv = null;
	private GridView gridview = null;
	private VideoMontageView montageView = null;
	private FrameLayout frameLayout = null;
	
	private ImageView confirmBtn = null;
	private ImageView cancleBtn = null;
	
	private ImageView ivSelectDelete = null;
	private ImageView ivSelectSave = null;
	private LinearLayout llSelectDelete = null;
	private LinearLayout llSelectSave = null;
	
	boolean isFirstShow = true;
	public double totalTime = 0;
	long duration = 0;// 单位为s
	int totalWidth = 0;
	int startValue = 0;
	int endValue = 0;
	ImageAdapter adapter = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_edit_video_montage, container, false);
		CmmobiClickAgentWrapper.onEvent(mActivity, "my_edit_vi", "1");
		CmmobiClickAgentWrapper.onEventBegin(mActivity, "my_edit_vi", "1");
        
        startTimeTv = (TextView) view.findViewById(R.id.tv_start_time_text);
        endTimeTv = (TextView) view.findViewById(R.id.tv_end_time_text);
        
        gridview = (GridView) view.findViewById(R.id.gv_edit_video_montage_imgs);
        adapter = new ImageAdapter(mActivity);
        gridview.setAdapter(adapter);
        
        frameLayout = (FrameLayout) view.findViewById(R.id.fl_edit_video_montage);
        montageView = (VideoMontageView) view.findViewById(R.id.vv_edit_video_montageview);
        
        totalTime = mActivity.totalTime;
        duration = mActivity.duration;
        startValue = mActivity.startValue;
        endValue = mActivity.endValue;
        montageView.setVideoMontageChangeListener(this,startValue,endValue);
        montageView.setTotalTime(totalTime);
        
        confirmBtn = (ImageView) view.findViewById(R.id.iv_edit_video_montage_yes);
        cancleBtn = (ImageView) view.findViewById(R.id.iv_edit_video_montage_no);
        confirmBtn.setOnClickListener(this);
        cancleBtn.setOnClickListener(this);
        
        ivSelectDelete = (ImageView) view.findViewById(R.id.iv_edit_video_montage_delete);
        ivSelectSave = (ImageView) view.findViewById(R.id.iv_edit_video_montage_save);
        llSelectDelete = (LinearLayout) view.findViewById(R.id.ll_fragment_edit_video_montage_delete);
        llSelectSave = (LinearLayout) view.findViewById(R.id.ll_fragment_edit_video_montage_save);
        llSelectDelete.setOnClickListener(this);
        llSelectSave.setOnClickListener(this);
        
        if (mActivity.isSelectedDelete) {
        	ivSelectDelete.setImageResource(R.drawable.gou_pressed);
			ivSelectSave.setImageResource(R.drawable.gou_normal);
        } else {
        	ivSelectDelete.setImageResource(R.drawable.gou_normal);
			ivSelectSave.setImageResource(R.drawable.gou_pressed);
        }
        
        mActivity.generateThumbsCovers(adapter);
        
        isFirstShow = true;
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Log.e(TAG, "EditVoiceMontageFragment - onAttach");

		try {
			mActivity = (EditVideoDetailActivity) activity;
			// onGotoPageListener = (OnGotoPageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnGotoPageListener");
		}
	}

	@Override
	public void videoMontageValueChanged(int Thumb1Value, int Thumb2Value) {
		// TODO Auto-generated method stub
		if (isFirstShow) {
			isFirstShow = false;
			startTimeTv.setVisibility(View.VISIBLE);
			endTimeTv.setVisibility(View.VISIBLE);
			totalWidth = montageView.getTotalWidth();
		}
		
		int startWidth = startTimeTv.getWidth();
		int marginLeft = frameLayout.getLeft();
		Log.d("=AAA="," marginLeft = " + marginLeft + " satrtWidth = " + startWidth + " thumb1Value = " + Thumb1Value + " thumb2Value = " + Thumb2Value);

		LayoutParams leftParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		leftParams.setMargins(marginLeft - startWidth  + Thumb1Value + montageView.getThumbWidth(), 0, 0, 0);
		
		LayoutParams rightParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		rightParams.setMargins(Thumb2Value - Thumb1Value, 0, 0, 0);
		
		startTimeTv.setLayoutParams(leftParams);
		endTimeTv.setLayoutParams(rightParams);
		
		startTimeTv.setText(calculateTime(Thumb1Value));
	    endTimeTv.setText(calculateTime(Thumb2Value));
	    
	    startValue = Thumb1Value;
	    endValue = Thumb2Value;
	    
	    startTimeTv.requestLayout();
	    endTimeTv.requestLayout();
	}
	
	public String calculateTime(int value) {
		Log.d("=AAA=","value = " + value + " duration = " + duration + " totalWidth = " + totalWidth);
		int retValue = (int) (value * totalTime / totalWidth);
		int minute = retValue / 60;
		String minuteStr = String.valueOf(minute);
		if (minute < 10) {
			minuteStr = "0" + minute;
		}
		int second = retValue % 60;
		String secondStr = String.valueOf(second);
		if (second < 10) {
			secondStr = "0" + second;
		}
		return minuteStr + ":" + secondStr;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_edit_video_montage_yes:
			totalWidth = montageView.getTotalWidth();
			if (totalWidth != 0 && totalTime != 0){
				float startTime = (float) (((float)startValue / totalWidth) * totalTime) ;
				float endTime = (float) (((float)endValue / totalWidth) * totalTime) ;
				mActivity.setMontageTimeRange(startTime, endTime);
				Log.d(TAG,"startValue = " + startValue + " endValue = " + endValue + " totalWidth = " + montageView.getTotalWidth() + " thumbWidth = " + montageView.getThumbWidth());
				if ((endValue - startValue) != 0 && (endValue -startValue) != montageView.getTotalWidth()) {
					mActivity.isDiaryMontaged = true;
					mActivity.isMainAttachChanged = true;
					mActivity.startValue = startValue;
					mActivity.endValue = endValue;
				} else {
					mActivity.isDiaryMontaged = false;
					mActivity.startValue = startValue;
					mActivity.endValue = endValue;
				}
			}
			CmmobiClickAgentWrapper.onEventEnd(mActivity, "my_edit_vi", "1");
			mActivity.goToPage(EditVideoDetailActivity.FRAGMENT_MAIN_VIEW, false);
			break;
		case R.id.iv_edit_video_montage_no:
			totalWidth = montageView.getTotalWidth();
			mActivity.videoMontageStartTime = mActivity.montageStartTime;
			mActivity.videoMontageEndTime = mActivity.montageEndTime;
			mActivity.seekBarCropUp(mActivity.startValue, mActivity.endValue, totalWidth);
			CmmobiClickAgentWrapper.onEventEnd(mActivity, "my_edit_vi", "1");
			mActivity.goToPage(EditVideoDetailActivity.FRAGMENT_MAIN_VIEW, false);
			break;
		case R.id.ll_fragment_edit_video_montage_delete:
			mActivity.isSelectedDelete = true;
			ivSelectDelete.setImageResource(R.drawable.gou_pressed);
			ivSelectSave.setImageResource(R.drawable.gou_normal);
			break;
		case R.id.ll_fragment_edit_video_montage_save:
			mActivity.isSelectedDelete = false;
			ivSelectDelete.setImageResource(R.drawable.gou_normal);
			ivSelectSave.setImageResource(R.drawable.gou_pressed);
			break;
		}
	}

	@Override
	public void setThumbValues(int thumb1Value, int thumb2Value) {
		// TODO Auto-generated method stub
		startValue = thumb1Value;
		endValue = thumb2Value;
		if (mActivity.endValue == 0) {
			mActivity.endValue = endValue;
		}
		totalWidth = montageView.getTotalWidth();
	}

	@Override
	public void seekBarCropUp(int thumb1Value, int thumb2Value) {
		// TODO Auto-generated method stub
		mActivity.seekBarCropUp(thumb1Value, thumb2Value, totalWidth);
	}
	

}
