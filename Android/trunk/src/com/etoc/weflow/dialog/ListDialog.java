package com.etoc.weflow.dialog;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.ViewUtils;

public class ListDialog extends Dialog {

	private LayoutInflater inflater;
	private View view;
	private TextView tvTitle;
	private ListView lvListView;
	
	private TextView tvShowType;
	private List<String> listStr;
	private int curSelector = 0;
	
	public ListDialog(Context context,TextView tvType) {
		super(context,R.style.CmmobiDialog);
		// TODO Auto-generated constructor stub
		init();
		this.tvShowType = tvType;
	}
	
	private void init() {
		inflater=LayoutInflater.from(getContext());
		view=inflater.inflate(R.layout.listdialog, null);
		setContentView(view);
		
		ViewUtils.setMarginLeft(view, 32);
		ViewUtils.setMarginRight(view, 32);
		lvListView = (ListView) view.findViewById(R.id.list_view);
		
		lvListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				tvShowType.setText(listStr.get(position));
				curSelector = position;
				ListDialog.this.dismiss();
			}
		});
	}
	
	public int getCurSelector() {
		return curSelector;
	}
	
	public void setDate(List<String> list) {
		DialogAdapter adapter = new DialogAdapter(getContext(), list);
		listStr = list;
		lvListView.setAdapter(adapter);
	}
	
	public void setDate(List<String> list, List<Integer> listIcon) {
		DialogAdapter adapter = new DialogAdapter(getContext(), list, listIcon);
		listStr = list;
		lvListView.setAdapter(adapter);
	}
	
	public void setTitle(String title) {
		tvTitle.setText(title);
	}
	
	
	class DialogAdapter extends BaseAdapter {
		private List<String> listStr;
		private List<Integer> listIcon;
		private Context context;
		public DialogAdapter(Context context,List<String> listStr) {
			this.context = context;
			this.listStr = listStr;
		}
		
		public DialogAdapter(Context context,List<String> listStr, List<Integer> listIcon) {
			this.context = context;
			this.listStr = listStr;
			this.listIcon = listIcon;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listStr.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
//			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.item_list_dialog_layout, null);
				holder = new ViewHolder();
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
				holder.tvName.setTextSize(DisplayUtil.textGetSizeSp(context, 36));
				ViewUtils.setMarginTop(holder.tvName, 30);
				ViewUtils.setMarginTop(convertView.findViewById(R.id.view_line), 30);
				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//			}
			holder.tvName.setText(listStr.get(position));
			if(listIcon!=null && listIcon.size()==listStr.size()){
				holder.tvName.setCompoundDrawablesWithIntrinsicBounds(listIcon.get(position), 0, 0, 0);
				holder.tvName.setCompoundDrawablePadding(0);
			}
			return convertView;
		}
		
	}
	
	class ViewHolder {
		TextView tvName;
	}

}
