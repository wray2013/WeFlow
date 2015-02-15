package com.cmmobi.looklook.common.listview.pulltorefresh;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.RenrenFriendWeiboActivity;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWUser;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.ActiveAccount;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-6-21
 */
public class RenrenFriendsListView extends AbsRefreshView<List<RWUser>> {

	private static final String TAG="RenrenFriendsListView";
	
	public RenrenFriendsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RenrenFriendsListView(Context context) {
		super(context);
		init();
	}
	
	LinearLayout root;
	private void init(){
		root=(LinearLayout) inflater.inflate(R.layout.activity_singlelistview, null);
		addChild(root,1);
	}

	@Override
	protected void initContent(List<RWUser> items) {
		root.removeAllViews();
		addView(items);
		
	}
	
	private List<String> to_invite;
	private Handler handler;
	public void setParams(Handler handler,List<String> list_to_invite){
		this.to_invite=list_to_invite;
		this.handler=handler;
	}

	@Override
	protected void addView(List<RWUser> items) {
		if(null==items||0==items.size()){
			Log.e(TAG, "items is null");
			return;
		}
		
		Log.d(TAG, "items="+items.size());
		for(int i=0;i<items.size();i++){
			final RWUser item=items.get(i);
			View v=inflater.inflate(R.layout.row_list_friend,null);
			WebImageView head = (WebImageView) v.findViewById(R.id.wiv_row_list_friend_head);
			TextView nick = (TextView) v.findViewById(R.id.tv_row_list_friend_nick);
			CheckBox choose = (CheckBox) v.findViewById(R.id.ck_row_list_friend_choose);
			if(item.avatar!=null && item.avatar.length>0 && item.avatar[0]!=null &&  item.avatar[0].url!=null){
				head.setImageUrl(R.drawable.zone_1, ActiveAccount.getInstance(context).getUID(), 1, item.avatar[0].url, true);
			}
			
			nick.setText(item.name);
			
			choose.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					CheckBox cb = (CheckBox)v;  
					if(cb.isChecked()){
						to_invite.add(item.name);
						if(to_invite.size()==1){
							Message msg = handler.obtainMessage(RenrenFriendWeiboActivity.HANDLER_FLAG_ENABLE_INVITE);
							msg.sendToTarget();
						}
					}else{
						to_invite.remove(item.name) ;
						if(to_invite.size()==0){
							Message msg = handler.obtainMessage(RenrenFriendWeiboActivity.HANDLER_FLAG_DISABLE_INVITE);
							msg.sendToTarget();
						}
					}
					 
				}
			});
			root.addView(v);
		}
	}

}
