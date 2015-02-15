package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * 可进行特殊处理的adapter，特殊处理需设置 监听  setPeculiarListener（）;
 * @author guoyang
 *
 */
public class CmmobiAdapter extends SimpleAdapter{
    private int[] mTo;
    private String[] mFrom;
    private ViewBinder mViewBinder;
    private List<? extends Map<String, ?>> mData;
    private int mResource;
    private int mDropDownResource;
    private LayoutInflater mInflater;
    private ArrayList<Integer> peculiarView;
    private AdapterPeculiarListener peculiarListener;
    private int defulatDrawableId = -1;
    private Context mContext;
    
    public CmmobiAdapter(Context context, List<? extends Map<String, ?>> data,
            int resource, String[] from, int[] to) {
    	super(context, data, resource, from, to);
    	mContext = context;
        mData = data;
        mResource = mDropDownResource = resource;
        mFrom = from;
        mTo = to;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    /**
     * 设置特殊处理监听和需特殊处理的视图id。
     * @param apl 处理特殊事件的监听
     * @param viewID 需特殊处理的视图id
     */
    public void setPeculiarListener(AdapterPeculiarListener apl,Integer... viewID){
    	peculiarListener = apl;
    	peculiarView = new ArrayList<Integer>(Arrays.asList(viewID));
    }
    
    
    public int getDefulatDrawableId() {
		return defulatDrawableId;
	}

	public void setDefulatDrawableId(int defulatDrawableId) {
		this.defulatDrawableId = defulatDrawableId;
	}

	public int getCount() {
        return mData.size();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }

    private View createViewFromResource(int position, View convertView,
            ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(resource, parent, false);

            final int[] to = mTo;
            final int count = to.length;
            final View[] holder = new View[count];

            for (int i = 0; i < count; i++) {
                holder[i] = v.findViewById(to[i]);
            }

            v.setTag(holder);
        } else {
            v = convertView;
        }

        bindView(position, v);

        return v;
    }


    public void setDropDownViewResource(int resource) {
        this.mDropDownResource = resource;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mDropDownResource);
    }

    private void bindView(int position, View view) {
        final Map<String,?> dataSet = mData.get(position);
        if (dataSet == null) {
            return;
        }

        final ViewBinder binder = mViewBinder;
        final View[] holder = (View[]) view.getTag();
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = holder[i];
            if (v != null) {
                final Object data = dataSet.get(from[i]);
                if(peculiarView != null && peculiarView.contains(v.getId())){
                	peculiarListener.handlePeculiarView(v,data,view,dataSet,position);
                	continue;
                }
                String text = data == null ? "" : data.toString();
                if (text == null) {
                    text = "";
                }

                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text);
                }

                if (!bound) {
                    if (v instanceof Checkable) {
                        if (data instanceof Boolean) {
                            ((Checkable) v).setChecked((Boolean) data);
                        } else {
                            throw new IllegalStateException(v.getClass().getName() +
                                    " should be bound to a Boolean, not a " + data.getClass());
                        }
                    } else if (v instanceof TextView) {
                        // Note: keep the instanceof TextView check at the bottom of these
                        // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                        setViewText((TextView) v, text);
                    } else if (v instanceof ImageView) {
                        if (data instanceof Integer) {
                            setViewImage((ImageView) v, (Integer) data);                            
                        } else {
                        	setViewImage((ImageView) v, (String)data);                            
                        }
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a " +
                                " view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
    }


    public ViewBinder getViewBinder() {
        return mViewBinder;
    }


    public void setViewBinder(ViewBinder viewBinder) {
        mViewBinder = viewBinder;
    }


    public void setViewImage(ImageView v, int value) {
        v.setImageResource(value);
    }

    private DisplayImageOptions options;

    public void setViewImage(ImageView v, String url) {
    	if(options == null){
    		options = new DisplayImageOptions.Builder()
				.cacheInMemory()
				.cacheOnDisc()
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(500))
				.build();
    	}
    	ImageLoader.getInstance().displayImage(url, v, options, ActiveAccount.getInstance(mContext).getUID(), 1);
    }

    public void setViewText(TextView v, String text) {
        v.setText(text);
    }
    
    
    public interface AdapterPeculiarListener {

    	
    	/**
    	 * 处理listView里需要特殊处理的视图
    	 * @param v 需要特殊处理的视图
    	 * @param data 需要特殊处理的视图所对应的数据
    	 * @param parentView 特殊处理视图 所对应的整个条目的视图
    	 * @param dataSet 特殊处理视图所处条目的所有数据
    	 * @param position 特殊处理视图所处条目在listview中的位置。
    	 */
    	public void handlePeculiarView(View v,Object data,View parentView,Map<String, ?> dataSet,int position);

    }
    
}

