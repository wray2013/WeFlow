package com.cmmobi.looklook.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
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
import com.cmmobi.looklook.activity.HomeActivity;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;

public class EditMediaSoundtrackFragment extends Fragment implements OnClickListener{

	private static final String TAG = "EditMediaSoundtrackFragment";
	private EditMediaDetailActivity mActivity;
	
	private ImageView confirmBtn = null;
	private ImageView cancleBtn = null;
	private RadioGroup soundtrackLayer;
	private ArrayList<AudioSoundTrackBean> trackBeanList = new ArrayList<AudioSoundTrackBean>();
	private AudioSoundTrackBean trackBean = null;
	private SeekBar volumeSeek;
	private LinearLayout originalSoundLayout = null;
	private ImageView originalSoundImg = null;
	private String soundTrackText = "";
	
	private int trackId = 0;
	private double percent = 0.5;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_edit_voice_soundtrack, container, false);
		CmmobiClickAgentWrapper.onEvent(mActivity, "my_edit_vi", "3");
		CmmobiClickAgentWrapper.onEventBegin(mActivity, "my_edit_vi", "3");
		confirmBtn = (ImageView) view.findViewById(R.id.iv_edit_voice_soundtrack_yes);
        cancleBtn = (ImageView) view.findViewById(R.id.iv_edit_voice_soundtrack_no);
        confirmBtn.setOnClickListener(this);
        cancleBtn.setOnClickListener(this);
        
        soundtrackLayer = (RadioGroup) view.findViewById(R.id.effects_wrap_layout);
        
        volumeSeek = (SeekBar) view.findViewById(R.id.sk_edit_voice_seekbar);
        final int max = volumeSeek.getMax();
        volumeSeek.setProgress((int)(max * mActivity.longRecPercent));
        volumeSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				if (trackBean != null) {
					percent = ((double) volumeSeek.getProgress()/ max);
					mActivity.effectUtils.addMusic(mActivity.mEffects, "effectcfg/audio/addmusic.cfg", trackBean.filePath, 
							mActivity.mMediaPlayer.getChannels() , 
							mActivity.mMediaPlayer.getSampleRate(), 
							mActivity.mMediaPlayer.getBitsPerChannel(), 
							percent, 
							1);
					Log.d(TAG,"precent = " + percent);
				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				
			}
		});
        originalSoundLayout = (LinearLayout)view.findViewById(R.id.ll_edit_media_original_sound);
        originalSoundLayout.setOnClickListener(this);
        originalSoundImg = (ImageView) view.findViewById(R.id.iv_edit_media_original_sound);
        init();
		return view;
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()) {
		case R.id.iv_edit_voice_soundtrack_yes:
			CmmobiClickAgentWrapper.onEventEnd(mActivity, "my_edit_vi", "3");
			mActivity.goToPage(EditMediaDetailActivity.FRAGMENT_MAIN_VIEW, false);
			mActivity.setSoundTrackText(soundTrackText);
			if (trackId != 0) {
				mActivity.effectState = 0;
				mActivity.isMainAttachChanged = true;
				mActivity.isAddSoundTrack = true;
			} else {
				mActivity.isAddSoundTrack = false;
			}
			mActivity.trackId = trackId;
			mActivity.longRecPercent = percent;
			
			break;
		case R.id.iv_edit_voice_soundtrack_no:
			CmmobiClickAgentWrapper.onEventEnd(mActivity, "my_edit_vi", "3");
			mActivity.goToPage(EditMediaDetailActivity.FRAGMENT_MAIN_VIEW, false);
			mActivity.mEffects.cleanEffects();
			soundtrackLayer.clearCheck();
			if (mActivity.trackId != 0) {
				trackBean = trackBeanList.get(mActivity.trackId - 1);
				mActivity.effectUtils.addMusic(mActivity.mEffects, "effectcfg/audio/addmusic.cfg", trackBean.filePath, 
						mActivity.mMediaPlayer.getChannels() , 
						mActivity.mMediaPlayer.getSampleRate(), 
						mActivity.mMediaPlayer.getBitsPerChannel(), 
						mActivity.longRecPercent, 
						1);
				soundtrackLayer.check(mActivity.trackId);
			}
			break;
		case R.id.ll_edit_media_original_sound:
			originalSoundImg.setBackgroundResource(R.drawable.yinxiao_xuanzhong);
			soundtrackLayer.clearCheck();
			mActivity.mEffects.cleanEffects();
			soundTrackText = getString(R.string.edit_media_original_sound);
			trackId = 0;
			break;
		default:
			if (view.getId() > 0 && view.getId() <= trackBeanList.size()) {
				percent = (double)volumeSeek.getProgress() / volumeSeek.getMax();
				Log.d(TAG,"onClick percent = " + percent);
				AudioSoundTrackBean trackBeanTemp = null;
				if (view.getTag() != null) {
					trackBeanTemp = (AudioSoundTrackBean) view.getTag();
				}
				if (mActivity.effectUtils != null && trackBeanTemp != null 
						&& EditMediaDetailActivity.isFileExists(trackBeanTemp.filePath)) {
					trackId = view.getId();
					trackBean = trackBeanTemp;
					soundTrackText = trackBean.trackName;
					mActivity.effectUtils.addMusic(mActivity.mEffects, "effectcfg/audio/addmusic.cfg", trackBeanTemp.filePath, 
						mActivity.mMediaPlayer.getChannels() , 
						mActivity.mMediaPlayer.getSampleRate(), 
						mActivity.mMediaPlayer.getBitsPerChannel(), 
						percent, 
						1);
					Log.d(TAG,"onclick use soundtrack");
				}
				originalSoundImg.setBackgroundResource(R.drawable.radio_transparent);
			}
			break;
		}
	}
	
	private void init() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				createFile("dianzi.mp3");
				createFile("diqu.mp3");
				createFile("dianzigangqin.mp3");
				createFile("shuqinggangqin.mp3");
			}
		}).start();
		String preffix = HomeActivity.SDCARD_PATH +Constant.SD_STORAGE_ROOT + "/" + "sound/";
		trackBeanList.clear();
		trackBeanList.add(new AudioSoundTrackBean(1, preffix + "diqu.mp3","迪曲"));
		trackBeanList.add(new AudioSoundTrackBean(2, preffix + "dianzi.mp3","电子"));
		trackBeanList.add(new AudioSoundTrackBean(3, preffix + "dianzigangqin.mp3","电子钢琴"));
		trackBeanList.add(new AudioSoundTrackBean(4, preffix + "shuqinggangqin.mp3","抒情钢琴"));
		int size = trackBeanList.size();
		soundtrackLayer.removeAllViews();
		
		for (int i = 0;i < size;i++) {
			AudioSoundTrackBean trackBeanTemp = trackBeanList.get(i);
			LinearLayout layout = (LinearLayout) ZLayoutInflater.inflate(R.layout.include_effects_soundtrack_buttons_stub);
			RadioButton radioButton = (RadioButton) layout.findViewById(R.id.radio_button);
			
			radioButton.setId(trackBeanTemp.trackID);
			radioButton.setText(trackBeanTemp.trackName);
			radioButton.setTag(trackBeanTemp);
			radioButton.setOnClickListener(this);
			radioButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			layout.removeAllViewsInLayout();
			soundtrackLayer.addView(radioButton);
		}
		trackId = mActivity.trackId;
		Log.d(TAG,"init -----trackId = " + trackId);
		if (trackId == 0) {
			soundtrackLayer.clearCheck();
		} else {
			soundtrackLayer.check(trackId);
			originalSoundImg.setBackgroundResource(R.drawable.radio_transparent);
		}
	}
	
	private void createFile(String fileName) {  
		String fileDirPath = HomeActivity.SDCARD_PATH +Constant.SD_STORAGE_ROOT + "/" + "sound/";
        String filePath = fileDirPath + "/" + fileName;// 文件路径 
        if (EditMediaDetailActivity.isFileExists(filePath)) {
        	return;
        }
        try {
            File dir = new File(fileDirPath);// 目录路径  
            if (!dir.exists()) {// 如果不存在，则创建路径名  
                System.out.println("要存储的目录不存在");  
                if (dir.mkdirs()) {// 创建该路径名，返回true则表示创建成功  
                    System.out.println("已经创建文件存储目录");  
                } else {  
                    System.out.println("创建目录失败");  
                }  
            }  
            // 目录存在，则将apk中raw中的需要的文档复制到该目录下  
            File file = new File(filePath);  
            if (!file.exists()) {// 文件不存在  
                System.out.println("要打开的文件不存在");  
                String filename = "effectcfg/audio/" + fileName;
                InputStream ins = mActivity.getAssets().open(filename);// 通过raw得到数据资源  
                System.out.println("开始读入");  
                FileOutputStream fos = new FileOutputStream(file);  
                System.out.println("开始写出");  
                byte[] buffer = new byte[8192];  
                int count = 0;// 循环写出  
                while ((count = ins.read(buffer)) > 0) {  
                    fos.write(buffer, 0, count);  
                }  
                System.out.println("已经创建该文件");  
                fos.close();// 关闭流  
                ins.close();  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
	
	private class AudioSoundTrackBean {
		public int trackID;
		public String filePath;
		public String trackName;
		
		public AudioSoundTrackBean(int id,String path,String name) {
			trackID = id;
			filePath = path;
			trackName = name;
		}
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
