package com.cmmobi.looklook.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.EditMediaDetailActivity;
import com.cmmobi.looklook.activity.EditPhotoDetailActivity;
import com.cmmobi.looklook.common.view.EditExpressionView;

public class EditMediaTextInputFragment extends Fragment implements OnClickListener, OnItemClickListener{

	private EditMediaDetailActivity mActivity = null;
	private ImageView textEditCancle = null;
	private EditExpressionView expressionView;
	private ImageView expressionBtn = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_edit_media_text_input, container, false);
		
		textEditCancle = (ImageView) view.findViewById(R.id.iv_edit_media_text_input_no);
		expressionBtn = (ImageView) view.findViewById(R.id.iv_edit_media_facial_expression);
		
		textEditCancle.setOnClickListener(this);
		expressionBtn.setOnClickListener(this);
		
		expressionView = new EditExpressionView(mActivity,view,mActivity.edit);
		expressionView.setOnclickListener(this);
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			mActivity = (EditMediaDetailActivity) activity;
			// onGotoPageListener = (OnGotoPageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnGotoPageListener");
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.iv_edit_media_text_input_no:
//			expressionView.showSoftKeyboard();
			mActivity.hideSoftKeyboard();
			mActivity.goToPage(mActivity.lastFragmentType, false);
			break;
		case R.id.iv_edit_media_facial_expression:
			View view = expressionView.getExpressionView();
			if (View.GONE != view.getVisibility()) {
//				expressionView.showSoftKeyboard();
				expressionView.hideExpressionView();
				//expressionView.show(false);
				expressionBtn.setImageResource(R.drawable.message_biaoqing);
			} else {
				expressionView.showExpressionView();
				//expressionView.show(true);
				expressionBtn.setImageResource(R.drawable.jianpan);
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		mActivity.edit.append(arg1.getTag().toString());
	}

}
