package com.cmmobi.railwifi.fragment;

import java.util.List;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.adapter.CollectionListAdapter;
import com.cmmobi.railwifi.dao.Fav;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.sql.FavManager;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.DisplayUtil;

/**
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2014-12-01
 */
public class UserCollectionFragment extends Fragment implements Callback{

	private View contentView;
	private Handler handler;

	private ListView lv_data;
	private RelativeLayout ll_empty;
	private TextView tv_empty;
	
	private CollectionListAdapter mListAdapter;
	private List<Fav> listItems;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		contentView = inflater.inflate(
				R.layout.fragment_user_collection, null);	
		return contentView;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) throws OutOfMemoryError{
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		handler = new Handler(this);
		
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
		mListAdapter = new CollectionListAdapter(getActivity());
		
		lv_data.setAdapter(mListAdapter);
		
		lv_data.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
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
	public void onResume(){
		super.onResume();
		listItems = FavManager.getInstance().getAllFavList();
		mListAdapter.setData(listItems);
		mListAdapter.notifyDataSetChanged();
		
		if(listItems!=null && listItems.size()>0){
			lv_data.setVisibility(View.VISIBLE);
			ll_empty.setVisibility(View.GONE);
		}else{
			lv_data.setVisibility(View.GONE);
			ll_empty.setVisibility(View.VISIBLE);
			tv_empty.setText(R.string.empty_fav_descrp);
		}
	}
	
	@Override
	public void onHiddenChanged(boolean hidden){
		super.onHiddenChanged(hidden);
		if(!hidden){
			CmmobiClickAgentWrapper.onEvent(getActivity(), "p_inf_tab", "1");
			listItems = FavManager.getInstance().getAllFavList();
			mListAdapter.setData(listItems);
			mListAdapter.notifyDataSetChanged();
			if(listItems!=null && listItems.size()>0){
				lv_data.setVisibility(View.VISIBLE);
				ll_empty.setVisibility(View.GONE);
			}else{
				lv_data.setVisibility(View.GONE);
				ll_empty.setVisibility(View.VISIBLE);
				tv_empty.setText(R.string.empty_fav_descrp);
			}
		}
		Log.v(getTag(), "onHiddenChanged - " + hidden);

		
	}


	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
}
