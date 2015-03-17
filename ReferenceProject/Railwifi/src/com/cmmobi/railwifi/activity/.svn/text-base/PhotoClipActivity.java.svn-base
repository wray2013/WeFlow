package com.cmmobi.railwifi.activity;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.utils.PhotoClipUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author wangjm
 * @email wangjm@cmmobi.com
 */

public class PhotoClipActivity extends BaseActivity implements OnClickListener {
	
	private final String TAG = "PhotoClipActivity";
	private String picPath = "";
//	private String picPath = "";
	private TextView backBtn = null;
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
		
		picPath = getIntent().getStringExtra(PHOTO_PATH);
		
		widthHeightScale = getIntent().getFloatExtra(WIDTH_HEIGHT_SCALE, 1.0f);
		
		backBtn = (TextView) findViewById(R.id.tv_back);
		doneBtn = (TextView) findViewById(R.id.tv_done);
		backBtn.setOnClickListener(this);
		doneBtn.setOnClickListener(this);
		
		isFromSpaceCover = getIntent().getBooleanExtra(IS_FROM_SPACE_COVER, false);
		
		photoView = (ImageView) findViewById(R.id.iv_pic);
		clipView = (ImageView) findViewById(R.id.clip_view);
		doneBtn.setEnabled(false);
		
		photoClipUtil = new PhotoClipUtil(this, photoView, clipView, picPath,widthHeightScale);
		photoClipUtil.setDoneBtn(doneBtn);
	}
	

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()) {
		case R.id.tv_back:
			finish();
			break;
		case R.id.tv_done:
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
	public int rootViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_clip_photo;
	}

}
