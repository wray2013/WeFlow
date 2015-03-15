package com.etoc.weflow.fragment;

import com.etoc.weflow.R;
import com.etoc.weflow.activity.ScratchCardActivity;
import com.etoc.weflow.activity.ShakeShakeActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PlayGameFragment extends Fragment implements OnClickListener {
	private View mView;
	
	private ImageView ivShake, ivScratchCard;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(mView != null){
		    ViewGroup parent = (ViewGroup) mView.getParent();  
		    if (parent != null) {  
		        parent.removeView(mView);  
		    }   
		    return mView;
		}
		super.onCreateView(inflater, container, savedInstanceState);
		
		View v = inflater.inflate(R.layout.fragment_play_game, null);
		mView = v;
		initView(v);
		return v;
	}


	private void initView(View v) {
		// TODO Auto-generated method stub
		ivShake = (ImageView) v.findViewById(R.id.iv_game_shake);
		ivScratchCard = (ImageView) v.findViewById(R.id.iv_scratch_card);
		
		ivShake.setOnClickListener(this);
		ivScratchCard.setOnClickListener(this);
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.iv_game_shake:
			startActivity(new Intent(getActivity(), ShakeShakeActivity.class));
			break;
		case R.id.iv_scratch_card:
			startActivity(new Intent(getActivity(), ScratchCardActivity.class));
			break;
		}
	}
	
	
}
