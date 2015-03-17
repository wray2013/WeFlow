package com.etoc.weflow.fragment;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.adapter.RechargeAdapter;
import com.etoc.weflow.dao.DaoMaster;
import com.etoc.weflow.dao.DaoMaster.DevOpenHelper;
import com.etoc.weflow.dao.DaoSession;
import com.etoc.weflow.dao.FrequentQQ;
import com.etoc.weflow.dao.FrequentQQDao;
import com.etoc.weflow.dao.FrequentQQDao.Properties;
import com.etoc.weflow.net.GsonResponseObject;
import com.etoc.weflow.net.GsonResponseObject.RechargePhoneResp;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.ViewUtils;

import de.greenrobot.dao.query.QueryBuilder;

public class RechargeQQFragment extends Fragment implements OnClickListener{

	private View mView;
	GridView gvQQMenu = null;
	RechargeAdapter adapter = null;
	TextView tvCostCoins = null;
	List<RechargePhoneResp> itemList = new ArrayList<GsonResponseObject.RechargePhoneResp>();
	TextView tvCommit = null;
	private FrequentQQDao qqDao;
	private EditText etQQ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(WeFlowApplication.getAppInstance(), "weflowdb", null);
		SQLiteDatabase db = helper.getWritableDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
        qqDao = daoSession.getFrequentQQDao();
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
		View v = inflater.inflate(R.layout.fragment_recharge_qq, null);
		initView(v);
		mView = v;
		return v;
	}
	
	private void initView(View view) {
		gvQQMenu = (GridView) view.findViewById(R.id.gv_menu);
		
		int [] moneys = {1,5,10,20,50,100};
		
		if (itemList.size() == 0) {
			for (int i = 0;i < 6;i++) {
				RechargePhoneResp resp = new RechargePhoneResp();
				resp.chargesid = "" + i;
				resp.money = moneys[i] + "Q币";
				resp.cost = moneys[i] * 100 + "";
				itemList.add(resp);
			}
		}
		
		adapter = new RechargeAdapter(getActivity(), itemList);
		gvQQMenu.setAdapter(adapter);
		
		gvQQMenu.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		gvQQMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				// TODO Auto-generated method stub
				adapter.setSelect(position);
				adapter.notifyDataSetChanged();
				tvCostCoins.setText(adapter.getSelectCost());
			}
		});
		
		tvCostCoins = (TextView) view.findViewById(R.id.tv_cost_coins);
		ViewUtils.setMarginRight(tvCostCoins, 16);
		tvCostCoins.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 32));
		tvCostCoins.setText(adapter.getSelectCost());
		
		tvCommit = (TextView) view.findViewById(R.id.tv_btn_order);
		tvCommit.setOnClickListener(this);
		ViewUtils.setSize(tvCommit, 552, 96);
		ViewUtils.setMarginBottom(tvCommit, 68);
		tvCommit.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 32));
		
		etQQ = (EditText) view.findViewById(R.id.et_qq);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.tv_btn_order:
			QueryBuilder<FrequentQQ> build = qqDao.queryBuilder();
			build.where(Properties.Qq_num.eq(etQQ.getText().toString()));
			if (build.buildCount().count() ==0) {
				Log.d("=AAA=","buildCount = 0");
				qqDao.insert(new FrequentQQ(etQQ.getText().toString()));
			}
			break;
		}
	}
}
