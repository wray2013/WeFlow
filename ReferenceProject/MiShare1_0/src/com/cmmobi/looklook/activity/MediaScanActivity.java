package com.cmmobi.looklook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.fragment.FragmentAudioList;
import com.cmmobi.looklook.fragment.FragmentPictureList;
import com.cmmobi.looklook.fragment.VShareFragment;

/**
 * Copyright (C) ......
 * All rights reserved.
 * @package com.cmmobi.looklook.activity
 * @filename MediaScanActivity.java
 * @summary 导入界面
 * @author Lanhai
 * @date 2013-11-18
 * @version 1.0
 */
public class MediaScanActivity extends FragmentActivity implements OnClickListener, Callback, OnLongClickListener
{
	private final String TAG = "MediaScanActivity";
	
	private final int HANLDER_CREATE_DIARY = 0;
	
	public static String INTENT_SCAN_MODE = "intent_scan_mode";
	
	public static final int MODE_PIC_NORMAL = 0;
	public static final int MODE_PIC_SHARE = 1;
	public static final int MODE_AUDIO_SHARE = 2;
	
	public int mode = MODE_PIC_NORMAL;
	
	private FragmentPictureList mFragPicture = null;
	private FragmentAudioList mFragAudio = null;
	
	private ImageButton mBtnBack = null;
	private TextView mTVTitle = null;
	private Button mBtnNext = null;
	
	private Handler mHandler = null;
	public boolean isFromVshare = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Log.v(TAG, "oncreate");
		setContentView(R.layout.activity_mediascan);
		
		setContent();
		
		setListener();
		
		mFragPicture = new FragmentPictureList();
		mFragAudio = new FragmentAudioList();
		
		mode = getIntent().getIntExtra(INTENT_SCAN_MODE, MODE_PIC_NORMAL);
		
		mHandler = new Handler(this);
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

//		ft.setCustomAnimations(android.R.animator.fade_in,  
//                android.R.animator.fade_out);
		if(mode == MODE_PIC_NORMAL || mode == MODE_PIC_SHARE)
		{
			ft.add(R.id.ll_media_file, mFragPicture, FragmentPictureList.class.toString());
			mTVTitle.setText("本地相册");
		}
		else
		{
			ft.add(R.id.ll_media_file, mFragAudio, FragmentAudioList.class.toString());
			mTVTitle.setText("本地音乐");
		}
		
		isFromVshare = getIntent().getBooleanExtra(VShareFragment.IS_FROM_VSHARE, false);
		
//		ft.hide(mFragAudio);
//		ft.show(mFragPicture);
		ft.commit();
	}
	
	private void setContent()
	{
		mBtnBack = (ImageButton) findViewById(R.id.ib_title_back);
		mTVTitle = (TextView) findViewById(R.id.tv_title_text);
		mBtnNext = (Button) findViewById(R.id.btn_next);
		mBtnNext.setEnabled(false);
	}
	
	private void setListener()
	{
		mBtnBack.setOnClickListener(this);
		mBtnBack.setOnLongClickListener(this);
		mBtnNext.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v)
	{
		if (isFastDoubleClick())
		{
			switch (v.getId())
			{
			case R.id.ib_title_back:
				if(mode == MODE_PIC_NORMAL || mode == MODE_PIC_SHARE)
				{
					FragmentManager fm = getSupportFragmentManager();
					FragmentPictureList frag = (FragmentPictureList) fm.findFragmentByTag(FragmentPictureList.class.toString());
					if (frag != null)
					{
						frag.back();
					}
				}
				finish();
				break;
			case R.id.btn_next:
				ZDialog.show(R.layout.progressdialog, false, true, this, true);
				Log.v(TAG, "btn_next");
				mHandler.sendEmptyMessageDelayed(HANLDER_CREATE_DIARY, 200);
				break;
			default :
				break;
			}
		}
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		System.gc();
		super.onStop();
	}
	
	@Override
	public boolean onLongClick(View v)
	{
		switch (v.getId())
		{
		case R.id.ib_title_back:
			if(mode == MODE_PIC_NORMAL || mode == MODE_PIC_SHARE)
			{
				FragmentManager fm = getSupportFragmentManager();
				FragmentPictureList frag = (FragmentPictureList) fm.findFragmentByTag(FragmentPictureList.class.toString());
				if (frag != null)
				{
					frag.back();
				}
			}
			startActivity(new Intent(this,LookLookActivity.class));
			finish();
			break;
		default:
			break;
		}
		return false;
	}
	
	public void changeTitle(String text)
	{
		if(text != null)
		{
			mTVTitle.setText(text);
//			mBtnNext.setVisibility(View.VISIBLE);
			mBtnNext.setEnabled(true);
		}
		else
		{
//			mBtnNext.setVisibility(View.INVISIBLE);
			mBtnNext.setEnabled(false);
			if(mode == MODE_PIC_NORMAL || mode == MODE_PIC_SHARE)
			{
				mTVTitle.setText("本地相册");
			}
			else
			{
				mTVTitle.setText("本地音乐");
			}
		}
	}

	// 创建日记
	private void createDiary()
	{
		FragmentManager fm = getSupportFragmentManager();
		if(mode == MODE_PIC_NORMAL || mode == MODE_PIC_SHARE)
		{
			FragmentPictureList frag = (FragmentPictureList) fm.findFragmentByTag(FragmentPictureList.class.toString());
			if (frag != null)
			{
				frag.createDiary();
			}
		}
		else
		{
			FragmentAudioList frag = (FragmentAudioList) fm.findFragmentByTag(FragmentAudioList.class.toString());
			if (frag != null)
			{
				frag.createDiary();
			}
		}
	}

	@Override
	public boolean handleMessage(Message msg)
	{
		switch(msg.what)
		{
		case HANLDER_CREATE_DIARY:
			createDiary();
			break;
		}
		return false;
	}

	private static long lastClickTime;

	public static boolean isFastDoubleClick()
	{
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800)
		{
			return false;
		}
		lastClickTime = time;
		return true;
	}
	
}
