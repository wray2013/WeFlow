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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.EditPhotoDetailActivity;
import com.cmmobi.looklook.common.adapter.VideoEffectAdapter;
import com.cmmobivideo.utils.EffectBean;
import com.tencent.mm.sdk.platformtools.PhoneUtil.MacInfo;

public class EditPhotoEffectFragment extends Fragment implements OnClickListener{

	private GridView gridView = null;
	private static final String TAG = "EditPhotoEffectFragment";
	private EditPhotoDetailActivity mActivity;
	private ImageView preSltView = null;
	private VideoEffectAdapter mAdapter;
	
	private ImageView confirmBtn = null;
	private ImageView cancleBtn = null;
	private int effectId = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_edit_video_effect, container, false);
		gridView = (GridView) view.findViewById(R.id.gv_edit_video_effect);
		mAdapter = new VideoEffectAdapter(mActivity);
		effectId = mActivity.effectId;
		mAdapter.lastPosition = effectId;
		gridView.setAdapter(mAdapter);
		
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if (position == mAdapter.lastPosition) {
					return;
				}
				
				if (preSltView != null) {
					preSltView.setVisibility(View.GONE);
				}
				ImageView selectImg = (ImageView) view.findViewById(R.id.iv_effect_selected);
				preSltView = selectImg;
				selectImg.setVisibility(View.VISIBLE);
//				mAdapter.hideLastSelected();
				if (preSltView != mAdapter.selectImage) {// 由于gridview的重用机制，两次点击虽然不是同一个position，但可能重用一个view
					mAdapter.hideLastSelected();
				}
				mAdapter.lastPosition = position;
				mAdapter.selectImage = selectImg;
//				mAdapter.notifyDataSetChanged();
//				mActivity.switchEffect(position);
				if(mActivity.effectBitmap != null){
					mActivity.effectBitmap.recycle();
					mActivity.effectBitmap = null;
				}
				Bitmap bmp = mActivity.switchEffect(position, mActivity.originBitmap);
				mActivity.effectBitmap = bmp;
				if(bmp == null){
					bmp = mActivity.originBitmap;
				}
				mActivity.getPreView().setImageBitmap(bmp);
				mActivity.getThumbView().setImageBitmap(bmp);
				effectId = position;
			}
		});
		
		confirmBtn = (ImageView) view.findViewById(R.id.iv_edit_video_effect_yes);
        cancleBtn = (ImageView) view.findViewById(R.id.iv_edit_video_effect_no);
        confirmBtn.setOnClickListener(this);
        cancleBtn.setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()) {
		case R.id.iv_edit_video_effect_yes:
			if (mActivity.effectBeans != null && mActivity.effectBeans.size() > effectId) {
				mActivity.effectId = effectId;
				EffectBean bean = mActivity.effectBeans.get(effectId);
				if(bean != null) {
					mActivity.setEffectText(bean.getZHName());
				}
				if (effectId != 0) {
					mActivity.isMainAttachChanged = true;
					mActivity.isAddEffect = true;
				} else {
					mActivity.isAddEffect = false;
				}
			}
			mActivity.goToPage(EditPhotoDetailActivity.FRAGMENT_MAIN_VIEW, false);
			break;
		case R.id.iv_edit_video_effect_no:
//			mActivity.switchEffect(mActivity.effectId);
			Bitmap bmp = mActivity.switchEffect(mActivity.effectId, mActivity.originBitmap);
			if(bmp == null){
				bmp = mActivity.originBitmap;
			}
			mActivity.getPreView().setImageBitmap(bmp);
			mActivity.getThumbView().setImageBitmap(bmp);
			mActivity.goToPage(EditPhotoDetailActivity.FRAGMENT_MAIN_VIEW, false);
			break;
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Log.e(TAG, "EditVideoEffectFragment - onAttach");

		try {
			mActivity = (EditPhotoDetailActivity) activity;
			// onGotoPageListener = (OnGotoPageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnGotoPageListener");
		}
	}
}
