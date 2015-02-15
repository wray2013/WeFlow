package com.cmmobi.looklook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.PhotoClipUtil;

/**
 * 
 * @author wangjm
 * @email wangjm@cmmobi.com
 */

public class PhotoClipActivity extends ZActivity implements OnLongClickListener{
	
	private final String TAG = "PhotoClipActivity";
	private String picPath = "";
//	private String picPath = "";
	private ImageView backBtn = null;
	private TextView doneBtn = null;
	private ImageView photoView = null;
	private ImageView clipView = null;
	private PhotoClipUtil photoClipUtil = null;
	private float widthHeightScale = 1.0f;
	public final static int PHOTO_CLIP_REQUEST = 0x0017;
	public final static String PHOTO_PATH = "photopath";
	public final static String IS_FROM_IMPORT = "isfromimport";
	public final static String IS_FROM_SPACE_COVER = "isfromspacecover";
	public final static String WIDTH_HEIGHT_SCALE = "scale";
	public final static String CLIP_PATH = "clippath";
	private boolean isFromSpaceCover = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clip_photo);
		
		picPath = getIntent().getStringExtra(PHOTO_PATH);
		
		widthHeightScale = getIntent().getFloatExtra(WIDTH_HEIGHT_SCALE, 1.0f);
		
		backBtn = (ImageView) findViewById(R.id.iv_back);
		doneBtn = (TextView) findViewById(R.id.tv_save);
		backBtn.setOnClickListener(this);
		backBtn.setOnLongClickListener(this);
		doneBtn.setOnClickListener(this);
		
		boolean isFromImport = getIntent().getBooleanExtra(IS_FROM_IMPORT, false);
		if (isFromImport) {
			doneBtn.setText(R.string.next_step);
		}
		isFromSpaceCover = getIntent().getBooleanExtra(IS_FROM_SPACE_COVER, false);
		
		photoView = (ImageView) findViewById(R.id.iv_pic);
		clipView = (ImageView) findViewById(R.id.clip_view);
		doneBtn.setEnabled(false);
		
		photoClipUtil = new PhotoClipUtil(this, photoView, clipView, picPath,widthHeightScale);
		photoClipUtil.setDoneBtn(doneBtn);
	}
	
	@Override
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.tv_save:
			String outPath = "";
			if(isFromSpaceCover) {
				outPath = photoClipUtil.saveClipImage(true);
			} else {
				outPath = photoClipUtil.saveClipImage();
			}
			if (outPath != null) {
				Intent intent = new Intent();
				intent.putExtra(CLIP_PATH, outPath);
				setResult(RESULT_OK,intent);
			}
			finish();
			break;
		}
	}


	@Override
	public boolean onLongClick(View v)
	{
		switch(v.getId())
		{
		case R.id.iv_back:
			Intent intent = new Intent(this,LookLookActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			break;
		}
		return false;
	}
}
