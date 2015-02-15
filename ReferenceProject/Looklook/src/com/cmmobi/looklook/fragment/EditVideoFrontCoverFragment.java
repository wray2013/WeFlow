package com.cmmobi.looklook.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.EditVideoDetailActivity;
import com.cmmobi.looklook.common.adapter.ImageAdapter;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.view.VideoFrontCoverView;


public class EditVideoFrontCoverFragment extends Fragment implements  OnClickListener, Callback{

	private EditVideoDetailActivity mActivity;
	private final String TAG = this.getClass().getSimpleName();
	
	ImageAdapter adapter = null;
	private GridView gridview = null;
	private VideoFrontCoverView frontCoverView = null;
	private ImageView confirmBtn = null;
	private ImageView cancleBtn = null;
	public double totalTime = 263;
	long duration = 0;// 单位为s
	public final static int HANDLER_UPDATE_FRONT_COVER = 0xf8;
	
	private Handler handler = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_edit_video_frontcover, container, false);
		CmmobiClickAgentWrapper.onEvent(mActivity, "my_edit_vi", "4");
		CmmobiClickAgentWrapper.onEventBegin(mActivity, "my_edit_vi", "4");
		gridview = (GridView) view.findViewById(R.id.gv_edit_video_frontcover_imgs);
        adapter = new ImageAdapter(mActivity);
        gridview.setAdapter(adapter);
        handler = new Handler(this);
        
        frontCoverView = (VideoFrontCoverView) view.findViewById(R.id.vfv_edit_video_frontcover);
        frontCoverView.setHandler(handler);
        
        confirmBtn = (ImageView) view.findViewById(R.id.iv_edit_video_frontcover_yes);
        cancleBtn = (ImageView) view.findViewById(R.id.iv_edit_video_frontcover_no);
        confirmBtn.setOnClickListener(this);
        cancleBtn.setOnClickListener(this);
        
        mActivity.generateThumbsCovers(adapter);
        totalTime = mActivity.totalTime;
        duration = mActivity.duration;
        
        
		return view;
	}
	
	public void setThumbsBitmaps(Bitmap[] bmps) {
		adapter.setBitmaps(bmps);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.iv_edit_video_frontcover_yes:
			mActivity.setFrontCover();
			CmmobiClickAgentWrapper.onEventEnd(mActivity, "my_edit_vi", "4");
			mActivity.goToPage(EditVideoDetailActivity.FRAGMENT_MAIN_VIEW, false);
			break;
		case R.id.iv_edit_video_frontcover_no:
			CmmobiClickAgentWrapper.onEventEnd(mActivity, "my_edit_vi", "4");
			mActivity.goToPage(EditVideoDetailActivity.FRAGMENT_MAIN_VIEW, false);
			break;
		}
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
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case HANDLER_UPDATE_FRONT_COVER:
			
			double curTime = ((double) msg.arg1) / msg.arg2 * totalTime ;
			Log.d(TAG,"curTime = " + curTime + " arg1 = " + msg.arg1 + " arg2 = " + msg.arg2 + " totalTime = " + totalTime);
			mActivity.previewFrontCover(curTime);
			break;
		}
		return false;
	}

}
