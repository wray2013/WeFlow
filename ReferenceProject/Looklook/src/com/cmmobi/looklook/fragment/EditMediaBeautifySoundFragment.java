package com.cmmobi.looklook.fragment;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import cn.zipper.framwork.core.ZLayoutInflater;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.EditMediaDetailActivity;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobivideo.utils.EffectBean;

import effect.EffectType;

public class EditMediaBeautifySoundFragment extends Fragment implements OnClickListener {

	private static final String TAG = "EditVoiceSoundtrackFragment";
	private EditMediaDetailActivity mActivity;
	private ImageView confirmBtn = null;
	private ImageView cancleBtn = null;
	
	private RadioGroup beautifySoundLayer;
	private int beautifyEffectsSize = 0;
	private LinearLayout originalSoundLayout = null;
	private ImageView originalSoundImg = null;
	private SeekBar volumeSeek;
	private int beautifulSoundId = 0;
	private String beautifulSoundText = "";
	private String lastSoundEffectId;
	private long lastSoundEffectMillis;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		CmmobiClickAgentWrapper.onEvent(mActivity, "my_edit_de", "2");
		CmmobiClickAgentWrapper.onEventBegin(mActivity, "my_edit_de", "2");
		View view = inflater.inflate(R.layout.fragment_edit_media_beautysound, container, false);
		
		confirmBtn = (ImageView) view.findViewById(R.id.iv_edit_media_beautifysound_yes);
		cancleBtn = (ImageView) view.findViewById(R.id.iv_edit_media_beautifysound_no);
		
		confirmBtn.setOnClickListener(this);
		cancleBtn.setOnClickListener(this);
		
		beautifySoundLayer = (RadioGroup) view.findViewById(R.id.effects_wrap_layout);
		originalSoundLayout = (LinearLayout)view.findViewById(R.id.ll_edit_media_original_sound);
        originalSoundLayout.setOnClickListener(this);
        originalSoundImg = (ImageView) view.findViewById(R.id.iv_edit_media_original_sound);
        volumeSeek = (SeekBar) view.findViewById(R.id.sk_edit_voice_seekbar);
        final AudioManager mAudioManager = (AudioManager)mActivity.getSystemService(Context.AUDIO_SERVICE);
        final int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volumeSeek.setMax(maxVolume);
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeSeek.setProgress(currentVolume);
        
        volumeSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
				// TODO Auto-generated method stub
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		init();
		return view;
	}
	
	private void init() {
		if (mActivity.shortRecEffectBeans != null) {
			beautifyEffectsSize = mActivity.shortRecEffectBeans.size();
		}
		beautifySoundLayer.removeAllViews();
		
		for (int i = 1;i <= beautifyEffectsSize;i++) {
			EffectBean bean = mActivity.shortRecEffectBeans.get(i - 1);
			if (bean.getType() == EffectType.KEF_TYPE_ADD_MUSIC) {
				continue;
			}
			
			LinearLayout layout = (LinearLayout) ZLayoutInflater.inflate(R.layout.include_effects_soundtrack_buttons_stub);
			RadioButton radioButton = (RadioButton) layout.findViewById(R.id.radio_button);
			
			radioButton.setId(i);
			radioButton.setText(bean.getZHName());
			radioButton.setTag(bean);
			radioButton.setOnClickListener(this);
			radioButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			layout.removeAllViewsInLayout();
			beautifySoundLayer.addView(radioButton);
		}
		
		beautifulSoundId = mActivity.beautifulSoundId;
		Log.d(TAG,"init -----beautifulSoundId = " + beautifulSoundId);
		if (beautifulSoundId == 0) {
			beautifySoundLayer.clearCheck();
		} else {
			beautifySoundLayer.check(beautifulSoundId);
			originalSoundImg.setBackgroundResource(R.drawable.radio_transparent);
		}
		
		lastSoundEffectId = "0";
		lastSoundEffectMillis = TimeHelper.getInstance().now();
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()) {
		case R.id.iv_edit_media_beautifysound_yes:
			CmmobiClickAgentWrapper.onEventEnd(mActivity, "my_edit_de", "2");
			mActivity.goToPage(EditMediaDetailActivity.FRAGMENT_DETAIL_MAIN_VIEW, false);
			if (beautifulSoundId > 0) {
				mActivity.isAddSoundAttach = true;
				mActivity.beautifulSoundId = beautifulSoundId;
				mActivity.setBeautifulSoundText(beautifulSoundText);
			}
			
			break;
		case R.id.iv_edit_media_beautifysound_no:
			CmmobiClickAgentWrapper.onEventEnd(mActivity, "my_edit_de", "2");
			mActivity.goToPage(EditMediaDetailActivity.FRAGMENT_DETAIL_MAIN_VIEW, false);
			mActivity.shortRecEffect.cleanEffects();
			beautifySoundLayer.clearCheck();
			if (mActivity.shortRecEffectBeans != null && mActivity.beautifulSoundId > 0) {
				EffectBean bean = mActivity.shortRecEffectBeans.get(mActivity.beautifulSoundId - 1);
				beautifySoundLayer.check(mActivity.beautifulSoundId);
				mActivity.shortRecEffectUtils.changeAudioInflexion(mActivity.shortRecEffect, bean.getFilePath(), 
						mActivity.shortRecMediaPlayer.getChannels() , 
						mActivity.shortRecMediaPlayer.getSampleRate(), 
						mActivity.shortRecMediaPlayer.getBitsPerChannel(), 1);
				mActivity.tackView.playAudio(mActivity.audioPath,mActivity.shortRecMediaPlayer);
			}
			break;
		case R.id.ll_edit_media_original_sound:
			originalSoundImg.setBackgroundResource(R.drawable.yinxiao_xuanzhong);
			beautifySoundLayer.clearCheck();
			mActivity.tackView.playAudio(mActivity.audioPath,mActivity.shortRecMediaPlayer);
			mActivity.shortRecEffect.cleanEffects();
			beautifulSoundText = getString(R.string.edit_media_original_sound);
			break;
		default:
			if (view.getId() > 0 && view.getId() <= beautifyEffectsSize) {
				
				CmmobiClickAgentWrapper.onEventDuration(mActivity, "my_sound", lastSoundEffectId, TimeHelper.getInstance().now() - lastSoundEffectMillis);
				EffectBean bean = null;
				if (view.getTag() != null) {
					bean = (EffectBean) view.getTag();
				}
				beautifulSoundId = view.getId();
				if (bean != null) {
					mActivity.shortRecEffectUtils.changeAudioInflexion(mActivity.shortRecEffect, bean.getFilePath(), 
						mActivity.shortRecMediaPlayer.getChannels() , 
						mActivity.shortRecMediaPlayer.getSampleRate(), 
						mActivity.shortRecMediaPlayer.getBitsPerChannel(), 1);
					mActivity.tackView.playAudio(mActivity.audioPath,mActivity.shortRecMediaPlayer);
					beautifulSoundText = bean.getZHName();
				}
				originalSoundImg.setBackgroundResource(R.drawable.radio_transparent);
				
				lastSoundEffectId = String.valueOf(view.getId());
				lastSoundEffectMillis = TimeHelper.getInstance().now();
			}
		}
	}
	
	@Override
	public void onPause() {
		CmmobiClickAgentWrapper.onEventDuration(mActivity, "my_sound", lastSoundEffectId, TimeHelper.getInstance().now() - lastSoundEffectMillis);
		super.onPause();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Log.e(TAG, "EditVoiceSoundtrackFragment - onAttach");

		try {
			mActivity = (EditMediaDetailActivity) activity;
			// onGotoPageListener = (OnGotoPageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnGotoPageListener");
		}
	}
}
