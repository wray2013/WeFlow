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
import com.cmmobi.looklook.activity.EditPhotoDetailActivity;
import com.cmmobi.looklook.common.utils.EffectsDownloadUtil;
import com.cmmobivideo.utils.PluginUtils;

import effect.XEffects;

public class EditPhotoMainFragment extends Fragment implements OnClickListener{

	private RelativeLayout cropLayout = null;
	private RelativeLayout effectLayout = null;
	private static final String TAG = "EditPhotoMainFragment";
	private EditPhotoDetailActivity mActivity;
	private LinearLayout editDetailLayout = null;
	private TextView tvEffect = null;
	private String effectText = "";
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_edit_photo_main, container, false);
		cropLayout = (RelativeLayout) view.findViewById(R.id.rl_edit_photo_crop);
		effectLayout = (RelativeLayout) view.findViewById(R.id.rl_edit_photo_effect);
		editDetailLayout = (LinearLayout) view.findViewById(R.id.ll_edit_photo_detail);
		cropLayout.setOnClickListener(this);
		editDetailLayout.setOnClickListener(this);
		effectLayout.setOnClickListener(this);
		
		tvEffect = (TextView) view.findViewById(R.id.tv_edit_photo_effect);
        
		if (!"".equals(effectText)) {
			tvEffect.setText(effectText);
		}
		
		mActivity.mainLayout.setVisibility(View.VISIBLE);
		mActivity.detailMainLayout.setVisibility(View.GONE);
		return view;
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.rl_edit_photo_crop:
			mActivity.goToPage(EditPhotoDetailActivity.FRAGMENT_CROP_VIEW, false);
			break;
		case R.id.ll_edit_photo_detail:
			mActivity.goToPage(EditPhotoDetailActivity.FRAGMENT_DETAIL_MAIN_VIEW, false);
			break;
		case R.id.rl_edit_photo_effect:
//			Toast.makeText(mActivity, "特效库目前还无法使用", 2000).show();
			if (!EffectsDownloadUtil.getInstance(mActivity).checkEffects()) {
				if (mActivity.mEffects == null) {
					mActivity.mEffects = new XEffects();
				}
				mActivity.goToPage(EditPhotoDetailActivity.FRAGMENT_EFFECT_VIEW, false);
			} 
			break;
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Log.e(TAG, "NearByFragment - onAttach");

		try {
			mActivity = (EditPhotoDetailActivity) activity;
			// onGotoPageListener = (OnGotoPageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnGotoPageListener");
		}
	}
	
	public void setEffectText(String text) {
		effectText = text;
		tvEffect.setText(effectText);
	}

}
