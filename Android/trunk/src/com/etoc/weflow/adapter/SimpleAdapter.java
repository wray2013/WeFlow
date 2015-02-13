package com.etoc.weflow.adapter;

import java.util.Locale;
import com.etoc.weflow.R;
import com.handmark.pulltorefresh.library.PinnedSectionListView.PinnedSectionListAdapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class SimpleAdapter extends ArrayAdapter<Object> implements PinnedSectionListAdapter {

    private static final int[] COLORS = new int[] {
        R.color.green, R.color.red,
        R.color.blue, R.color.orange };

    public SimpleAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        generateDataset('A', 'H', false);
    }

    public void generateDataset(char from, char to, boolean clear) {
    	
    	if (clear) clear();
    	
        final int sectionsNumber = to - from + 1;
        prepareSections(sectionsNumber);

        int sectionPosition = 0, listPosition = 0;
        for (char i=0; i<sectionsNumber; i++) {
            SimpleItem section = new SimpleItem(SimpleItem.SECTION, String.valueOf((char)('A' + i)) + "类活动");
            section.sectionPosition = sectionPosition;
            section.listPosition = listPosition++;
            onSectionAdded(section, sectionPosition);
            add(section);

            final int itemsNumber = (int) Math.abs((Math.cos(2f*Math.PI/3f * sectionsNumber / (i+1f)) * 25f));
            for (int j=0;j<itemsNumber;j++) {
                SimpleItem item = new SimpleItem(SimpleItem.ITEM, section.text.toUpperCase(Locale.ENGLISH) + " - " + j);
                item.sectionPosition = sectionPosition;
                item.listPosition = listPosition++;
                add(item);
            }

            sectionPosition++;
        }
    }
    
    protected void prepareSections(int sectionsNumber) { }
    protected void onSectionAdded(SimpleItem section, int sectionPosition) { }

    @Override 
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTextColor(Color.WHITE);
        view.setTag("" + position);
        SimpleItem item = (SimpleItem) getItem(position);
        if (item.type == SimpleItem.SECTION) {
            //view.setOnClickListener(PinnedSectionListActivity.this);
            view.setBackgroundColor(parent.getResources().getColor(COLORS[item.sectionPosition % COLORS.length]));
        }
        return view;
    }

    @Override 
    public int getViewTypeCount() {
        return 2;
    }

    @Override 
    public int getItemViewType(int position) {
        return ((SimpleItem)getItem(position)).type;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == SimpleItem.SECTION;
    }

	public static class SimpleItem {

		public static final int ITEM = 0;
		public static final int SECTION = 1;

		public final int type;
		public final String text;

		public int sectionPosition;
		public int listPosition;

		public SimpleItem(int type, String text) {
		    this.type = type;
		    this.text = text;
		}

		@Override public String toString() {
			return text;
		}

	}
}