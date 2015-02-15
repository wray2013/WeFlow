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
import com.cmmobi.looklook.activity.TencentFriendWeiboActivity;
import com.cmmobi.looklook.common.gson.WeiboResponse.tencentInfo;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.ActiveAccount;

/**
 * @author wuxiang
 * @author zhangwei
 * @email wuxiang@cmmobi.com
 * @date  2013-6-21
 */
public class TencentFriendsListView extends AbsRefreshView<List<tencentInfo>> {

	private static final String TAG="SinaFriendsListView";
	
	public TencentFriendsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TencentFriendsListView(Context context) {
		super(context);
		init();
	}
	
	LinearLayout root;
	private void init(){
		root=(LinearLayout) inflater.inflate(R.layout.activity_singlelistview, null);
		addChild(root,1);
	}

	@Override
	protected void initContent(List<tencentInfo> items) {
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
	protected void addView(List<tencentInfo> items) {
		if(null==items||0==items.size()){
			Log.e(TAG, "items is null");
			return;
		}
		
		Log.d(TAG, "items="+items.size());
		for(int i=0;i<items.size();i++){
			final tencentInfo item=items.get(i);
			View v=inflater.inflate(R.layout.row_list_friend,null);
			WebImageView head = (WebImageView) v.findViewById(R.id.wiv_row_list_friend_head);
			TextView nick = (TextView) v.findViewById(R.id.tv_row_list_friend_nick);
			CheckBox choose = (CheckBox) v.findViewById(R.id.ck_row_list_friend_choose);
			if(item!=null && item.headurl!=null){
				head.setImageUrl(R.drawable.zone_1, ActiveAccount.getInstance(context).getUID(), 1, item.headurl + "/50", true);
			}
			
			nick.setText(item.nick);
			
			choose.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					CheckBox cb = (CheckBox)v;  
					if(cb.isChecked()){
						to_invite.add(item.name);
						if(to_invite.size()==1){
							Message msg = handler.obtainMessage(TencentFriendWeiboActivity.HANDLER_FLAG_ENABLE_INVITE);
							msg.sendToTarget();
						}
					}else{
						to_invite.remove(item.name) ;
						if(to_invite.size()==0){
							Message msg = handler.obtainMessage(TencentFriendWeiboActivity.HANDLER_FLAG_DISABLE_INVITE);
							msg.sendToTarget();
						}
					}
					 
				}
			});
			root.addView(v);
		}
	}

}
