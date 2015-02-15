package com.cmmobi.looklook.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.FriendAddActivity;
import com.cmmobi.looklook.activity.FriendsCircleContactsAttentionActivity;
import com.cmmobi.looklook.activity.FriendsCircleContactsBlickListActivity;
import com.cmmobi.looklook.activity.FriendsCircleContactsFunsActivity;
import com.cmmobi.looklook.activity.FriendsCircleSystemFunctionActivity;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.ContactManager;
import com.cmmobi.looklook.info.profile.FriendsFunsManager;

public class FriendsContactsFragment extends Fragment implements OnClickListener {

	private View contentView;
	private TextView attenCountTextView;
	private TextView funsCountTextView;
	private RelativeLayout blackListButton;
	private ImageView lastLine;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		contentView = inflater.inflate(
				R.layout.fragment_friends_circle_contacts, null);
		return contentView;
	}
	
	private void setAttentionAndFans() {
		String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		AccountInfo accountInfo = AccountInfo.getInstance(userID);
		ContactManager attentionContactManager = accountInfo.attentionContactManager;
		FriendsFunsManager friendsFunsManager = accountInfo.friendsFunsManager;
		
		if (null != attentionContactManager && null != attentionContactManager.getCache()) {
			int size = attentionContactManager.getCache().size();
			if (size > 0) {
				attenCountTextView.setText(size+"");
			}
		}
		if (null != friendsFunsManager && null != friendsFunsManager.getCache()) {
			int size = friendsFunsManager.getCache().size();
			if (size > 0) {
				funsCountTextView.setText(size+"");
			}
		}
	}

//	private void setAttentionAndFans() {
//		
//		String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
//		AccountInfo accountInfo = AccountInfo.getInstance(userID);
//		DiaryManager diaryManager = DiaryManager.getInstance();
//		if (diaryManager.getMyHomePageResponseList() != null
//				&& diaryManager.getMyHomePageResponseList().size() > 0) {
//			String fansCount = diaryManager.getMyHomePageResponseList().get(0).fanscount;
//			String attentioncount = diaryManager.getMyHomePageResponseList().get(0).attentioncount;
//			funsCountTextView.setText(fansCount);
//			attenCountTextView.setText(attentioncount);
//
//		}
//	}

	private void isShowBlackList(){
		
		String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		AccountInfo accountInfo = AccountInfo.getInstance(userID);
		ContactManager blacklistContactManager = accountInfo.blackListContactManager;

		ArrayList<WrapUser> wrapUserList = (ArrayList<WrapUser>) blacklistContactManager.getCache()
				.clone();
		
		if (null != wrapUserList && wrapUserList.size() > 0) {
			blackListButton.setVisibility(View.VISIBLE);
			lastLine.setVisibility(View.VISIBLE);
		} else {
			blackListButton.setVisibility(View.INVISIBLE);
			lastLine.setVisibility(View.INVISIBLE);
		}

	}
	
	@Override
	public void onResume() {
		setAttentionAndFans();
//		isShowBlackList();
		super.onResume();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		RelativeLayout friendAddButton = (RelativeLayout) contentView
				.findViewById(R.id.btn_friend_add);
		RelativeLayout systemFundationButton = (RelativeLayout) contentView
				.findViewById(R.id.btn_system_fundation);
		RelativeLayout attentionButton = (RelativeLayout) contentView
				.findViewById(R.id.btn_attention);
		RelativeLayout funsButton = (RelativeLayout) contentView
				.findViewById(R.id.btn_funs);
		blackListButton = (RelativeLayout) contentView
				.findViewById(R.id.btn_black_list);
		
		lastLine = (ImageView) contentView.findViewById(R.id.last_line); 
		
		attenCountTextView = (TextView) contentView.findViewById(R.id.atten_count);
		
		funsCountTextView = (TextView) contentView.findViewById(R.id.funs_count);

		friendAddButton.setOnClickListener(this);
		systemFundationButton.setOnClickListener(this);
		attentionButton.setOnClickListener(this);
		funsButton.setOnClickListener(this);
		blackListButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		Intent intent;
		switch (v.getId()) {
		case R.id.btn_friend_add:
			intent = new Intent(this.getActivity(), FriendAddActivity.class);

			startActivity(intent);
			break;
		case R.id.btn_system_fundation:
			
			intent = new Intent(this.getActivity(),
					FriendsCircleSystemFunctionActivity.class);

			startActivity(intent);
			break;
		case R.id.btn_attention:
			intent = new Intent(this.getActivity(),
					FriendsCircleContactsAttentionActivity.class);

			startActivity(intent);
			break;
		case R.id.btn_funs:
			intent = new Intent(this.getActivity(),
					FriendsCircleContactsFunsActivity.class);

			startActivity(intent);
			break;
		case R.id.btn_black_list:
			intent = new Intent(this.getActivity(),
					FriendsCircleContactsBlickListActivity.class);

			startActivity(intent);
			break;

		default:
			break;
		}
	}
}
