package com.cmmobi.railwifi.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.railwifi.Config;
import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.activity.CmmobiVideoPlayer;
import com.cmmobi.railwifi.activity.MovieDetailActivity;
import com.cmmobi.railwifi.activity.VideoPlayerActivity;
import com.cmmobi.railwifi.adapter.PlayHistoryListAdapter;
import com.cmmobi.railwifi.dao.PlayHistory;
import com.cmmobi.railwifi.sql.FavManager;
import com.cmmobi.railwifi.sql.HistoryManager;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.ConStant;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.google.gson.Gson;

/**
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2014-12-01
 */
public class UserHistoryFragment extends Fragment implements Callback{

	private View contentView;
	private Handler handler;

	private PlayHistoryListAdapter mListAdapter;
	private List<PlayHistory> listItems;
	private View div_line;
	private ListView lv_data;
	private RelativeLayout ll_empty;
	private TextView tv_empty;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		contentView = inflater.inflate(
				R.layout.fragment_user_history, null);

		return contentView;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		handler = new Handler(this);
		
		div_line = (View)contentView.findViewById(R.id.view_line_top);
		lv_data = (ListView) contentView.findViewById(R.id.lv_data);
		ll_empty = (RelativeLayout) contentView.findViewById(R.id.ll_empty);
		ll_empty.setPadding(0, 0, 0, DisplayUtil.getSize(getActivity(), 60));
		tv_empty = (TextView) contentView.findViewById(R.id.tv_empty);
		tv_empty.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 48));
		tv_empty.setPadding(0, 0, 0, DisplayUtil.getSize(getActivity(), 24));
		try{
			((ImageView)contentView.findViewById(R.id.iv_empty)).setImageResource(R.drawable.image_no_content);
			}catch(Error e){
				e.printStackTrace();
			}
		mListAdapter = new PlayHistoryListAdapter(getActivity());
		listItems = HistoryManager.getInstance().getAllPlayHistoryList();
		
		mListAdapter.setData(listItems);
		mListAdapter.notifyDataSetChanged();
		lv_data.setAdapter(mListAdapter);
		
		lv_data.setOnItemClickListener(new OnItemClickListener() {

			private Gson gson = new Gson();

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
				PlayHistory ph = (PlayHistory) parent.getItemAtPosition(position);
				if(ph==null){
					return;
				}

				if(Config.IS_USE_COMMOBI_VIDEOVIEW){
					Intent intent2 = new Intent(getActivity(), CmmobiVideoPlayer.class);
					intent2.putExtra(VideoPlayerActivity.KEY_PLAYHISTORY, gson.toJson(ph));
					intent2.putExtra(VideoPlayerActivity.KEY_MOVIE_TYPE, "1");
					startActivity(intent2);
				}else{
					Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
					intent.putExtra(VideoPlayerActivity.KEY_PLAYHISTORY, gson.toJson(ph));
					intent.putExtra(VideoPlayerActivity.KEY_MOVIE_TYPE, "1");
					startActivity(intent);
				}


			}
		});
		
		lv_data.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
								
				return false;
			}
		});
	}


	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		listItems = HistoryManager.getInstance().getAllPlayHistoryList();
		mListAdapter.setData(listItems);
		mListAdapter.notifyDataSetChanged();
		
		if(listItems!=null && listItems.size()>0){
			div_line.setVisibility(View.VISIBLE);
			lv_data.setVisibility(View.VISIBLE);
			ll_empty.setVisibility(View.GONE);
		}else{
			div_line.setVisibility(View.GONE);
			lv_data.setVisibility(View.GONE);
			ll_empty.setVisibility(View.VISIBLE);
			tv_empty.setText(R.string.empty_history_descrp);
		}
	}
	
	@Override
	public void onHiddenChanged(boolean hidden){
		super.onHiddenChanged(hidden);
		if(!hidden){
			CmmobiClickAgentWrapper.onEvent(getActivity(), "p_inf_tab", "2");
			listItems = HistoryManager.getInstance().getAllPlayHistoryList();
			mListAdapter.setData(listItems);
			mListAdapter.notifyDataSetChanged();
			if(listItems!=null && listItems.size()>0){
				div_line.setVisibility(View.VISIBLE);
				lv_data.setVisibility(View.VISIBLE);
				ll_empty.setVisibility(View.GONE);
			}else{
				div_line.setVisibility(View.GONE);
				lv_data.setVisibility(View.GONE);
				ll_empty.setVisibility(View.VISIBLE);
				tv_empty.setText(R.string.empty_history_descrp);
			}
		}
		Log.v(getTag(), "onHiddenChanged - " + hidden);

		
	}
}
