package com.cmmobi.looklook.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.EditVideoDetailActivity;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.EffectsDownloadUtil;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobivideo.utils.PluginUtils;

public class EditVideoMainFragment extends Fragment implements OnClickListener{

	private static final String TAG = "EditVideoMainFragment";
	private EditVideoDetailActivity mActivity;
	private RelativeLayout effectLayout = null;
	private RelativeLayout montageLayout = null;
	private RelativeLayout frontCoverLayout = null;
	private RelativeLayout soundtrackLayout = null;
	private LinearLayout editDetailLayout = null;
	private TextView tvTimeRange = null;
	private ImageView frontCoverImage = null;
	private TextView tvSoundTrack = null;
	private String soundTrackText = "";
	private TextView tvEffect = null;
	private String effectText = "";
	private Bitmap mBitmap = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_edit_video_main, container, false);
		effectLayout = (RelativeLayout) view.findViewById(R.id.rl_activity_edit_video_effect);
		effectLayout.setOnClickListener(this);
		editDetailLayout = (LinearLayout) view.findViewById(R.id.ll_edit_video_detail);
		editDetailLayout.setOnClickListener(this);
		montageLayout = (RelativeLayout) view.findViewById(R.id.rl_edit_video_montage);
		montageLayout.setOnClickListener(this);
		frontCoverLayout = (RelativeLayout) view.findViewById(R.id.rl_edit_video_frontcover);
		frontCoverLayout.setOnClickListener(this);
		soundtrackLayout = (RelativeLayout) view.findViewById(R.id.rl_edit_video_soundtrack);
		soundtrackLayout.setOnClickListener(this);
		
		frontCoverImage = (ImageView) view.findViewById(R.id.iv_edit_video_front_cover);
		if (mActivity.frontCoverBmp != null) {
			frontCoverImage.setImageBitmap(mActivity.frontCoverBmp);
		}
		
		tvTimeRange = (TextView) view.findViewById(R.id.tv_edit_video_montage_range);
		if (mActivity.montageEndTime > 0) {
			mActivity.setMontageTimeRange(mActivity.montageStartTime, mActivity.montageEndTime);
		} else {
			mActivity.setMontageTimeRange(mActivity.montageStartTime, 0);
		}
		tvSoundTrack = (TextView) view.findViewById(R.id.tv_edit_video_soundtrack);
		if (!"".equals(soundTrackText)) {
			tvSoundTrack.setText(soundTrackText);
		}
		
		if (mBitmap != null) {
			frontCoverImage.setImageBitmap(mBitmap);
		}
		
		mActivity.mainLayout.setVisibility(View.VISIBLE);
		mActivity.detailMainLayout.setVisibility(View.GONE);
		
		tvEffect = (TextView) view.findViewById(R.id.tv_edit_video_effect);
		if (!"".equals(effectText)) {
			tvEffect.setText(effectText);
		}
		return view;
	}
	
	public void setFrontCoverBmp(Bitmap bmp) {
		mBitmap = bmp;
		if (frontCoverImage != null) {
			frontCoverImage.setImageBitmap(mBitmap);
		}
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.rl_activity_edit_video_effect:
//			Toast.makeText(mActivity, "特效库目前还无法使用", 2000).show();
			if (!EffectsDownloadUtil.getInstance(mActivity).checkEffects()) {
				mActivity.goToPage(EditVideoDetailActivity.FRAGMENT_EFFECT_VIEW, false);
				if (mActivity.mMediaPlayer != null && mActivity.mMediaPlayer.isPlaying()) {
					mActivity.mMediaPlayer.pause();
					mActivity.ivPlay.setImageResource(R.drawable.btn_edit_audio_play);
				}
			}
			break;
		case R.id.ll_edit_video_detail:
			if (mActivity.mMediaPlayer != null && mActivity.mMediaPlayer.isPlaying()) {
				mActivity.mMediaPlayer.pause();
				mActivity.ivPlay.setImageResource(R.drawable.btn_edit_audio_play);
			}
			mActivity.goToPage(EditVideoDetailActivity.FRAGMENT_DETAIL_MAIN_VIEW, false);
			break;
		case R.id.rl_edit_video_montage:
			if (mActivity.totalTime < 5) {
				Prompt.Alert(mActivity, "内容时间过短不支持剪切功能");
				break;
			}
			if (!EffectsDownloadUtil.getInstance(mActivity).checkEffects()) {
				mActivity.goToPage(EditVideoDetailActivity.FRAGMENT_CROP_VIEW, false);
				if (mActivity.mMediaPlayer != null && mActivity.mMediaPlayer.isPlaying()) {
					mActivity.mMediaPlayer.pause();
					mActivity.ivPlay.setImageResource(R.drawable.btn_edit_audio_play);
				}
			}
			
			break;
		case R.id.rl_edit_video_frontcover:
			if (!EffectsDownloadUtil.getInstance(mActivity).checkEffects()) {
				mActivity.goToPage(EditVideoDetailActivity.FRAGMENT_FRONTCOVER_VIEW, false);
				if (mActivity.mMediaPlayer != null && mActivity.mMediaPlayer.isPlaying()) {
					mActivity.mMediaPlayer.pause();
					mActivity.ivPlay.setImageResource(R.drawable.btn_edit_audio_play);
				}
			} 
			
			break;
		case R.id.rl_edit_video_soundtrack:
			if (!EffectsDownloadUtil.getInstance(mActivity).checkEffects()) {
				mActivity.goToPage(EditVideoDetailActivity.FRAGMENT_SOUNDTRACK_VIEW, false);
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

		Log.e(TAG, "NearByFragment - onAttach");

		try {
			mActivity = (EditVideoDetailActivity) activity;
			// onGotoPageListener = (OnGotoPageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnGotoPageListener");
		}
	}
	
	public void setTimeRange(int startTime,int endTime) {
		String startTimeStr = EditVoiceMainFragment.convertTimeFormartMS(String.valueOf(startTime));
		String endTimeStr = EditVoiceMainFragment.convertTimeFormartMS(String.valueOf(endTime));
		if (startTimeStr != null && endTimeStr != null && tvTimeRange != null) {
			tvTimeRange.setText(startTimeStr + "-" + endTimeStr);
			Log.d(TAG,"setTimeRange in startTime = " + startTimeStr + " endTime = " + endTimeStr);
		}
	}
	
	public void setSoundTrackText(String text) {
		soundTrackText = text;
		tvSoundTrack.setText(text);
	}
	
	public void setEffectText(String text) {
		effectText = text;
		tvEffect.setText(text);
	}
}
