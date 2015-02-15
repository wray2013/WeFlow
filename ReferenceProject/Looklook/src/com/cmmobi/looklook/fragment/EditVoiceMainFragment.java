package com.cmmobi.looklook.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.EditVoiceDetailActivity;
import com.cmmobi.looklook.common.utils.EffectsDownloadUtil;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobivideo.utils.PluginUtils;

public class EditVoiceMainFragment extends Fragment implements OnClickListener{

	private static final String TAG = "EditVoiceMainFragment";
	private EditVoiceDetailActivity mActivity;
	
	private LinearLayout editDetailLayout = null;
	private RelativeLayout montageLayout = null;
	private RelativeLayout soundtrackLayout = null;
	private TextView tvTimeRange = null;
	private TextView tvSoundTrack = null;
	private String soundTrackText = "";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_edit_voice_main, container, false);
		editDetailLayout = (LinearLayout) view.findViewById(R.id.ll_edit_voice_detail);
		editDetailLayout.setOnClickListener(this);
		montageLayout = (RelativeLayout) view.findViewById(R.id.rl_edit_voice_crop);
		montageLayout.setOnClickListener(this);
		soundtrackLayout = (RelativeLayout) view.findViewById(R.id.rl_edit_voice_soundtrack);
		soundtrackLayout.setOnClickListener(this);
		
		tvTimeRange = (TextView) view.findViewById(R.id.tv_edit_voice_montage_range);
		mActivity.setMontageTimeRange(mActivity.montageStartTime, mActivity.montageEndTime);
		
		tvSoundTrack = (TextView) view.findViewById(R.id.tv_edit_voice_soundtrack);
		if (!"".equals(soundTrackText)) {
			tvSoundTrack.setText(soundTrackText);
		}
		
		mActivity.mainLayout.setVisibility(View.VISIBLE);
		mActivity.detailMainLayout.setVisibility(View.GONE);
		return view;
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()) {
		case R.id.ll_edit_voice_detail:
			if (mActivity.mMediaPlayer != null && mActivity.mMediaPlayer.isPlaying()) {
				mActivity.mMediaPlayer.pause();
				mActivity.ivPlay.setImageResource(R.drawable.btn_edit_audio_play);
			}
			mActivity.goToPage(EditVoiceDetailActivity.FRAGMENT_DETAIL_MAIN_VIEW, false);
			break;
		case R.id.rl_edit_voice_crop:
			if (mActivity.totalTime < 5) {
				Prompt.Alert(mActivity, "内容时间过短不支持剪切功能");
				break;
			}
			if (!EffectsDownloadUtil.getInstance(mActivity).checkEffects()) {
				mActivity.goToPage(EditVoiceDetailActivity.FRAGMENT_CROP_VIEW, false);
				if (mActivity.mMediaPlayer != null && mActivity.mMediaPlayer.isPlaying()) {
					mActivity.mMediaPlayer.pause();
					mActivity.ivPlay.setImageResource(R.drawable.btn_edit_audio_play);
				}
			}
			break;
		case R.id.rl_edit_voice_soundtrack:
			if (!EffectsDownloadUtil.getInstance(mActivity).checkEffects()) {
				mActivity.goToPage(EditVoiceDetailActivity.FRAGMENT_SOUNDTRACK_VIEW, false);
				if (mActivity.mMediaPlayer != null && mActivity.mMediaPlayer.isPlaying()) {
					mActivity.mMediaPlayer.pause();
					mActivity.ivPlay.setImageResource(R.drawable.btn_edit_audio_play);
				}
			}
			break;
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Log.e(TAG, "EditVoiceMainFragment - onAttach");

		try {
			mActivity = (EditVoiceDetailActivity) activity;
			// onGotoPageListener = (OnGotoPageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnGotoPageListener");
		}
	}
	
	public void setTimeRange(int startTime,int endTime) {
		
		String startTimeStr = convertTimeFormartMS(String.valueOf(startTime));
		String endTimeStr = convertTimeFormartMS(String.valueOf(endTime));
		if (startTimeStr != null && endTimeStr != null) {
			tvTimeRange.setText(startTimeStr + "-" + endTimeStr);
			Log.d(TAG,"setTimeRange in startTime = " + startTimeStr + " endTime = " + endTimeStr);
		}
	}
	
	public static String convertTimeFormartMS(String timeStr) {
		if (timeStr == null) {
			 return "00:00";
		}
		
		int duration = 0;
		try {
		    duration = Integer.valueOf(timeStr);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return "00:00";
		}
		int minute = duration / 60;
		int second = duration % 60;
		String minStr = "";
		String secStr = "";
		if (minute < 10) {
			minStr = "0" + minute;
		} else {
			minStr = "" + minute;
		}
		
		if (second < 10) {
			secStr = "0" + second;
		} else {
			secStr = "" + second;
		}
		
		return minStr + ":" + secStr;
	}
	
	public void setSoundTrackText(String text) {
		soundTrackText = text;
		tvSoundTrack.setText(text);
	}

}
