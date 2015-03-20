package com.etoc.weflow.fragment;

import com.etoc.weflow.R;
import com.etoc.weflow.event.GameCoinEvent;
import com.etoc.weflow.net.Requester;

import de.greenrobot.event.EventBus;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GameRechargeFragment extends Fragment implements Callback {
	
	private Handler handler = null;
	private View mView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		handler = new Handler(this);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	public void onEvent(GameCoinEvent event) {
		if (event == GameCoinEvent.GAMERECHARGE) {
			
		}
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
		
		View view = inflater.inflate(R.layout.fragment_game_recharge, null);
		mView = view;
		initView(view);
		return view;
	}
	
	private void initView(View view) {
		
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

}
