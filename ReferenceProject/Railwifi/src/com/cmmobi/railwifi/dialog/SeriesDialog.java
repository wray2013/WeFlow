package com.cmmobi.railwifi.dialog;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.cmmobi.railwifi.MainApplication;
import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.activity.MovieDetailActivity;
import com.cmmobi.railwifi.adapter.SeriesAdapter;
import com.cmmobi.railwifi.download.DownloadEvent;
import com.cmmobi.railwifi.download.DownloadManager;
import com.cmmobi.railwifi.download.DownloadStatus;
import com.cmmobi.railwifi.download.DownloadType;
import com.cmmobi.railwifi.download.SeriesItem;
import com.cmmobi.railwifi.network.GsonResponseObject.mediaDetailInfoResp;
import com.cmmobi.railwifi.network.GsonResponseObject.sohuMovieResp;
import com.cmmobi.railwifi.utils.DisplayUtil;

import de.greenrobot.event.EventBus;

public class SeriesDialog extends Dialog implements View.OnClickListener, OnItemClickListener{
	private ImageView btnCancel;
	private GridView gvContent;
//	private SeriesDownloadInterface seriesListener;
	private SeriesAdapter seriesAdapter;
	private sohuMovieResp curSohuDetail;
	private mediaDetailInfoResp curMediaDetail;
	
	private SeriesDialog(Context context) {
		this(context,R.style.CmmobiDialog);
	}
	
	private SeriesDialog(Context context,int style) {
		super(context,style);
		initView();
	}
	
	private void initView(){
		
		View view = LayoutInflater.from(MainApplication.getAppInstance()).inflate(R.layout.dialog_series, null);
		btnCancel=(ImageView) view.findViewById(R.id.btn_cancel);
		
		
		gvContent = (GridView)view.findViewById(R.id.gv_content);
		
		btnCancel.setOnClickListener(this);
		
		EventBus.getDefault().register(this);
		
		setContentView(view);
		
		seriesAdapter = new SeriesAdapter(getContext());
		gvContent.setAdapter(seriesAdapter);
		gvContent.setOnItemClickListener(this);
		
	}

	public void setData(sohuMovieResp curSohuDetail, mediaDetailInfoResp curMediaDetail) {
		// TODO Auto-generated method stub
		this.curSohuDetail = curSohuDetail;
		this.curMediaDetail = curMediaDetail;
		
		List<SeriesItem> list = MovieDetailActivity.convertSohu2Series(curSohuDetail, curMediaDetail);
		
		seriesAdapter.setData(list);
	}
	
	public void onEventMainThread(DownloadEvent event) {
		if(event==DownloadEvent.STATUS_CHANGED){
			if(seriesAdapter!=null){				
				List<SeriesItem> list = MovieDetailActivity.convertSohu2Series(curSohuDetail, curMediaDetail);
				seriesAdapter.setData(list);
			}

		}
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btn_cancel:
			
			this.dismiss();
			
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		SeriesItem item = seriesAdapter.getItem(position);
		
		if(item==null){
			return;
		}
		
		item.choose = true;
		
		DownloadManager.getInstance().addDownloadTask(item.url, item.media_id, item.title, item.picUrl, item.detail, DownloadType.MOVIE, item.source, item.data);
		
		seriesAdapter.notifyDataSetChanged();
		
	}
	
	public static class Builder {
		
		private SeriesDialog vdialog;
		
        public Builder(Context context) {
        	vdialog=new SeriesDialog(context);
        }

        public Builder(Context context, int theme) {
        	vdialog=new SeriesDialog(context,theme);
        }
        

        /**
         * Sets whether the dialog is cancelable or not.  Default is true.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setCancelable(boolean cancelable) {
        	vdialog.setCancelable(cancelable);
        	if(cancelable){
        		vdialog.setCanceledOnTouchOutside(true);
        	}
            return this;
        }
        
        public Builder setData(sohuMovieResp curSohuDetail, mediaDetailInfoResp curMediaDetail){
        	
        	
        	vdialog.setData(curSohuDetail, curMediaDetail);
        	
        	return this;
        }
        


        /**
         * Creates a {@link AlertDialog} with the arguments supplied to this builder. It does not
         * {@link Dialog#show()} the dialog. This allows the user to do any extra processing
         * before displaying the dialog. Use {@link #show()} if you don't have any other processing
         * to do and want this to be created and displayed.
         */
        public SeriesDialog create() {            
            return vdialog;
        }

        /**
         * Creates a {@link AlertDialog} with the arguments supplied to this builder and
         * {@link Dialog#show()}'s the dialog.
         */
        public SeriesDialog show() {
        	SeriesDialog dialog = create();

            WindowManager.LayoutParams params = dialog.getWindow().getAttributes(); 
            params.width = DisplayUtil.getSize(MainApplication.getAppInstance(), 680); 
//            params.verticalMargin = DisplayUtil.getSize(MainApplication.getAppInstance(), 280); 
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;//DisplayUtil.getSize(MainApplication.getAppInstance(), 658); 
//            params.height = DisplayUtil.getSize(MainApplication.getAppInstance(), 658); 
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setGravity(Gravity.CENTER);
            
            dialog.show();
            return dialog;
        }
	}




}
