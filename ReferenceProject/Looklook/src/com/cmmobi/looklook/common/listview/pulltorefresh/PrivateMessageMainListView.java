package com.cmmobi.looklook.common.listview.pulltorefresh;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.FriendsMessageAdapter;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.MessageWrapper;

/**
 * @author zhangwei
 * @date  2013-6-26
 */
public class PrivateMessageMainListView extends AbsRefreshView<List<MessageWrapper>> {

	private static final String TAG="PrivateMessageListView";
	
	private Context context;
	public Handler handler;
	
	public PrivateMessageMainListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PrivateMessageMainListView(Context context) {
		super(context);
		init(context);
	}
	
	LinearLayout root;
	
	public void setParams(Handler handler){
		this.handler=handler;
	}
	
	private void init(Context context){
		this.context = context;
		root=(LinearLayout) inflater.inflate(R.layout.activity_singlelistview, null);
		addChild(root,1);
	}

	@Override
	protected void initContent(List<MessageWrapper> items) {
		root.removeAllViews();
		addView(items);
		
	}
	

	@Override
	protected void addView(List<MessageWrapper> items) {
		
		View v;
		WebImageView imageView;
		TextView text_nickname;
		TextView text_content;

		TextView strangerTextView;
		ImageView arrowImageView;
		TextView timeTextView;
		LinearLayout textLinearLayout;
		
		//stranger:
		v = inflater.inflate(R.layout.list_item_friends_message,null);
		
		imageView = (WebImageView) v.findViewById(R.id.image);
		
		text_nickname = (TextView) v.findViewById(R.id.nickname);
		text_content = (TextView) v.findViewById(R.id.content);
		
		strangerTextView = (TextView) v.findViewById(R.id.stranger);
		arrowImageView = (ImageView) v.findViewById(R.id.arrow);
		
		timeTextView = (TextView) v.findViewById(R.id.time);
		textLinearLayout= (LinearLayout) v.findViewById(R.id.text_ll);
		
		imageView.setImageResource(R.drawable.moshengren);
		strangerTextView.setVisibility(View.VISIBLE);
		arrowImageView.setVisibility(View.VISIBLE);
		timeTextView.setVisibility(View.GONE);
		textLinearLayout.setVisibility(View.GONE);
		
		root.addView(v);
		
		ImageView div = new ImageView(context);
		div.setBackgroundResource(R.drawable.friends_list_line);
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 3);
		root.addView(div,params);
		
		v.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "stranger click", Toast.LENGTH_SHORT).show();

				handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_STRANGER).sendToTarget();
			}
		});
		
		
		if(null==items||0==items.size()){
			Log.e(TAG, "items is null");
			return;
		}
		
		//other:
		Log.d(TAG, "items="+items.size());
		for(int i=0; i<items.size(); i++){
			final MessageWrapper item=items.get(i);
			v = inflater.inflate(R.layout.list_item_friends_message,null);
			
			imageView = (WebImageView) v.findViewById(R.id.image);
			
			text_nickname = (TextView) v.findViewById(R.id.nickname);
			text_content = (TextView) v.findViewById(R.id.content);
			
			strangerTextView = (TextView) v.findViewById(R.id.stranger);
			arrowImageView = (ImageView) v.findViewById(R.id.arrow);
			
			timeTextView = (TextView) v.findViewById(R.id.time);
			textLinearLayout= (LinearLayout) v.findViewById(R.id.text_ll);
			
			strangerTextView.setVisibility(View.GONE);
			arrowImageView.setVisibility(View.GONE);
			timeTextView.setVisibility(View.VISIBLE);
			textLinearLayout.setVisibility(View.VISIBLE);
			
			//imageView.setImageUrl(R.drawable.temp_local_icon, 4, item.headimageurl);
			//imageView.setPortraiUrl(R.drawable.temp_local_icon, 4, item.headimageurl);
			imageView.setImageUrl(R.drawable.moren_touxiang, 4, item.headimageurl, true);

			text_nickname.setText(item.nickname);
			text_content.setText(item.content);
			
			timeTextView.setText(DateUtils.getMyCommonShowDate(new Date(item.lastTimeMill)));
			
			v.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Toast.makeText(context, "item click:" + item.userid, Toast.LENGTH_SHORT).show();
					handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_USER, item.userid).sendToTarget();
				}
			});

			root.addView(v);
			
			div = new ImageView(context);
			div.setBackgroundResource(R.drawable.friends_list_line);
			root.addView(div,params);
		}
	}
	
}
