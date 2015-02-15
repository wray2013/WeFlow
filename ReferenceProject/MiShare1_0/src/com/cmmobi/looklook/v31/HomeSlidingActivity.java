package com.cmmobi.looklook.v31;
import android.app.ActivityGroup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.activity_del.FriendsActivity;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-10-21
 */
@SuppressWarnings("deprecation")
public class HomeSlidingActivity extends ActivityGroup implements OnItemClickListener,OnClickListener {
	
	private static final String TAG=HomeSlidingActivity.class.getSimpleName();
	private SlidingMenuView slidingMenuView;
	private ViewGroup tabcontent;
	private GridView gv;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.del_activity_home_sliding);
        slidingMenuView = (SlidingMenuView) findViewById(R.id.sliding_menu_view);
        slidingMenuView.findViewById(R.id.ll_go_home).setOnClickListener(this);
        slidingMenuView.findViewById(R.id.rl_enterTask).setOnClickListener(this);
        tabcontent = (ViewGroup) slidingMenuView.findViewById(R.id.sliding_body);
        gv=(GridView)findViewById(R.id.feature_list);
        gv.setAdapter(new GridViewAdapter(this.getApplicationContext()));
        gv.setOnItemClickListener(this);
        changeMyZoneActivity();
        
        IntentFilter filter=new IntentFilter();
        filter.addAction(ACTION_HIDE_MENU);
        filter.addAction(ACTION_SHOW_MENU);
        registerReceiver(myBroadcastReceiver, filter);
    }
    
    @Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		switch (position) {
		case 0://通讯录
			Log.d(TAG, "通讯录");
			startActivity(new Intent(this, FriendsActivity.class));
			break;
		case 1://评论
			Log.d(TAG, "评论");
			startActivity(new Intent(this, PlayerActivity.class));
			break;
		case 2://收藏
			Log.d(TAG, "收藏");
			break;
		case 3://分享记录
			Log.d(TAG, "分享记录");
			break;
		case 4://邀请好友
			Log.d(TAG, "邀请好友");
			break;
		case 5://活动
			Log.d(TAG, "活动");
			break;
		case 6://设置
			Log.d(TAG, "设置");
			break;
		case 7://保险箱
			Log.d(TAG, "保险箱");
			changeSafeboxActivity();
			break;

		default:
			break;
		}
		hideMenu();
	}

    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_go_home:
			changeMyZoneActivity();
			hideMenu();
			break;
		case R.id.rl_enterTask:
			Log.d(TAG, "进入任务页");
			hideMenu();
			break;
		default:
			break;
		}
	}

	//隐藏滑动侧边栏
	private void hideMenu(){
    	slidingMenuView.snapToScreen(0);
    }
    
	//显示滑动侧边栏
    private void showMenu(){
    	slidingMenuView.snapToScreen(2);
    }
    
    //显示我的空间页
    private void changeMyZoneActivity(){
    	Intent i = new Intent(this,LookLookActivity.class);
    	View v = getLocalActivityManager().startActivity(LookLookActivity.class.getName(), i).getDecorView();
		tabcontent.removeAllViews();
		tabcontent.addView(v);
    }
    
    //显示保险箱页
    private void changeSafeboxActivity(){
    	Intent i = new Intent(this,SafeboxActivity.class);
    	View v = getLocalActivityManager().startActivity(SafeboxActivity.class.getName(), i).getDecorView();
		tabcontent.removeAllViews();
		tabcontent.addView(v);
    }

	@Override
	protected void onDestroy() {
		unregisterReceiver(myBroadcastReceiver);
		super.onDestroy();
	}
    
	public static final String ACTION_SHOW_MENU="action_show_menu";
	public static final String ACTION_HIDE_MENU="action_hide_menu";
	private BroadcastReceiver myBroadcastReceiver=new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			if(ACTION_SHOW_MENU.equals(action)){
				showMenu();
			}else if(ACTION_HIDE_MENU.equals(action)){
				hideMenu();
			}
		}
	};
    
	private static int[] images={
		R.drawable.del_btn_collect,
		R.drawable.del_btn_collect,
		R.drawable.del_btn_collect,
		R.drawable.del_btn_collect,
		R.drawable.del_btn_collect,
		R.drawable.del_btn_collect,
		R.drawable.del_btn_collect,
		R.drawable.del_btn_collect,
	};
	
	private static String[] descriptions={
		"通讯录",
		"评论",
		"收藏",
		"分享记录",
		"邀请好友",
		"活动",
		"设置",
		"保险箱"
	};
	class GridViewAdapter extends BaseAdapter{
		private LayoutInflater inflater;
		public GridViewAdapter(Context context){
			inflater=LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public Object getItem(int position) {
			return images[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			viewHolder holder;
			if(null==convertView){
				holder=new viewHolder();
				convertView=inflater.inflate(R.layout.include_btn_slidingbar, null);
				holder.ivPic=(ImageView) convertView.findViewById(R.id.iv_pic);
				holder.tvDes= (TextView) convertView.findViewById(R.id.tv_des);
				convertView.setTag(holder);
			}else{
				holder=(viewHolder) convertView.getTag();
			}
			holder.ivPic.setImageResource(images[position]);
			holder.tvDes.setText(descriptions[position]);
			return convertView;
		}
	}
	
	static class viewHolder{
		TextView tvDes;
		ImageView ivPic;
	}
}
