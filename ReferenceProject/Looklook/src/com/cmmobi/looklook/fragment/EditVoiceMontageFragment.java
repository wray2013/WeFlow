package com.cmmobi.looklook.fragment;


import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.EditVoiceDetailActivity;
import com.cmmobi.looklook.common.view.SeekBarWithTwoThumb;
import com.cmmobi.looklook.common.view.SeekBarWithTwoThumb.SeekBarChangeListener;
import com.cmmobi.looklook.prompt.Prompt;

public class EditVoiceMontageFragment extends Fragment implements SeekBarChangeListener, OnClickListener{

	private EditVoiceDetailActivity mActivity;
	private final String TAG = "EditVoiceMontageFragment";
	private SeekBarWithTwoThumb swtt;
	private TextView startTimeTv = null;
	private TextView endTimeTv = null;
	private ImageView confirmBtn = null;
	private ImageView cancleBtn = null;
	
	private ImageView ivSelectDelete = null;
	private ImageView ivSelectSave = null;
	private LinearLayout llSelectDelete = null;
	private LinearLayout llSelectSave = null;
	
	public double totalTime = 0;
	long duration = 0;// 单位为s
	int totalWidth = 0;
	boolean isFirstShow = true;
	int startValue = 0;
	int endValue = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_edit_voice_montage, container, false);
		swtt = (SeekBarWithTwoThumb) view.findViewById(R.id.myseekbar);
		startValue = mActivity.startValue;
        endValue = mActivity.endValue;
        
        totalTime = mActivity.totalTime;
        duration = mActivity.duration;
        swtt.setSeekBarChangeListener(this,startValue,endValue);
        swtt.setTotalTime(totalTime);
        
        startTimeTv = (TextView) view.findViewById(R.id.tv_start_time_text);
        endTimeTv = (TextView) view.findViewById(R.id.tv_end_time_text);
        
        confirmBtn = (ImageView) view.findViewById(R.id.iv_edit_voice_montage_yes);
        cancleBtn = (ImageView) view.findViewById(R.id.iv_edit_voice_montage_no);
        confirmBtn.setOnClickListener(this);
        cancleBtn.setOnClickListener(this);
        
        ivSelectDelete = (ImageView) view.findViewById(R.id.iv_edit_voice_montage_delete);
        ivSelectSave = (ImageView) view.findViewById(R.id.iv_edit_voice_montage_save);
        llSelectDelete = (LinearLayout) view.findViewById(R.id.ll_fragment_edit_voice_montage_delete);
        llSelectSave = (LinearLayout) view.findViewById(R.id.ll_fragment_edit_voice_montage_save);
        llSelectDelete.setOnClickListener(this);
        llSelectSave.setOnClickListener(this);
        
        Log.d(TAG,"isSelectedDelted = " + mActivity.isSelectedDelete);
        if (mActivity.isSelectedDelete) {
        	ivSelectDelete.setImageResource(R.drawable.gou_pressed);
			ivSelectSave.setImageResource(R.drawable.gou_normal);
        } else {
        	ivSelectDelete.setImageResource(R.drawable.gou_normal);
			ivSelectSave.setImageResource(R.drawable.gou_pressed);
        }
        
		/*if (mActivity.mainAudioPath != null) {
			try {
				final MediaPlayer mp=new MediaPlayer();
				mp.setDataSource(mActivity.mainAudioPath);
				mp.prepare();
				mp.setOnPreparedListener(new OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer arg0) {
						// TODO Auto-generated method stub
						totalTime = mp.getDuration();
						duration = totalTime;
						if (totalTime < 1000) {
							duration = 1000;
						}
						duration /= 1000;
						Log.d(TAG,"duration = " + totalTime);
					}
				});
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			
		}*/
		
		isFirstShow = true;
        
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Log.e(TAG, "EditVoiceMontageFragment - onAttach");

		try {
			mActivity = (EditVoiceDetailActivity) activity;
			// onGotoPageListener = (OnGotoPageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnGotoPageListener");
		}
	}
	
	@Override
	public void SeekBarValueChanged(int Thumb1Value, int Thumb2Value) {
		// TODO Auto-generated method stub
		if (isFirstShow) {
			isFirstShow = false;
			startTimeTv.setVisibility(View.VISIBLE);
			endTimeTv.setVisibility(View.VISIBLE);
			totalWidth = swtt.getTotalWidth();
		}
		
		int startWidth = startTimeTv.getWidth();
		int marginLeft = swtt.getLeft();
		Log.d(TAG," marginLeft = " + marginLeft + " satrtWidth = " + startWidth 
				+ " thumb1Value = " + Thumb1Value + " thumb2Value = " + Thumb2Value
				+ " totalTime = " + totalTime + " totalWidth = " + totalWidth);

		LayoutParams leftParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		leftParams.setMargins(marginLeft - startWidth  + Thumb1Value + swtt.getThumbWidth(), 0, 0, 0);
		
		LayoutParams rightParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		rightParams.setMargins(Thumb2Value - Thumb1Value, 0, 0, 0);
		
		startTimeTv.setLayoutParams(leftParams);
		endTimeTv.setLayoutParams(rightParams);
		int secondTime1 = (int) (Thumb1Value * totalTime / totalWidth);
		int secondTime2 = (int) (Thumb2Value * totalTime / totalWidth);
		/*if (secondTime1 == secondTime2) {
			if (swtt.getSelectThumb() == 1) {
				secondTime2 = secondTime1 + 1;
			} else if (swtt.getSelectThumb() == 2) {
				secondTime1 = secondTime2 - 1;
			}
		}*/
		startTimeTv.setText(calculateTime(secondTime1));
	    endTimeTv.setText(calculateTime(secondTime2));
	    startValue = Thumb1Value;
	    endValue = Thumb2Value;
	    
	    startTimeTv.requestLayout();
	    endTimeTv.requestLayout();
	}
	
	public String calculateTime(int value) {
		int minute = value / 60;
		String minuteStr = String.valueOf(minute);
		if (minute < 10) {
			minuteStr = "0" + minute;
		}
		int second = value % 60;
		String secondStr = String.valueOf(second);
		if (second < 10) {
			secondStr = "0" + second;
		}
		return minuteStr + ":" + secondStr;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()) {
		case R.id.iv_edit_voice_montage_yes:
			totalWidth = swtt.getTotalWidth();
			if (totalWidth != 0 && totalTime != 0) {
				float startTime = (float) (((float)startValue / totalWidth) * totalTime) ;
				float endTime = (float) (((float)endValue / totalWidth) * totalTime) ;
				mActivity.setMontageTimeRange(startTime, endTime);
				Log.d(TAG,"startValue = " + startValue + " endValue = " + endValue + " totalWidth = " + swtt.getTotalWidth());
				if ((endValue - startValue) != 0 && (endValue -startValue) != swtt.getTotalWidth()) {
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
			mActivity.goToPage(EditVoiceDetailActivity.FRAGMENT_MAIN_VIEW, false);
			break;
		case R.id.iv_edit_voice_montage_no:
			totalWidth = swtt.getTotalWidth();
			mActivity.goToPage(EditVoiceDetailActivity.FRAGMENT_MAIN_VIEW, false);
			mActivity.audioMontageStartTime = mActivity.montageStartTime;
			mActivity.audioMontageEndTime = mActivity.montageEndTime;
			mActivity.seekBarCropUp(mActivity.startValue, mActivity.endValue, totalWidth);
			break;
		case R.id.ll_fragment_edit_voice_montage_delete:
			mActivity.isSelectedDelete = true;
			ivSelectDelete.setImageResource(R.drawable.gou_pressed);
			ivSelectSave.setImageResource(R.drawable.gou_normal);
			break;
		case R.id.ll_fragment_edit_voice_montage_save:
			mActivity.isSelectedDelete = false;
			ivSelectDelete.setImageResource(R.drawable.gou_normal);
			ivSelectSave.setImageResource(R.drawable.gou_pressed);
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
		totalWidth = swtt.getTotalWidth();
	}

	@Override
	public void seekBarCropUp(int thumb1Value, int thumb2Value) {
		// TODO Auto-generated method stub
		mActivity.seekBarCropUp(thumb1Value, thumb2Value, totalWidth);
	}

}
